"use client";

import { useQuery } from "@tanstack/react-query";
import {
  Activity,
  AlertTriangle,
  BadgeDollarSign,
  Boxes,
  CalendarCheck,
  ChevronRight,
  LoaderCircle,
  ShieldCheck,
  Users,
  Wrench
} from "lucide-react";

import type { SessaoAutenticada } from "@/features/auth/lib/auth-storage";
import { consultarDashboardEmpresa, type DashboardEmpresa } from "@/features/operacional/api/operacional-client";

type SecaoOperacaoProps = {
  empresaId: string;
  sessao: SessaoAutenticada;
};

type Icone = typeof Users;

export function SecaoOperacao({ empresaId, sessao }: SecaoOperacaoProps) {
  const dashboardQuery = useQuery({
    queryKey: ["dashboard-empresa", empresaId],
    queryFn: () => consultarDashboardEmpresa(empresaId),
    enabled: Boolean(empresaId)
  });

  const dashboard = dashboardQuery.data;

  if (!empresaId) {
    return (
      <EstadoOperacional icon={ShieldCheck} titulo="Selecione uma empresa" descricao="Escolha uma empresa para carregar os indicadores operacionais." />
    );
  }

  return (
    <section className="grid gap-5">
      <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <MetricaOperacional
          icon={Users}
          label="Clientes ativos"
          value={valorMetrica(dashboard?.clientesAtivos, dashboardQuery.isLoading)}
        />
        <MetricaOperacional
          icon={CalendarCheck}
          label="Agenda hoje"
          value={valorMetrica(dashboard?.compromissosHoje, dashboardQuery.isLoading)}
        />
        <MetricaOperacional
          icon={Activity}
          label="Proximos 7 dias"
          value={valorMetrica(dashboard?.compromissosProximos7Dias, dashboardQuery.isLoading)}
        />
        <MetricaOperacional
          icon={BadgeDollarSign}
          label="Custos ativos"
          value={dashboardQuery.isLoading ? "..." : formatarMoeda(custosTotais(dashboard))}
        />
      </div>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_360px]">
        <section className="min-w-0 rounded-lg border bg-card p-4 shadow-sm">
          <div className="mb-4 flex items-center justify-between gap-3 border-b pb-4">
            <div>
              <p className="text-sm font-medium text-primary">Alertas operacionais</p>
              <h2 className="mt-1 text-lg font-semibold text-card-foreground">Acoes que pedem atencao</h2>
            </div>
            {dashboardQuery.isFetching ? <LoaderCircle className="h-4 w-4 animate-spin text-muted-foreground" /> : null}
          </div>

          <div className="grid gap-3 sm:grid-cols-3">
            <IndicadorAlerta
              icon={Boxes}
              label="Estoque baixo"
              value={dashboard?.produtosEstoqueBaixo ?? 0}
              active={(dashboard?.produtosEstoqueBaixo ?? 0) > 0}
            />
            <IndicadorAlerta
              icon={AlertTriangle}
              label="Validade 30 dias"
              value={dashboard?.produtosVencendo30Dias ?? 0}
              active={(dashboard?.produtosVencendo30Dias ?? 0) > 0}
            />
            <IndicadorAlerta
              icon={Wrench}
              label="Manutencao 30 dias"
              value={dashboard?.equipamentosManutencao30Dias ?? 0}
              active={(dashboard?.equipamentosManutencao30Dias ?? 0) > 0}
            />
          </div>
        </section>

        <section className="grid content-start gap-3 rounded-lg border bg-card p-4 shadow-sm">
          <div className="flex items-center justify-between gap-3 border-b pb-4">
            <div>
              <p className="text-sm font-medium text-primary">Sessao</p>
              <h2 className="text-lg font-semibold text-card-foreground">{sessao.usuario.nome}</h2>
            </div>
            <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 text-primary">
              <ShieldCheck className="h-5 w-5" />
            </span>
          </div>
          <div className="grid gap-2 text-sm">
            <LinhaSessao label="Email" value={sessao.usuario.email} />
            <LinhaSessao label="Authorities" value={String(sessao.usuario.authorities.length)} />
            <LinhaSessao label="Tenant" value={sessao.usuario.empresaId ?? "Admin SaaS"} />
          </div>
        </section>
      </div>
    </section>
  );
}

function EstadoOperacional({ icon: Icon, titulo, descricao }: { icon: Icone; titulo: string; descricao: string }) {
  return (
    <div className="flex min-h-72 flex-col items-center justify-center rounded-lg border bg-card p-8 text-center shadow-sm">
      <Icon className="h-9 w-9 text-primary" />
      <h2 className="mt-3 text-lg font-semibold text-card-foreground">{titulo}</h2>
      <p className="mt-1 max-w-md text-sm leading-6 text-muted-foreground">{descricao}</p>
    </div>
  );
}

function MetricaOperacional({ icon: Icon, label, value }: { icon: Icone; label: string; value: string }) {
  return (
    <article className="rounded-lg border bg-card p-4 shadow-sm">
      <div className="flex items-center justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 text-primary">
          <Icon className="h-5 w-5" />
        </span>
        <ChevronRight className="h-4 w-4 text-muted-foreground" />
      </div>
      <p className="mt-4 text-sm font-medium text-muted-foreground">{label}</p>
      <p className="mt-1 text-2xl font-semibold tracking-normal text-card-foreground">{value}</p>
    </article>
  );
}

function IndicadorAlerta({ icon: Icon, label, value, active }: { icon: Icone; label: string; value: number; active: boolean }) {
  return (
    <article className="rounded-lg border bg-background p-4">
      <div className="flex items-center justify-between gap-3">
        <span className={active ? "text-secondary" : "text-primary"}>
          <Icon className="h-5 w-5" />
        </span>
        <span className="text-lg font-semibold text-card-foreground">{value}</span>
      </div>
      <p className="mt-3 text-sm font-medium text-muted-foreground">{label}</p>
    </article>
  );
}

function LinhaSessao({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-center justify-between gap-3 rounded-md border bg-background px-3 py-2">
      <span className="text-muted-foreground">{label}</span>
      <span className="min-w-0 truncate font-semibold text-card-foreground">{value}</span>
    </div>
  );
}

function valorMetrica(valor: number | undefined, carregando: boolean) {
  if (carregando) {
    return "...";
  }
  return formatarNumero(valor ?? 0);
}

function custosTotais(dashboard?: DashboardEmpresa) {
  return (dashboard?.custosGeraisAtivos ?? 0) + (dashboard?.custosAlimentacaoTransporteAtivos ?? 0);
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR").format(valor);
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(valor);
}
