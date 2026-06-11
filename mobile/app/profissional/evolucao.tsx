import { useEffect, useMemo, useState } from "react";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";
import {
  CapaPagina,
  Cartao,
  CabecalhoSecao,
  EstadoVazio,
  ItemLista,
  temaAtendePro
} from "@/components/ui-shell";
import { carregarSessaoAutenticada } from "@/lib/auth";
import {
  listarAgendaPortal,
  listarClientesPortal,
  listarDocumentosPortal,
  type ClientePacienteApi,
  type CompromissoAgendaApi,
  type DocumentoProfissionalApi
} from "@/lib/api/client";

type PacienteAcompanhamentoReal = {
  cliente: ClientePacienteApi;
  proximaAgenda: CompromissoAgendaApi | null;
  documentos: DocumentoProfissionalApi[];
};

function formatarData(valor: string | null) {
  if (!valor) {
    return "Sem data";
  }

  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(valor));
}

function statusPaciente(item: PacienteAcompanhamentoReal) {
  if (!item.proximaAgenda) {
    return "Sem retorno futuro";
  }

  if (item.documentos.length > 0) {
    return "Com documentos ativos";
  }

  return "Acompanhamento ativo";
}

function descricaoPaciente(item: PacienteAcompanhamentoReal) {
  if (!item.proximaAgenda) {
    return "Priorizar contato de retorno ou revisão de acompanhamento.";
  }

  return `Próximo compromisso em ${formatarData(item.proximaAgenda.inicio)}.`;
}

export default function EvolucaoProfissionalMobile() {
  const [pacientes, setPacientes] = useState<PacienteAcompanhamentoReal[]>([]);
  const [empresaId, setEmpresaId] = useState<string | null>(null);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let ativo = true;

    const carregar = async () => {
      setCarregando(true);
      setErro("");

      try {
        const sessao = await carregarSessaoAutenticada();
        const contextoEmpresa = sessao?.usuario.empresaId ?? null;
        setEmpresaId(contextoEmpresa);

        const [clientesResposta, agendaResposta, documentosResposta] = await Promise.all([
          listarClientesPortal({ empresaId: contextoEmpresa, ativo: true, tamanho: 50, pagina: 0 }),
          listarAgendaPortal({ empresaId: contextoEmpresa, tamanho: 80, pagina: 0 }),
          listarDocumentosPortal({ empresaId: contextoEmpresa, ativo: true, tamanho: 80, pagina: 0 })
        ]);

        if (!ativo) {
          return;
        }

        const agora = Date.now();
        const clientes = clientesResposta.itens ?? [];
        const agenda = (agendaResposta.itens ?? []).filter((item) => item.status !== "CANCELADO");
        const documentos = documentosResposta.itens ?? [];

        setPacientes(
          clientes.slice(0, 20).map((cliente) => {
            const proximaAgenda = agenda
              .filter((item) => item.clientePacienteId === cliente.id && new Date(item.inicio).getTime() >= agora)
              .sort((a, b) => new Date(a.inicio).getTime() - new Date(b.inicio).getTime())[0] ?? null;
            const documentosCliente = documentos.filter((documento) => documento.clientePacienteId === cliente.id);
            return { cliente, proximaAgenda, documentos: documentosCliente };
          })
        );
      } catch (falha) {
        if (ativo) {
          setErro(falha instanceof Error ? falha.message : "Não foi possível carregar acompanhamento real.");
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

  const resumo = useMemo(() => {
    const semRetorno = pacientes.filter((item) => !item.proximaAgenda).length;
    const comDocumento = pacientes.filter((item) => item.documentos.length > 0).length;
    return { semRetorno, comDocumento };
  }, [pacientes]);

  return (
    <CapaPagina titulo="Acompanhamento" subtitulo={`Carteira real da empresa ${empresaId ?? "sem contexto"}.`}>
      {carregando ? (
        <View style={estilos.estadoCarregamento}>
          <ActivityIndicator size="small" color={temaAtendePro.destaque} />
          <Text style={estilos.textoCarregamento}>Carregando carteira real...</Text>
        </View>
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : null}

      <CabecalhoSecao titulo="Pacientes e clientes ativos" />
      <View style={estilos.lista}>
        {!carregando && !erro && pacientes.length === 0 ? <EstadoVazio texto="Nenhum cliente ativo encontrado para a empresa." /> : null}
        {pacientes.map((item) => (
          <ItemLista
            key={item.cliente.id}
            titulo={item.cliente.nome}
            meta={`${item.cliente.area} · ${item.cliente.telefone ?? "sem telefone"}`}
            descricao={descricaoPaciente(item)}
            status={statusPaciente(item)}
          />
        ))}
      </View>

      <CabecalhoSecao titulo="Observações de gestão" />
      <Cartao titulo="Indicador clínico-operacional" descricao="Priorize pacientes sem retorno futuro e com documentos pendentes de acompanhamento.">
        <ItemLista
          titulo="Sem retorno futuro"
          descricao={`${resumo.semRetorno} clientes ativos sem próximo compromisso localizado.`}
          meta="Revisão recomendada hoje"
        />
        <ItemLista
          titulo="Com documentos ativos"
          descricao={`${resumo.comDocumento} clientes possuem documentos vinculados para continuidade.`}
          meta="Base real de documentos profissionais"
        />
      </Cartao>
    </CapaPagina>
  );
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
  }
});
