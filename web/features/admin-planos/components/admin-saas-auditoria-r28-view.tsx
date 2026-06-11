"use client";

import { useQuery } from "@tanstack/react-query";
import { Activity, AlertTriangle, CheckCircle2, ClipboardCheck, Clock3, LoaderCircle, ShieldAlert, ShieldCheck } from "lucide-react";

import {
  consultarAuditoriaOperacionalAdminSaas,
  type ChecklistAuditoriaAdminSaas,
  type EventoAuditoriaAdminSaas
} from "@/features/admin-planos/api/planos-client";

export function AdminSaasAuditoriaR28View() {
  const auditoriaQuery = useQuery({
    queryKey: ["admin-saas-auditoria-r28"],
    queryFn: consultarAuditoriaOperacionalAdminSaas
  });

  if (auditoriaQuery.isLoading) {
    return <EstadoCarregando />;
  }

  if (auditoriaQuery.error instanceof Error) {
    return (
      <section className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-900 shadow-sm">
        <p className="font-semibold">Nao foi possivel carregar a auditoria operacional.</p>
        <p className="mt-1">{auditoriaQuery.error.message}</p>
      </section>
    );
  }

  const auditoria = auditoriaQuery.data;

  return (
    <section className="grid gap-4">
      <section className="overflow-hidden rounded-2xl border bg-slate-950 text-white shadow-sm">
        <div className="grid gap-6 p-5 lg:grid-cols-[minmax(0,1fr)_420px]">
          <div>
            <div className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3 py-1 text-xs font-semibold text-cyan-100">
              <ShieldCheck className="h-4 w-4" /> R28 Auditoria operacional
            </div>
            <h2 className="mt-4 max-w-3xl text-2xl font-semibold tracking-tight">Admin SaaS com trilha de eventos, checklist de risco e prontidao operacional.</h2>
            <p className="mt-3 max-w-3xl text-sm leading-6 text-slate-300">
              Visao para suporte, gestor e operador entenderem eventos sensiveis, riscos ativos e prontidao antes de avancar para gateway real.
            </p>
            <p className="mt-4 text-xs font-medium uppercase tracking-[0.24em] text-slate-400">Atualizado em {formatarDataHora(auditoria?.atualizadoEm)}</p>
          </div>
          <div className="grid gap-3 sm:grid-cols-2">
            <IndicadorAuditoria titulo="Eventos 7 dias" valor={auditoria?.eventosUltimos7Dias ?? 0} detalhe="Trilha persistida" />
            <IndicadorAuditoria titulo="Criticos" valor={auditoria?.eventosCriticosUltimos7Dias ?? 0} detalhe="Exigem revisao" alerta={(auditoria?.eventosCriticosUltimos7Dias ?? 0) > 0} />
            <IndicadorAuditoria titulo="Trials expirando" valor={auditoria?.trialsExpirando7Dias ?? 0} detalhe="Proximos 7 dias" alerta={(auditoria?.trialsExpirando7Dias ?? 0) > 0} />
            <IndicadorAuditoria titulo="Chamados criticos" valor={auditoria?.chamadosCriticosAbertos ?? 0} detalhe="Abertos" alerta={(auditoria?.chamadosCriticosAbertos ?? 0) > 0} />
          </div>
        </div>
      </section>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)]">
        <section className="rounded-xl border bg-card p-4 shadow-sm">
          <CabecalhoAuditoria icon={ClipboardCheck} titulo="Checklist automatico" descricao="Sinais que devem bloquear ou acelerar decisoes operacionais." />
          <div className="mt-4 grid gap-3">
            {(auditoria?.checklist ?? []).map((item) => <ChecklistItem key={item.codigo} item={item} />)}
          </div>
        </section>

        <section className="rounded-xl border bg-card p-4 shadow-sm">
          <CabecalhoAuditoria icon={Activity} titulo="Eventos recentes" descricao="Acoes administrativas registradas para rastreabilidade e suporte." />
          <div className="mt-4 grid gap-3">
            {(auditoria?.eventosRecentes ?? []).map((evento) => <EventoItem key={evento.id} evento={evento} />)}
            {!(auditoria?.eventosRecentes ?? []).length ? <EstadoVazio /> : null}
          </div>
        </section>
      </div>
    </section>
  );
}

function CabecalhoAuditoria({ icon: Icon, titulo, descricao }: { icon: typeof ClipboardCheck; titulo: string; descricao: string }) {
  return (
    <div className="flex items-start gap-3">
      <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-cyan-500/10 text-cyan-700"><Icon className="h-5 w-5" /></span>
      <div>
        <h3 className="text-base font-semibold text-card-foreground">{titulo}</h3>
        <p className="mt-1 text-sm leading-6 text-muted-foreground">{descricao}</p>
      </div>
    </div>
  );
}

function IndicadorAuditoria({ titulo, valor, detalhe, alerta = false }: { titulo: string; valor: number; detalhe: string; alerta?: boolean }) {
  return (
    <div className={`rounded-xl border p-3 ${alerta ? "border-amber-300 bg-amber-400/15" : "border-white/10 bg-white/10"}`}>
      <p className="text-xs font-medium text-slate-300">{titulo}</p>
      <p className="mt-1 text-xl font-semibold text-white">{formatarNumero(valor)}</p>
      <p className="mt-1 text-xs leading-5 text-slate-300">{detalhe}</p>
    </div>
  );
}

function ChecklistItem({ item }: { item: ChecklistAuditoriaAdminSaas }) {
  const ok = item.status === "OK";
  const Icon = ok ? CheckCircle2 : ShieldAlert;
  return (
    <article className={`rounded-lg border p-3 ${ok ? "border-emerald-200 bg-emerald-50" : "border-amber-200 bg-amber-50"}`}>
      <div className="flex items-start gap-3">
        <Icon className={`mt-0.5 h-5 w-5 ${ok ? "text-emerald-700" : "text-amber-700"}`} />
        <div>
          <div className="flex flex-wrap items-center gap-2">
            <p className="text-sm font-semibold text-card-foreground">{item.titulo}</p>
            <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{item.severidade}</span>
          </div>
          <p className="mt-1 text-sm leading-6 text-muted-foreground">{item.detalhe}</p>
        </div>
      </div>
    </article>
  );
}

function EventoItem({ evento }: { evento: EventoAuditoriaAdminSaas }) {
  const alerta = evento.severidade === "ALTA" || evento.severidade === "CRITICA";
  return (
    <article className="rounded-lg border bg-background p-3">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <div className="flex flex-wrap items-center gap-2">
            {alerta ? <AlertTriangle className="h-4 w-4 text-amber-700" /> : <Activity className="h-4 w-4 text-cyan-700" />}
            <p className="text-sm font-semibold text-card-foreground">{evento.tipo}</p>
            <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{evento.severidade}</span>
          </div>
          <p className="mt-2 text-sm leading-6 text-muted-foreground">{evento.descricao}</p>
          <p className="mt-2 text-xs text-muted-foreground">{evento.empresaNome ?? "Sem empresa vinculada"} {evento.referenciaTipo ? `- ${evento.referenciaTipo}` : ""}</p>
        </div>
        <span className="inline-flex items-center gap-1 rounded-md border bg-white px-2 py-1 text-xs font-medium text-muted-foreground"><Clock3 className="h-3.5 w-3.5" /> {formatarDataHora(evento.criadoEm)}</span>
      </div>
      {evento.metadados ? <pre className="mt-3 overflow-x-auto rounded-md bg-slate-950 p-2 text-xs text-slate-100">{evento.metadados}</pre> : null}
    </article>
  );
}

function EstadoCarregando() {
  return <section className="flex min-h-80 items-center justify-center rounded-xl border bg-card text-sm text-muted-foreground shadow-sm"><LoaderCircle className="mr-2 h-4 w-4 animate-spin" /> Carregando auditoria operacional Admin SaaS</section>;
}

function EstadoVazio() {
  return <div className="rounded-lg border bg-background p-4 text-sm leading-6 text-muted-foreground">Nenhum evento administrativo registrado ainda. Bloqueios de empresa e reset demo passam a aparecer aqui.</div>;
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR", { maximumFractionDigits: 0 }).format(valor);
}

function formatarDataHora(valor?: string) {
  if (!valor) {
    return "agora";
  }
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "short", timeStyle: "short" }).format(new Date(valor));
}
