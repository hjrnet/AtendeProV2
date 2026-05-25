"use client";

import { useMemo, useState } from "react";
import { AlertTriangle, Bell, CalendarClock, CheckCheck, CircleDot, Info, Megaphone } from "lucide-react";

import { Button } from "@/components/ui/button";

type TipoNotificacao = "ALERTA" | "AVISO" | "EVENTO";

type NotificacaoInterna = {
  id: string;
  titulo: string;
  descricao: string;
  tipo: TipoNotificacao;
  area: string;
  criadaEm: string;
  lida: boolean;
};

const notificacoesIniciais: NotificacaoInterna[] = [
  {
    id: "alerta-precificacao",
    titulo: "Simulações em alerta",
    descricao: "Revise procedimentos com margem baixa ou prejuízo antes da próxima apresentação.",
    tipo: "ALERTA",
    area: "Precificação",
    criadaEm: "Hoje, 16:20",
    lida: false
  },
  {
    id: "evento-roadmap",
    titulo: "Roadmap atualizado",
    descricao: "Novos pedidos de melhoria foram priorizados na R9.",
    tipo: "EVENTO",
    area: "Admin SaaS",
    criadaEm: "Hoje, 15:45",
    lida: false
  },
  {
    id: "aviso-ajuda",
    titulo: "Central de ajuda disponível",
    descricao: "Artigos, FAQ e tutoriais rápidos já estão acessíveis no Admin SaaS.",
    tipo: "AVISO",
    area: "Suporte",
    criadaEm: "Hoje, 14:10",
    lida: true
  }
];

export function CentralNotificacoesInternas() {
  const [aberta, setAberta] = useState(false);
  const [mostrarSomenteNaoLidas, setMostrarSomenteNaoLidas] = useState(false);
  const [notificacoes, setNotificacoes] = useState(notificacoesIniciais);

  const naoLidas = notificacoes.filter((notificacao) => !notificacao.lida).length;
  const notificacoesFiltradas = useMemo(
    () => notificacoes.filter((notificacao) => !mostrarSomenteNaoLidas || !notificacao.lida),
    [mostrarSomenteNaoLidas, notificacoes]
  );

  function marcarComoLida(notificacaoId: string) {
    setNotificacoes((atuais) =>
      atuais.map((notificacao) => (notificacao.id === notificacaoId ? { ...notificacao, lida: true } : notificacao))
    );
  }

  function marcarTodasComoLidas() {
    setNotificacoes((atuais) => atuais.map((notificacao) => ({ ...notificacao, lida: true })));
  }

  return (
    <div className="relative">
      <Button type="button" variant="outline" size="icon" onClick={() => setAberta((valor) => !valor)} aria-label="Notificações internas">
        <Bell className="h-4 w-4" />
        {naoLidas > 0 ? (
          <span className="absolute -right-1 -top-1 flex h-5 min-w-5 items-center justify-center rounded-full bg-red-600 px-1 text-[10px] font-bold text-white">
            {naoLidas}
          </span>
        ) : null}
      </Button>

      {aberta ? (
        <section className="absolute right-0 z-30 mt-2 w-[min(92vw,420px)] rounded-lg border bg-card p-3 shadow-xl">
          <div className="flex items-start justify-between gap-3 border-b pb-3">
            <div>
              <p className="text-sm font-medium text-primary">Notificações internas</p>
              <h2 className="mt-1 text-base font-semibold text-card-foreground">Eventos, avisos e alertas</h2>
            </div>
            <Button type="button" variant="outline" size="icon" onClick={marcarTodasComoLidas} title="Marcar todas como lidas">
              <CheckCheck className="h-4 w-4" />
            </Button>
          </div>

          <div className="mt-3 flex gap-2">
            <button
              type="button"
              onClick={() => setMostrarSomenteNaoLidas(false)}
              className={`h-9 rounded-md border px-3 text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring ${
                !mostrarSomenteNaoLidas ? "border-primary bg-primary text-primary-foreground" : "bg-background text-card-foreground"
              }`}
            >
              Todas
            </button>
            <button
              type="button"
              onClick={() => setMostrarSomenteNaoLidas(true)}
              className={`h-9 rounded-md border px-3 text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring ${
                mostrarSomenteNaoLidas ? "border-primary bg-primary text-primary-foreground" : "bg-background text-card-foreground"
              }`}
            >
              Não lidas
            </button>
          </div>

          <div className="mt-3 max-h-[420px] overflow-y-auto pr-1">
            {notificacoesFiltradas.length === 0 ? (
              <div className="flex min-h-32 flex-col items-center justify-center rounded-md border bg-background p-4 text-center">
                <CheckCheck className="h-7 w-7 text-primary" />
                <p className="mt-2 text-sm font-semibold text-card-foreground">Nenhuma notificação pendente</p>
              </div>
            ) : (
              <div className="grid gap-2">
                {notificacoesFiltradas.map((notificacao) => (
                  <article key={notificacao.id} className="rounded-md border bg-background p-3">
                    <div className="flex items-start gap-3">
                      <span className={`mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-md ${classeIcone(notificacao.tipo)}`}>
                        <IconeTipo tipo={notificacao.tipo} />
                      </span>
                      <div className="min-w-0 flex-1">
                        <div className="flex items-start justify-between gap-2">
                          <div>
                            <p className="text-sm font-semibold text-card-foreground">{notificacao.titulo}</p>
                            <p className="mt-1 text-xs leading-5 text-muted-foreground">{notificacao.descricao}</p>
                          </div>
                          {!notificacao.lida ? <CircleDot className="h-4 w-4 shrink-0 text-primary" /> : null}
                        </div>
                        <div className="mt-2 flex flex-wrap items-center gap-2 text-xs font-medium text-muted-foreground">
                          <span className="rounded-md border bg-card px-2 py-1">{notificacao.area}</span>
                          <span>{notificacao.criadaEm}</span>
                          {!notificacao.lida ? (
                            <button
                              type="button"
                              onClick={() => marcarComoLida(notificacao.id)}
                              className="rounded-md border bg-card px-2 py-1 font-semibold text-primary transition-colors hover:border-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                            >
                              Marcar como lida
                            </button>
                          ) : null}
                        </div>
                      </div>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </div>
        </section>
      ) : null}
    </div>
  );
}

function IconeTipo({ tipo }: { tipo: TipoNotificacao }) {
  if (tipo === "ALERTA") {
    return <AlertTriangle className="h-4 w-4" />;
  }
  if (tipo === "EVENTO") {
    return <CalendarClock className="h-4 w-4" />;
  }
  if (tipo === "AVISO") {
    return <Megaphone className="h-4 w-4" />;
  }
  return <Info className="h-4 w-4" />;
}

function classeIcone(tipo: TipoNotificacao) {
  if (tipo === "ALERTA") {
    return "bg-amber-50 text-amber-700";
  }
  if (tipo === "EVENTO") {
    return "bg-blue-50 text-blue-700";
  }
  return "bg-emerald-50 text-emerald-700";
}
