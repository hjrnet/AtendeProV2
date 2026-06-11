import React, { useEffect, useMemo, useState } from "react";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";

import {
  CapaPagina,
  Cartao,
  CabecalhoSecao,
  EstadoVazio,
  ItemLista,
  temaAtendePro
} from "@/components/ui-shell";
import { carregarSessaoAutenticada, type SessaoAutenticada } from "@/lib/auth";
import { listarAgendaPortal, resolverPrimeiroClienteBeauty, resolverPrimeiroPacienteNutri, type CompromissoAgendaApi, type TipoStatusAgenda } from "@/lib/api/client";

function extrairErro(mensagem: unknown) {
  if (mensagem instanceof Error) {
    return mensagem.message;
  }

  return "Não foi possível carregar a agenda.";
}

function isHoje(dataIso: string) {
  const data = new Date(dataIso);
  const agora = new Date();
  return (
    data.getDate() === agora.getDate() &&
    data.getMonth() === agora.getMonth() &&
    data.getFullYear() === agora.getFullYear()
  );
}

function formatarHora(dataIso: string) {
  return new Intl.DateTimeFormat("pt-BR", { hour: "2-digit", minute: "2-digit" }).format(new Date(dataIso));
}

function formatarDia(dataIso: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    weekday: "short",
    day: "2-digit",
    month: "2-digit"
  }).format(new Date(dataIso));
}

function textoStatus(status: TipoStatusAgenda) {
  return {
    AGENDADO: "Agendado",
    CONFIRMADO: "Confirmado",
    REALIZADO: "Realizado",
    CANCELADO: "Cancelado",
    FALTOU: "Faltou",
    REMARCADO: "Remarcado"
  }[status];
}

export default function AgendaClienteMobile() {
  const [agenda, setAgenda] = useState<CompromissoAgendaApi[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [empresaId, setEmpresaId] = useState<string | null>(null);

  useEffect(() => {
    let ativo = true;

    const carregar = async () => {
      setCarregando(true);
      setErro("");

      try {
        const sessao: SessaoAutenticada | null = await carregarSessaoAutenticada();
        const contextoEmpresa = sessao?.usuario.empresaId ?? null;
        setEmpresaId(contextoEmpresa);
        const pacienteNutri = await resolverPrimeiroPacienteNutri(contextoEmpresa);
        const clienteBeauty = await resolverPrimeiroClienteBeauty(contextoEmpresa);
        const clientePacienteId = pacienteNutri?.id ?? clienteBeauty?.id;

        const resposta = await listarAgendaPortal({ empresaId: contextoEmpresa, clientePacienteId, tamanho: 50 });
        if (!ativo) {
          return;
        }

        setAgenda((resposta?.itens ?? []).filter((item) => item.status !== "CANCELADO"));
      } catch (falha) {
        if (ativo) {
          setErro(extrairErro(falha));
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

  const agendaHoje = useMemo(() => agenda.filter((item) => isHoje(item.inicio)), [agenda]);
  const agendaProxima = useMemo(() => agenda.filter((item) => !isHoje(item.inicio)), [agenda]);

  return (
    <CapaPagina
      titulo="Agenda do cliente"
      subtitulo={`Lista operacional da sua empresa ${empresaId ? "com autenticação ativa." : "sem contexto."}`}
    >
      {carregando ? (
        <View style={estilos.estadoCarregamento}>
          <ActivityIndicator size="small" color={temaAtendePro.destaque} />
          <Text style={estilos.textoCarregamento}>Carregando compromissos...</Text>
        </View>
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : (
        <>
          <Cartao titulo={`Hoje (${agendaHoje.length})`} descricao="Compromissos confirmados e programados para o dia atual.">
            {agendaHoje.length === 0 ? (
              <EstadoVazio texto="Sem compromissos hoje." />
            ) : (
              <View style={estilos.lista}>
                {agendaHoje.map((item) => (
                  <ItemLista
                    key={item.id}
                    titulo={`${formatarHora(item.inicio)} • ${item.observacoes ?? "Sem observações"}`}
                    meta={`${item.sala ?? "Sem sala"} • ${item.profissionalNome}`}
                    descricao={textoStatus(item.status)}
                    status={textoStatus(item.status)}
                  />
                ))}
              </View>
            )}
          </Cartao>

          <CabecalhoSecao titulo="Próximos dias" />
          <View style={estilos.lista}>
            {agendaProxima.length === 0 ? (
              <EstadoVazio texto="Sem compromissos na semana atual." />
            ) : (
              agendaProxima.map((item) => (
                <ItemLista
                  key={item.id}
                  titulo={`${formatarDia(item.inicio)} ${formatarHora(item.inicio)} • ${item.observacoes ?? "Sem observações"}`}
                  descricao={item.profissionalNome}
                  meta={`${item.sala ?? "Sem sala"} • ${textoStatus(item.status)}`}
                  status={textoStatus(item.status)}
                />
              ))
            )}
          </View>
        </>
      )}

      <CabecalhoSecao titulo="Lembrete diário" />
      <Cartao titulo="Rotina recomendada" descricao="Check de sintomas e alinhamento de rotina após 20:00.">
        <Text style={estilos.textoLembrete}>
          O app não substitui orientação clínica; use para organização e comunicação prévia.
        </Text>
      </Cartao>
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  lista: {
    gap: 12
  },
  textoLembrete: {
    color: temaAtendePro.texto,
    marginTop: 8,
    lineHeight: 20
  },
  estadoCarregamento: {
    backgroundColor: temaAtendePro.superficieSecundaria,
    borderColor: temaAtendePro.borda,
    borderWidth: 1,
    borderRadius: 12,
    padding: 12,
    gap: 10,
    marginBottom: 14
  },
  textoCarregamento: {
    color: temaAtendePro.textoSecundario,
    textAlign: "center"
  }
});
