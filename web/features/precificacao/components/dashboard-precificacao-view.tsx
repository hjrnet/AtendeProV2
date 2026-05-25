"use client";

import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  AlertTriangle,
  BadgeDollarSign,
  BarChart3,
  CheckCircle2,
  LoaderCircle,
  Percent,
  TrendingUp
} from "lucide-react";
import {
  Bar,
  BarChart as RechartsBarChart,
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from "recharts";

import {
  consultarDashboardPrecificacao,
  type DashboardPrecificacao,
  type DistribuicaoStatusPrecificacao
} from "@/features/precificacao/api/precificacao-client";

type DashboardPrecificacaoViewProps = {
  empresaId: string;
};

type Icone = typeof BadgeDollarSign;

export function DashboardPrecificacaoView({ empresaId }: DashboardPrecificacaoViewProps) {
  const dashboardQuery = useQuery({
    queryKey: ["precificacao-dashboard", empresaId],
    queryFn: () => consultarDashboardPrecificacao(empresaId),
    enabled: Boolean(empresaId)
  });

  const dashboard = dashboardQuery.data;
  const distribuicao = useMemo(
    () => (dashboard?.distribuicaoStatus ?? []).map((item) => ({ status: rotuloStatus(item.status), total: item.total })),
    [dashboard]
  );
  const recentes = useMemo(
    () =>
      [...(dashboard?.simulacoesRecentes ?? [])].reverse().map((item, index) => ({
        nome: item.nomeProcedimento.length > 18 ? `${item.nomeProcedimento.slice(0, 18)}...` : item.nomeProcedimento,
        ordem: index + 1,
        margem: item.margemRealPercentual,
        recomendado: item.precoRecomendado,
        venda: item.precoVenda
      })),
    [dashboard]
  );

  if (dashboardQuery.isLoading) {
    return (
      <section className="rounded-lg border bg-card p-4 shadow-sm">
        <div className="flex min-h-44 items-center justify-center text-sm text-muted-foreground">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando precificacao
        </div>
      </section>
    );
  }

  if (!dashboard || dashboard.totalSimulacoes === 0) {
    return (
      <section className="rounded-lg border bg-card p-4 shadow-sm">
        <CabecalhoDashboard />
        <div className="mt-4 flex min-h-44 flex-col items-center justify-center rounded-lg border bg-background p-6 text-center">
          <BarChart3 className="h-8 w-8 text-primary" />
          <p className="mt-3 text-sm font-semibold text-card-foreground">Nenhuma simulacao para analisar</p>
        </div>
      </section>
    );
  }

  return (
    <section className="rounded-lg border bg-card p-4 shadow-sm">
      <CabecalhoDashboard dashboard={dashboard} />

      <div className="mt-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <MetricaDashboard icon={BadgeDollarSign} label="Preco recomendado medio" value={formatarMoeda(dashboard.precoMedioRecomendado)} />
        <MetricaDashboard icon={TrendingUp} label="Lucro medio" value={formatarMoeda(dashboard.lucroMedio)} />
        <MetricaDashboard icon={Percent} label="Margem media" value={`${formatarNumero(dashboard.margemMediaPercentual)}%`} />
        <MetricaDashboard icon={AlertTriangle} label="Simulacoes em alerta" value={String(dashboard.simulacoesComAlerta)} />
      </div>

      <div className="mt-4 grid gap-4 xl:grid-cols-[360px_minmax(0,1fr)]">
        <div className="min-h-72 rounded-lg border bg-background p-4">
          <div className="mb-3 flex items-center justify-between gap-3">
            <h3 className="text-sm font-semibold text-card-foreground">Status das margens</h3>
            <span className="rounded-md border bg-card px-2 py-1 text-xs font-semibold text-muted-foreground">
              {dashboard.totalSimulacoes} simulacoes
            </span>
          </div>
          <ResponsiveContainer width="100%" height={220}>
            <RechartsBarChart data={distribuicao} margin={{ left: -20, right: 8, top: 8, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="status" tick={{ fontSize: 11 }} interval={0} />
              <YAxis allowDecimals={false} tick={{ fontSize: 11 }} />
              <Tooltip formatter={(value) => [value, "Total"]} />
              <Bar dataKey="total" fill="#0f766e" radius={[4, 4, 0, 0]} />
            </RechartsBarChart>
          </ResponsiveContainer>
        </div>

        <div className="min-h-72 rounded-lg border bg-background p-4">
          <div className="mb-3 flex items-center justify-between gap-3">
            <h3 className="text-sm font-semibold text-card-foreground">Simulacoes recentes</h3>
            <span className="rounded-md border bg-card px-2 py-1 text-xs font-semibold text-muted-foreground">
              margem x venda
            </span>
          </div>
          <ResponsiveContainer width="100%" height={220}>
            <LineChart data={recentes} margin={{ left: -14, right: 12, top: 8, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" vertical={false} />
              <XAxis dataKey="ordem" tick={{ fontSize: 11 }} />
              <YAxis yAxisId="left" tick={{ fontSize: 11 }} />
              <YAxis yAxisId="right" orientation="right" tick={{ fontSize: 11 }} />
              <Tooltip
                formatter={(value, name) => [typeof value === "number" ? formatarTooltip(Number(value), String(name)) : value, rotuloGrafico(String(name))]}
                labelFormatter={(label) => `Simulacao ${label}`}
              />
              <Line yAxisId="left" type="monotone" dataKey="margem" stroke="#0f766e" strokeWidth={2} dot={{ r: 3 }} />
              <Line yAxisId="right" type="monotone" dataKey="venda" stroke="#d97706" strokeWidth={2} dot={{ r: 3 }} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </section>
  );
}

function CabecalhoDashboard({ dashboard }: { dashboard?: DashboardPrecificacao }) {
  return (
    <div className="flex flex-col gap-3 border-b pb-4 md:flex-row md:items-center md:justify-between">
      <div>
        <p className="text-sm font-medium text-primary">Dashboard</p>
        <h2 className="mt-1 text-xl font-semibold text-card-foreground">Precificacao</h2>
      </div>
      <div className="flex flex-wrap gap-2">
        <span className="inline-flex items-center gap-2 rounded-md border bg-background px-3 py-2 text-sm font-semibold text-card-foreground">
          <CheckCircle2 className="h-4 w-4 text-primary" />
          {dashboard?.simulacoesSaudaveis ?? 0} saudaveis
        </span>
        <span className="inline-flex items-center gap-2 rounded-md border bg-background px-3 py-2 text-sm font-semibold text-card-foreground">
          <AlertTriangle className="h-4 w-4 text-amber-600" />
          {dashboard?.simulacoesComAlerta ?? 0} alertas
        </span>
      </div>
    </div>
  );
}

function MetricaDashboard({ icon: Icon, label, value }: { icon: Icone; label: string; value: string }) {
  return (
    <article className="rounded-lg border bg-background p-4">
      <div className="flex items-center justify-between gap-3">
        <p className="text-sm font-medium text-muted-foreground">{label}</p>
        <Icon className="h-4 w-4 text-primary" />
      </div>
      <p className="mt-3 text-xl font-semibold text-card-foreground">{value}</p>
    </article>
  );
}

function rotuloStatus(status: DistribuicaoStatusPrecificacao["status"]) {
  const rotulos: Record<DistribuicaoStatusPrecificacao["status"], string> = {
    PREJUIZO: "Prejuizo",
    EQUILIBRIO: "Equilibrio",
    MARGEM_BAIXA: "Baixa",
    SAUDAVEL: "Saudavel"
  };
  return rotulos[status];
}

function rotuloGrafico(nome: string) {
  const rotulos: Record<string, string> = {
    margem: "Margem",
    venda: "Venda"
  };
  return rotulos[nome] ?? nome;
}

function formatarTooltip(valor: number, nome: string) {
  if (nome === "margem") {
    return `${formatarNumero(valor)}%`;
  }
  return formatarMoeda(valor);
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(valor);
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR", { maximumFractionDigits: 2 }).format(valor);
}
