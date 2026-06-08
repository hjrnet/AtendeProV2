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
import { listarAgendaPortal } from "@/lib/api/client";
import { mensagensNaoLidas } from "@/features/mensagens/mensagens";

type ContagemProfissional = {
  agendaHoje: number;
  agendaTotal: number;
};

const secoesProfissional = [
  {
    titulo: "Agenda do dia",
    rota: "/profissional/agenda",
    descricao: "Execução de agenda, protocolo e próximos atendimentos."
  },
  {
    titulo: "Mensagens",
    rota: "/profissional/mensagens",
    descricao: "Mensagens de retorno e dúvidas dos pacientes."
  },
  {
    titulo: "Acompanhamento",
    rota: "/profissional/evolucao",
    descricao: "Painel de evolução dos atendidos."
  }
];

export default function AreaProfissionalMobile() {
  const [contagens, setContagens] = useState<ContagemProfissional>({ agendaHoje: 0, agendaTotal: 0 });
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

        const agendaResposta = await listarAgendaPortal({ empresaId: contextoEmpresa, tamanho: 80, pagina: 0 });
        if (!ativo) {
          return;
        }

        const agora = new Date();
        const agendamentosHoje = (agendaResposta.itens ?? []).filter((agenda) => {
          const inicio = new Date(agenda.inicio);
          return (
            inicio.getDate() === agora.getDate() &&
            inicio.getMonth() === agora.getMonth() &&
            inicio.getFullYear() === agora.getFullYear() &&
            agenda.status !== "CANCELADO"
          );
        });

        setContagens({
          agendaHoje: agendamentosHoje.length,
          agendaTotal: agendaResposta.totalItens
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
    <CapaPagina titulo="Área do Profissional" subtitulo="Comandos principais para operação diária.">
      <CabecalhoSecao titulo="Resumo operacional" />
      {carregando ? (
        <EstadoVazio texto="Carregando indicadores..." />
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : (
        <Cartao titulo={`Empresa: ${empresaId ?? "sem contexto"}`} descricao="Indicadores carregados do backend." />
      )}
      <GridCards>
        <Cartao titulo={`Hoje: ${contagens.agendaHoje} atendimentos`} descricao="Agenda ativa disponível." />
        <Cartao titulo={`Mensagens: ${mensagensNaoLidas}`} descricao="Pendentes de resposta no momento." />
        <Cartao titulo={`Pipeline: ${contagens.agendaTotal}`} descricao="Total de compromissos planejados." />
      </GridCards>

      <CabecalhoSecao titulo="Ambientes de trabalho" />
      <View style={estilos.grade}>
        {secoesProfissional.map((secao) => (
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
