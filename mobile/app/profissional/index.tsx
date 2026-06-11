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
  listarAgendaPortal,
  listarMensagensNutri,
  resolverPrimeiroPacienteNutri
} from "@/lib/api/client";

type ContagemProfissional = {
  agendaHoje: number;
  agendaTotal: number;
  mensagensPendentes: number;
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
    descricao: "Mensagens reais do acompanhamento Nutri."
  },
  {
    titulo: "Acompanhamento",
    rota: "/profissional/evolucao",
    descricao: "Carteira ativa com dados reais de clientes, agenda e documentos."
  }
];

export default function AreaProfissionalMobile() {
  const [contagens, setContagens] = useState<ContagemProfissional>({ agendaHoje: 0, agendaTotal: 0, mensagensPendentes: 0 });
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

        const [agendaResposta, pacienteNutri] = await Promise.all([
          listarAgendaPortal({ empresaId: contextoEmpresa, tamanho: 80, pagina: 0 }),
          resolverPrimeiroPacienteNutri(contextoEmpresa)
        ]);

        const mensagensResposta = pacienteNutri
          ? await listarMensagensNutri({ empresaId: contextoEmpresa, pacienteId: pacienteNutri.id })
          : { itens: [] };

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
          agendaTotal: agendaResposta.totalItens,
          mensagensPendentes: mensagensResposta.itens.filter((mensagem) => !mensagem.lidaPeloProfissional).length
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
    <CapaPagina titulo="Área do Profissional" subtitulo="Comandos principais para operação diária com backend real.">
      <CabecalhoSecao titulo="Resumo operacional" />
      {carregando ? (
        <EstadoVazio texto="Carregando indicadores..." />
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : (
        <Cartao titulo={`Empresa: ${empresaId ?? "sem contexto"}`} descricao="Indicadores carregados com token da sessão mobile." />
      )}
      <GridCards>
        <Cartao titulo={`Hoje: ${contagens.agendaHoje} atendimentos`} descricao="Agenda ativa disponível." />
        <Cartao titulo={`Mensagens: ${contagens.mensagensPendentes}`} descricao="Pendentes de leitura no acompanhamento Nutri." />
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
