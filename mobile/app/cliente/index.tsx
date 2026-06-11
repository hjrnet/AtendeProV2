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
  consultarPainelPosVenda,
  consultarPlanoPublicadoNutri,
  listarAgendaPortal,
  listarDocumentosPortal,
  listarMensagensNutri,
  listarProtocolosBeauty,
  resolverPrimeiroClienteBeauty,
  resolverPrimeiroPacienteNutri
} from "@/lib/api/client";

type ContagemCliente = {
  agenda: number;
  documentos: number;
  mensagens: number;
  planoPublicado: boolean;
  protocolosBeauty: number;
  retornosPendentes: number;
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
  const [contagens, setContagens] = useState<ContagemCliente>({
    agenda: 0,
    documentos: 0,
    mensagens: 0,
    planoPublicado: false,
    protocolosBeauty: 0,
    retornosPendentes: 0
  });
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

        const [pacienteNutri, clienteBeauty] = await Promise.all([
          resolverPrimeiroPacienteNutri(contextoEmpresa),
          resolverPrimeiroClienteBeauty(contextoEmpresa)
        ]);
        const [agendaResposta, documentosResposta, mensagensResposta, posVendaNutri, posVendaBeauty] = await Promise.all([
          listarAgendaPortal({ empresaId: contextoEmpresa, tamanho: 10, pagina: 0 }),
          listarDocumentosPortal({ empresaId: contextoEmpresa, tamanho: 20, pagina: 0 }),
          pacienteNutri ? listarMensagensNutri({ empresaId: contextoEmpresa, pacienteId: pacienteNutri.id }) : Promise.resolve({ itens: [] }),
          consultarPainelPosVenda({ empresaId: contextoEmpresa, area: "NUTRI" }).catch(() => null),
          consultarPainelPosVenda({ empresaId: contextoEmpresa, area: "BEAUTY" }).catch(() => null)
        ]);

        let planoPublicado = false;
        let protocolosBeauty = 0;
        if (pacienteNutri) {
          try {
            await consultarPlanoPublicadoNutri({ empresaId: contextoEmpresa, pacienteId: pacienteNutri.id });
            planoPublicado = true;
          } catch {
            planoPublicado = false;
          }
        }
        if (clienteBeauty) {
          try {
            const protocolosResposta = await listarProtocolosBeauty({ empresaId: contextoEmpresa, clienteId: clienteBeauty.id });
            protocolosBeauty = protocolosResposta.itens.filter((protocolo) => protocolo.status !== "CANCELADO").length;
          } catch {
            protocolosBeauty = 0;
          }
        }

        if (!ativo) {
          return;
        }

        setContagens({
          agenda: agendaResposta.totalItens,
          documentos: documentosResposta.totalItens,
          mensagens: mensagensResposta.itens.filter((mensagem) => !mensagem.lidaPeloPaciente).length,
          planoPublicado,
          protocolosBeauty,
          retornosPendentes: (posVendaNutri?.metricas.retornosPendentes ?? 0) + (posVendaBeauty?.metricas.retornosPendentes ?? 0)
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
        <Cartao
          titulo={`Beauty: ${contagens.protocolosBeauty} protocolos`}
          descricao="Rotina estética vinculada ao cliente Beauty real."
        />
        <Cartao
          titulo={`Retornos: ${contagens.retornosPendentes}`}
          descricao="Sinais de pós-venda e recorrência em Nutri/Beauty."
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
