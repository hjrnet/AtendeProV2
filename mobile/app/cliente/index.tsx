import { Link } from "expo-router";
import { useEffect, useState } from "react";
import { StyleSheet, View } from "react-native";
import {
  CapaPagina,
  CabecalhoSecao,
  Cartao,
  EstadoVazio,
  GridCards
} from "@/components/ui-shell";
import { carregarSessaoAutenticada } from "@/lib/auth";
import {
  consultarPlanoPublicadoNutri,
  listarAgendaPortal,
  listarDocumentosPortal,
  listarMensagensNutri,
  resolverPrimeiroPacienteNutri
} from "@/lib/api/client";

type ContagemCliente = {
  agenda: number;
  documentos: number;
  mensagens: number;
  planoPublicado: boolean;
};

const secoesCliente = [
  {
    titulo: "Agenda do cliente",
    rota: "/cliente/agenda",
    descricao: "Acompanhe compromissos e protocolo do dia."
  },
  {
    titulo: "Documentos",
    rota: "/cliente/documentos",
    descricao: "Consulte e organize prescrições e laudos."
  },
  {
    titulo: "Diário e recados",
    rota: "/cliente/diario",
    descricao: "Histórico de evolução e observações recentes."
  },
  {
    titulo: "Mensagens",
    rota: "/cliente/mensagens",
    descricao: "Comunicação direta com os profissionais."
  }
];

export default function AreaClienteMobile() {
  const [contagens, setContagens] = useState<ContagemCliente>({ agenda: 0, documentos: 0, mensagens: 0, planoPublicado: false });
  const [empresaId, setEmpresaId] = useState<string | null>(null);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    let ativo = true;

    const carregarIndicadores = async () => {
      setErro("");
      setCarregando(true);

      try {
        const sessao = await carregarSessaoAutenticada();
        const contextoEmpresa = sessao?.usuario.empresaId ?? null;
        setEmpresaId(contextoEmpresa);

        const pacienteNutri = await resolverPrimeiroPacienteNutri(contextoEmpresa);
        const [agendaResposta, documentosResposta, mensagensResposta] = await Promise.all([
          listarAgendaPortal({ empresaId: contextoEmpresa, tamanho: 10, pagina: 0 }),
          listarDocumentosPortal({ empresaId: contextoEmpresa, tamanho: 20, pagina: 0 }),
          pacienteNutri ? listarMensagensNutri({ empresaId: contextoEmpresa, pacienteId: pacienteNutri.id }) : Promise.resolve({ itens: [] })
        ]);

        let planoPublicado = false;
        if (pacienteNutri) {
          try {
            await consultarPlanoPublicadoNutri({ empresaId: contextoEmpresa, pacienteId: pacienteNutri.id });
            planoPublicado = true;
          } catch {
            planoPublicado = false;
          }
        }

        if (!ativo) {
          return;
        }

        setContagens({
          agenda: agendaResposta.totalItens,
          documentos: documentosResposta.totalItens,
          mensagens: mensagensResposta.itens.filter((mensagem) => !mensagem.lidaPeloPaciente).length,
          planoPublicado
        });
      } catch (falha) {
        if (ativo) {
          setErro(falha instanceof Error ? falha.message : "Não foi possível carregar indicadores.");
        }
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    };

    void carregarIndicadores();

    return () => {
      ativo = false;
    };
  }, []);

  return (
    <CapaPagina titulo="Área do Cliente" subtitulo="Informações rápidas para o dia do paciente.">
      <CabecalhoSecao titulo="Resumo rápido" />
      {carregando ? (
        <EstadoVazio texto="Carregando indicadores..." />
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : (
        <Cartao
          titulo={`Empresa: ${empresaId ?? "sem contexto"}`}
          descricao="Indicadores carregados do backend."
        />
      )}
      <GridCards>
        <Cartao titulo={`Agenda: ${contagens.agenda} compromissos`} descricao="Agendamentos ativos no período consultado." />
        <Cartao titulo={`Documentos: ${contagens.documentos}`} descricao="Documentos profissionais recentes." />
        <Cartao
          titulo={`Mensagens: ${contagens.mensagens}`}
          descricao="Recados Nutri não lidos no acompanhamento."
        />
        <Cartao
          titulo={contagens.planoPublicado ? "Plano ativo publicado" : "Plano ainda não publicado"}
          descricao="Status do plano alimentar disponível no portal/app."
        />
      </GridCards>

      <CabecalhoSecao titulo="Módulos" />
      <View style={estilos.grade}>
        {secoesCliente.map((secao) => (
          <Link key={secao.rota} href={secao.rota}>
            <Cartao titulo={secao.titulo} descricao={secao.descricao} />
          </Link>
        ))}
      </View>
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  grade: {
    gap: 12
  }
});
