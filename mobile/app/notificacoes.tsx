import { useEffect, useState } from "react";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";
import { CapaPagina, CabecalhoSecao, EstadoVazio, ItemLista, temaAtendePro } from "@/components/ui-shell";
import { carregarSessaoAutenticada } from "@/lib/auth";
import {
  consultarPainelPosVenda,
  listarAgendaPortal,
  listarDocumentosPortal,
  listarMensagensNutri,
  resolverPrimeiroPacienteNutri,
  type AreaPosVendaApi
} from "@/lib/api/client";

type NotificacaoMobileReal = {
  id: string;
  titulo: string;
  descricao: string;
  dataHora: string;
  categoria: string;
  urgente: boolean;
};

export default function NotificacoesMobile() {
  const [notificacoes, setNotificacoes] = useState<NotificacaoMobileReal[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let ativo = true;

    const carregar = async () => {
      setCarregando(true);
      setErro("");

      try {
        const sessao = await carregarSessaoAutenticada();
        const empresaId = sessao?.usuario.empresaId ?? null;
        const paciente = await resolverPrimeiroPacienteNutri(empresaId);
        const [agenda, documentos, mensagens, posVendaNutri, posVendaBeauty] = await Promise.all([
          listarAgendaPortal({ empresaId, clientePacienteId: paciente?.id, tamanho: 8, pagina: 0 }),
          listarDocumentosPortal({ empresaId, clientePacienteId: paciente?.id, ativo: true, tamanho: 8, pagina: 0 }),
          paciente ? listarMensagensNutri({ empresaId, pacienteId: paciente.id }) : Promise.resolve({ itens: [] }),
          consultarPainelPosVenda({ empresaId, area: "NUTRI" }).catch(() => null),
          consultarPainelPosVenda({ empresaId, area: "BEAUTY" }).catch(() => null)
        ]);

        if (!ativo) {
          return;
        }

        const itens: NotificacaoMobileReal[] = [
          ...agenda.itens.slice(0, 3).map((item) => ({
            id: `agenda-${item.id}`,
            titulo: "Compromisso agendado",
            descricao: `${item.profissionalNome} · ${item.sala ?? "sem sala"} · ${item.status}`,
            dataHora: formatarData(item.inicio),
            categoria: "agenda",
            urgente: item.status === "CONFIRMADO"
          })),
          ...documentos.itens.slice(0, 3).map((documento) => ({
            id: `documento-${documento.id}`,
            titulo: "Documento disponível",
            descricao: `${documento.titulo} · ${documento.tipo}`,
            dataHora: formatarData(documento.criadoEm),
            categoria: "documento",
            urgente: documento.status === "EMITIDO"
          })),
          ...mensagens.itens.filter((mensagem) => !mensagem.lidaPeloPaciente).slice(0, 4).map((mensagem) => ({
            id: `mensagem-${mensagem.id}`,
            titulo: "Mensagem não lida",
            descricao: mensagem.texto,
            dataHora: formatarData(mensagem.enviadaEm),
            categoria: "mensagem",
            urgente: mensagem.remetenteTipo === "PROFISSIONAL"
          })),
          ...notificacoesPosVenda("NUTRI", posVendaNutri?.tarefas ?? []),
          ...notificacoesPosVenda("BEAUTY", posVendaBeauty?.tarefas ?? [])
        ];
        setNotificacoes(itens.slice(0, 12));
      } catch (falha) {
        if (ativo) {
          setErro(falha instanceof Error ? falha.message : "Não foi possível carregar notificações reais.");
        }
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    };

    void carregar();
    return () => {
      ativo = false;
    };
  }, []);

  const totalNaoLidas = notificacoes.filter((notificacao) => notificacao.urgente).length;

  return (
    <CapaPagina titulo="Notificações" subtitulo="Lembretes do dia e eventos importantes do paciente.">
      <CabecalhoSecao titulo={`${totalNaoLidas} não lidas`} />
      {carregando ? (
        <View style={estilos.estadoCarregamento}>
          <ActivityIndicator size="small" color={temaAtendePro.destaque} />
          <Text style={estilos.textoCarregamento}>Carregando eventos reais...</Text>
        </View>
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : null}
      <View style={estilos.lista}>
        {!carregando && !erro && notificacoes.length === 0 ? <EstadoVazio texto="Nenhum evento importante encontrado agora." /> : null}
        {notificacoes.map((notificacao) => (
          <ItemLista
            key={notificacao.id}
            titulo={`${notificacao.titulo} • ${notificacao.categoria.toUpperCase()}`}
            meta={notificacao.dataHora}
            descricao={notificacao.descricao}
            status={notificacao.urgente ? "Urgente" : "Pendente"}
          />
        ))}
      </View>

      <ItemLista
        titulo="Canal de eventos real"
        descricao="Esta tela já consolida agenda, documentos, mensagens e pós-venda; push nativo fica preparado para o próximo incremento."
        meta="R23"
      />
      <View style={estilos.rodape}>
        <View style={estilos.ponto} />
        <ItemLista
          titulo="Próximo estágio"
          descricao="Criar endpoint mobile/me e canal de eventos push com Expo Notifications."
          meta="Backlog técnico"
        />
      </View>
    </CapaPagina>
  );
}

function notificacoesPosVenda(area: AreaPosVendaApi, tarefas: Array<{ id: string | null; titulo: string; descricao: string | null; dataRecomendada: string | null; status: string }>): NotificacaoMobileReal[] {
  return tarefas
    .filter((tarefa) => tarefa.status === "PENDENTE")
    .slice(0, 3)
    .map((tarefa, indice) => ({
      id: `pos-venda-${area}-${tarefa.id ?? indice}`,
      titulo: `Pós-venda ${area}`,
      descricao: tarefa.descricao ?? tarefa.titulo,
      dataHora: tarefa.dataRecomendada ? formatarData(tarefa.dataRecomendada) : "Sem data",
      categoria: "pos-venda",
      urgente: true
    }));
}

function formatarData(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(valor));
}

const estilos = StyleSheet.create({
  lista: {
    gap: 12
  },
  estadoCarregamento: {
    backgroundColor: temaAtendePro.superficieSecundaria,
    borderColor: temaAtendePro.borda,
    borderWidth: 1,
    borderRadius: 12,
    padding: 12,
    gap: 10
  },
  textoCarregamento: {
    color: temaAtendePro.textoSecundario,
    textAlign: "center"
  },
  rodape: {
    marginTop: 12,
    gap: 10
  },
  ponto: {
    borderRadius: 999,
    width: 8,
    height: 8,
    backgroundColor: temaAtendePro.destaque,
    alignSelf: "center"
  }
});
