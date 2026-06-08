import React, { useEffect, useMemo, useState } from "react";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";

import {
  CapaPagina,
  CabecalhoSecao,
  Cartao,
  EstadoVazio,
  ItemLista,
  temaAtendePro
} from "@/components/ui-shell";
import { carregarSessaoAutenticada, type SessaoAutenticada } from "@/lib/auth";
import { listarAgendaPortal, type CompromissoAgendaApi, type TipoStatusAgenda } from "@/lib/api/client";

type SessaoAgenda = {
  hoje: CompromissoAgendaApi[];
  futuros: CompromissoAgendaApi[];
};

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

function extrairErro(mensagem: unknown) {
  if (mensagem instanceof Error) {
    return mensagem.message;
  }

  return "Não foi possível carregar a agenda.";
}

function estaNoHoje(inicio: string) {
  const data = new Date(inicio);
  const hoje = new Date();
  return (
    data.getDate() === hoje.getDate() &&
    data.getMonth() === hoje.getMonth() &&
    data.getFullYear() === hoje.getFullYear()
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

function separarItensPorPeriodo(agendamentos: CompromissoAgendaApi[]) {
  return agendamentos.reduce<SessaoAgenda>(
    (acumulador, item) => {
      if (estaNoHoje(item.inicio)) {
        acumulador.hoje.push(item);
      } else {
        acumulador.futuros.push(item);
      }

      return acumulador;
    },
    { hoje: [], futuros: [] }
  );
}

export default function AgendaProfissionalMobile() {
  const [agendamentos, setAgendamentos] = useState<CompromissoAgendaApi[]>([]);
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

        const resposta = await listarAgendaPortal({
          empresaId: contextoEmpresa,
          pagina: 0,
          tamanho: 60
        });

        if (!ativo) {
          return;
        }

        setAgendamentos((resposta?.itens ?? []).filter((item) => item.status !== "CANCELADO"));
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

  const { hoje, futuros } = useMemo(() => separarItensPorPeriodo(agendamentos), [agendamentos]);

  return (
    <CapaPagina titulo="Agenda do profissional" subtitulo={`Empresa: ${empresaId ?? "sem contexto."}`}>
      {carregando ? (
        <View style={estilos.estadoCarregamento}>
          <ActivityIndicator size="small" color={temaAtendePro.destaque} />
          <Text style={estilos.textoCarregamento}>Carregando agenda...</Text>
        </View>
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : (
        <>
          <Cartao titulo={`Hoje (${hoje.length})`} descricao="Atendimentos alinhados para execução.">
            <View style={estilos.lista}>
              {hoje.length === 0 ? (
                <EstadoVazio texto="Sem compromissos para hoje." />
              ) : (
                hoje.map((item) => (
                  <ItemLista
                    key={item.id}
                    titulo={`${formatarHora(item.inicio)} • ${item.observacoes ?? "Sem observações"}`}
                    meta={`${item.sala ?? "Sem sala"} • ${item.profissionalNome}`}
                    descricao={item.observacoes ?? "Sem observação adicional"}
                    status={textoStatus(item.status)}
                  />
                ))
              )}
            </View>
          </Cartao>

          <CabecalhoSecao titulo="Fila para próximos dias" />
          <View style={estilos.lista}>
            {futuros.length === 0 ? (
              <EstadoVazio texto="Sem compromissos além de hoje." />
            ) : (
              futuros.map((item) => (
                <ItemLista
                  key={item.id}
                  titulo={`${formatarDia(item.inicio)} ${formatarHora(item.inicio)} • ${item.observacoes ?? "Sem observações"}`}
                  meta={`${item.sala ?? "Sem sala"} • ${item.profissionalNome}`}
                  descricao={textoStatus(item.status)}
                  status={textoStatus(item.status)}
                />
              ))
            )}
          </View>
        </>
      )}
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
    gap: 10,
    marginBottom: 14
  },
  textoCarregamento: {
    color: temaAtendePro.textoSecundario,
    textAlign: "center"
  }
});
