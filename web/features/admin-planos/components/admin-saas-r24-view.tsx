"use client";

import { useMemo, useState } from "react";
import { useMutation, useQuery } from "@tanstack/react-query";
import { Building2, CheckCircle2, CreditCard, DatabaseZap, LoaderCircle, PlayCircle, RefreshCcw, Route, ShieldCheck, Sparkles, TrendingUp } from "lucide-react";

import {
  consultarDashboardAdminSaas,
  consultarDashboardVendasAdminSaas,
  listarPlanos,
  resetarDemoAdminSaas,
  type DashboardVendasAdminSaas,
  type PerfilDemoAdminSaas,
  type Plano
} from "@/features/admin-planos/api/planos-client";

type EtapaOnboarding = {
  titulo: string;
  descricao: string;
  status: "concluida" | "pronta" | "pendente";
};

const perfisDemo = [
  {
    perfil: "NUTRI" as PerfilDemoAdminSaas,
    label: "Nutri",
    descricao: "Pacientes, plano alimentar, diario, metas, documentos e mensagens.",
    seguranca: "Reset sem dados reais de clientes externos."
  },
  {
    perfil: "BEAUTY" as PerfilDemoAdminSaas,
    label: "Beauty",
    descricao: "Protocolos, estoque com validade, termos, evidencias e margem real.",
    seguranca: "Reset preserva cadastros base e recria somente massa demo."
  },
  {
    perfil: "GESTOR" as PerfilDemoAdminSaas,
    label: "Gestor",
    descricao: "Indicadores por vertical, agenda, pos-venda e comparacao comercial.",
    seguranca: "Reset focado em demonstracao executiva."
  },
  {
    perfil: "INVESTIDOR" as PerfilDemoAdminSaas,
    label: "Investidor",
    descricao: "MRR, trials, churn, planos vendidos e tracao por vertical.",
    seguranca: "Dados financeiros demo marcados como simulados."
  },
  {
    perfil: "SUPORTE" as PerfilDemoAdminSaas,
    label: "Suporte",
    descricao: "Base local para diagnostico, suporte e apresentacao guiada.",
    seguranca: "Disponivel somente no backend local."
  }
];

export function AdminSaasR24View() {
  const [planoSelecionadoCodigo, setPlanoSelecionadoCodigo] = useState<string | null>(null);
  const [acaoComercial, setAcaoComercial] = useState<string | null>(null);

  const planosQuery = useQuery({
    queryKey: ["admin-planos", "r24-checkout"],
    queryFn: () => listarPlanos({ pagina: 0, tamanho: 24 })
  });
  const dashboardQuery = useQuery({
    queryKey: ["admin-saas-dashboard-r24"],
    queryFn: consultarDashboardAdminSaas
  });
  const vendasQuery = useQuery({
    queryKey: ["admin-saas-dashboard-vendas-r24"],
    queryFn: consultarDashboardVendasAdminSaas
  });
  const resetDemoMutation = useMutation({
    mutationFn: resetarDemoAdminSaas
  });

  const planos = useMemo(() => (planosQuery.data?.itens ?? []).filter((plano) => plano.ativo), [planosQuery.data?.itens]);
  const planoSelecionado = planos.find((plano) => plano.codigo === planoSelecionadoCodigo) ?? planos[0] ?? null;
  const vendas = vendasQuery.data ?? null;
  const dashboard = dashboardQuery.data ?? null;
  const verticalMaisTracao = resolverVerticalMaisTracao(vendas);
  const etapasOnboarding = montarEtapasOnboarding(Boolean(planoSelecionado), dashboard?.empresasAtivas ?? 0);

  return (
    <section className="grid gap-4">
      <section className="overflow-hidden rounded-2xl border bg-slate-950 text-white shadow-sm">
        <div className="grid gap-6 p-5 lg:grid-cols-[1fr_0.8fr]">
          <div>
            <div className="inline-flex items-center gap-2 rounded-full border border-white/15 bg-white/10 px-3 py-1 text-xs font-semibold text-emerald-100">
              <Sparkles className="h-4 w-4" /> R24 SaaS comercial
            </div>
            <h2 className="mt-4 max-w-3xl text-2xl font-semibold tracking-tight">Checkout, onboarding, métricas e ambiente demo no mesmo cockpit.</h2>
            <p className="mt-3 max-w-3xl text-sm leading-6 text-slate-300">
              Esta visão prepara venda self-service e operação SaaS sem acoplar gateway real agora. O administrador já enxerga MRR, trials, churn, planos vendidos e rotinas de ativação.
            </p>
          </div>
          <div className="grid gap-3 sm:grid-cols-2">
            <IndicadorHero titulo="MRR atual" valor={formatarMoeda(dashboard?.mrr ?? vendas?.mrr ?? 0)} detalhe="Base real de assinaturas ativas" />
            <IndicadorHero titulo="Trials ativos" valor={formatarNumero(dashboard?.trialsAtivos ?? 0)} detalhe="Empresas em avaliação" />
            <IndicadorHero titulo="Conversão trial" valor={`${formatarNumero(vendas?.taxaConversaoTrial ?? 0)}%`} detalhe="Trial convertido em assinatura" />
            <IndicadorHero titulo="Churn" valor={`${formatarNumero(vendas?.taxaChurn ?? 0)}%`} detalhe="Cancelamentos sobre base" />
          </div>
        </div>
      </section>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,1.1fr)_minmax(340px,0.9fr)]">
        <section className="rounded-xl border bg-card p-4 shadow-sm">
          <CabecalhoR24 icon={CreditCard} titulo="Checkout e assinatura self-service" descricao="Escolha de plano, trial, upgrade/downgrade e integração futura com gateway." />
          {planosQuery.isLoading ? (
            <EstadoCarregandoR24 texto="Carregando planos comerciais" />
          ) : (
            <div className="mt-4 grid gap-3 md:grid-cols-2">
              {planos.map((plano) => (
                <button
                  key={plano.id}
                  type="button"
                  onClick={() => setPlanoSelecionadoCodigo(plano.codigo)}
                  className={`rounded-xl border p-4 text-left transition-colors ${planoSelecionado?.codigo === plano.codigo ? "border-emerald-500 bg-emerald-50" : "bg-background hover:border-emerald-300"}`}
                >
                  <div className="flex items-start justify-between gap-3">
                    <div>
                      <p className="text-sm font-semibold text-card-foreground">{plano.nome}</p>
                      <p className="mt-1 text-xs font-medium uppercase text-muted-foreground">{plano.codigo}</p>
                    </div>
                    <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-emerald-800">{formatarMoeda(plano.valorMensal)}</span>
                  </div>
                  <p className="mt-3 line-clamp-2 text-sm leading-6 text-muted-foreground">{plano.descricao ?? "Plano comercial AtendePro."}</p>
                  <div className="mt-3 flex flex-wrap gap-2 text-xs font-medium text-muted-foreground">
                    <span className="rounded-md border bg-white px-2 py-1">{plano.limiteUsuarios} usuários</span>
                    <span className="rounded-md border bg-white px-2 py-1">{plano.limiteClientes} clientes</span>
                    <span className="rounded-md border bg-white px-2 py-1">{plano.limiteProfissionais} profissionais</span>
                  </div>
                </button>
              ))}
              {!planos.length ? <EstadoVazioR24 texto="Nenhum plano ativo encontrado para checkout." /> : null}
            </div>
          )}
          <div className="mt-4 rounded-xl border bg-background p-4">
            <p className="text-sm font-semibold text-card-foreground">Próxima ação comercial</p>
            <p className="mt-1 text-sm leading-6 text-muted-foreground">
              Plano selecionado: <strong>{planoSelecionado?.nome ?? "nenhum"}</strong>. Gateway real fica preparado como contrato futuro; por enquanto a ação registra intenção segura.
            </p>
            <div className="mt-3 flex flex-wrap gap-2">
              {["Iniciar trial", "Upgrade", "Downgrade"].map((acao) => (
                <button
                  key={acao}
                  type="button"
                  onClick={() => setAcaoComercial(`${acao} preparado para ${planoSelecionado?.nome ?? "plano ativo"}`)}
                  className="inline-flex h-9 items-center gap-2 rounded-md bg-primary px-3 text-xs font-semibold text-primary-foreground transition-colors hover:bg-primary/90"
                >
                  <PlayCircle className="h-4 w-4" /> {acao}
                </button>
              ))}
            </div>
            {acaoComercial ? <p className="mt-3 rounded-md border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm font-medium text-emerald-900">{acaoComercial}</p> : null}
          </div>
        </section>

        <section className="rounded-xl border bg-card p-4 shadow-sm">
          <CabecalhoR24 icon={Route} titulo="Onboarding guiado de empresa" descricao="Configuração inicial por vertical, usuários, serviços, agenda e dados demo opcionais." />
          <div className="mt-4 grid gap-3">
            {etapasOnboarding.map((etapa, indice) => (
              <article key={etapa.titulo} className="grid grid-cols-[auto_minmax(0,1fr)] gap-3 rounded-lg border bg-background p-3">
                <span className={`flex h-8 w-8 items-center justify-center rounded-full text-sm font-semibold ${classeEtapa(etapa.status)}`}>{indice + 1}</span>
                <div>
                  <div className="flex flex-wrap items-center justify-between gap-2">
                    <p className="text-sm font-semibold text-card-foreground">{etapa.titulo}</p>
                    <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{rotuloEtapa(etapa.status)}</span>
                  </div>
                  <p className="mt-1 text-sm leading-6 text-muted-foreground">{etapa.descricao}</p>
                </div>
              </article>
            ))}
          </div>
          <div className="mt-4 rounded-lg border border-sky-200 bg-sky-50 p-3">
            <p className="text-sm font-semibold text-sky-950">Vertical recomendada</p>
            <p className="mt-1 text-sm leading-6 text-sky-950/80">{verticalMaisTracao}</p>
          </div>
        </section>
      </div>

      <div className="grid gap-4 xl:grid-cols-2">
        <section className="rounded-xl border bg-card p-4 shadow-sm">
          <CabecalhoR24 icon={TrendingUp} titulo="Métricas SaaS no Admin" descricao="MRR, churn, trials, conversão, planos ativos e alertas operacionais." />
          {vendasQuery.isLoading || dashboardQuery.isLoading ? (
            <EstadoCarregandoR24 texto="Carregando métricas SaaS" />
          ) : (
            <div className="mt-4 grid gap-3 sm:grid-cols-2">
              <MetricaSaas titulo="Empresas ativas" valor={formatarNumero(dashboard?.empresasAtivas ?? 0)} detalhe={`${formatarNumero(dashboard?.empresasBloqueadas ?? 0)} bloqueadas`} />
              <MetricaSaas titulo="Assinaturas ativas" valor={formatarNumero(vendas?.assinaturasAtivas ?? 0)} detalhe={`${formatarNumero(vendas?.assinaturasCanceladas ?? 0)} canceladas`} />
              <MetricaSaas titulo="Trials iniciados" valor={formatarNumero(vendas?.trialsIniciados ?? 0)} detalhe={`${formatarNumero(vendas?.trialsConvertidos ?? 0)} convertidos`} />
              <MetricaSaas titulo="Alertas operacionais" valor={formatarNumero(dashboard?.chamadosAbertos ?? 0)} detalhe="Chamados abertos na base" />
            </div>
          )}
          <div className="mt-4 grid gap-2">
            {(vendas?.planosVendidos ?? []).slice(0, 5).map((plano) => (
              <div key={plano.planoId} className="grid gap-2 rounded-lg border bg-background p-3 sm:grid-cols-[minmax(0,1fr)_auto] sm:items-center">
                <div>
                  <p className="text-sm font-semibold text-card-foreground">{plano.nome}</p>
                  <p className="mt-1 text-xs text-muted-foreground">{plano.codigo} · {formatarNumero(plano.assinaturasAtivas)} ativas de {formatarNumero(plano.totalAssinaturas)} assinaturas</p>
                </div>
                <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-emerald-800">{formatarMoeda(plano.mrr)}</span>
              </div>
            ))}
            {!vendasQuery.isLoading && !(vendas?.planosVendidos ?? []).length ? <EstadoVazioR24 texto="Ainda não há planos vendidos registrados." /> : null}
          </div>
        </section>

        <section className="rounded-xl border bg-card p-4 shadow-sm">
          <CabecalhoR24 icon={DatabaseZap} titulo="Ambiente demo/reset por perfil" descricao="Preparação segura para Nutri, Beauty, gestor, investidor e suporte." />
          <div className="mt-4 grid gap-3 sm:grid-cols-2">
            {perfisDemo.map((perfil) => (
              <article key={perfil.perfil} className="rounded-lg border bg-background p-3">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <p className="text-sm font-semibold text-card-foreground">{perfil.label}</p>
                    <p className="mt-1 text-sm leading-6 text-muted-foreground">{perfil.descricao}</p>
                  </div>
                  <ShieldCheck className="h-5 w-5 text-emerald-700" />
                </div>
                <p className="mt-2 rounded-md border bg-white px-2 py-1 text-xs font-medium text-muted-foreground">{perfil.seguranca}</p>
                <button
                  type="button"
                  disabled={resetDemoMutation.isPending}
                  onClick={() => resetDemoMutation.mutate({
                    perfil: perfil.perfil,
                    confirmarReset: true,
                    motivo: `Reset demo R26 solicitado pelo cockpit Admin SaaS para ${perfil.label}.`
                  })}
                  className="mt-3 inline-flex h-9 items-center gap-2 rounded-md border bg-white px-3 text-xs font-semibold text-card-foreground transition-colors hover:border-primary/50"
                >
                  <RefreshCcw className={`h-4 w-4 ${resetDemoMutation.isPending ? "animate-spin" : ""}`} /> Executar reset seguro
                </button>
              </article>
            ))}
          </div>
          {resetDemoMutation.data ? (
            <div className="mt-4 rounded-md border border-emerald-200 bg-emerald-50 px-3 py-3 text-sm text-emerald-950">
              <p className="font-semibold">{resetDemoMutation.data.perfilRotulo}: {resetDemoMutation.data.status}</p>
              <p className="mt-1 text-emerald-900/80">Ambiente: {resetDemoMutation.data.ambiente} · atualizado em {formatarDataHora(resetDemoMutation.data.atualizadoEm)}</p>
              <ul className="mt-2 list-disc space-y-1 pl-5">
                {resetDemoMutation.data.etapas.map((etapa) => <li key={etapa}>{etapa}</li>)}
              </ul>
              <p className="mt-2 font-medium">Credenciais: {resetDemoMutation.data.credenciais.join(" · ")}</p>
            </div>
          ) : null}
          {resetDemoMutation.error instanceof Error ? (
            <p className="mt-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm font-medium text-red-900">{resetDemoMutation.error.message}</p>
          ) : null}
        </section>
      </div>
    </section>
  );
}

function CabecalhoR24({ icon: Icon, titulo, descricao }: { icon: typeof CreditCard; titulo: string; descricao: string }) {
  return (
    <div className="flex items-start gap-3">
      <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary">
        <Icon className="h-5 w-5" />
      </span>
      <div>
        <h3 className="text-base font-semibold text-card-foreground">{titulo}</h3>
        <p className="mt-1 text-sm leading-6 text-muted-foreground">{descricao}</p>
      </div>
    </div>
  );
}

function IndicadorHero({ titulo, valor, detalhe }: { titulo: string; valor: string; detalhe: string }) {
  return (
    <div className="rounded-xl border border-white/10 bg-white/10 p-3">
      <p className="text-xs font-medium text-slate-300">{titulo}</p>
      <p className="mt-1 text-xl font-semibold text-white">{valor}</p>
      <p className="mt-1 text-xs leading-5 text-slate-300">{detalhe}</p>
    </div>
  );
}

function MetricaSaas({ titulo, valor, detalhe }: { titulo: string; valor: string; detalhe: string }) {
  return (
    <article className="rounded-lg border bg-background p-3">
      <p className="text-xs font-medium text-muted-foreground">{titulo}</p>
      <p className="mt-1 text-2xl font-semibold text-card-foreground">{valor}</p>
      <p className="mt-1 text-xs leading-5 text-muted-foreground">{detalhe}</p>
    </article>
  );
}

function EstadoCarregandoR24({ texto }: { texto: string }) {
  return (
    <div className="mt-4 flex min-h-28 items-center justify-center rounded-lg border bg-background text-sm text-muted-foreground">
      <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
      {texto}
    </div>
  );
}

function EstadoVazioR24({ texto }: { texto: string }) {
  return <div className="rounded-lg border bg-background p-4 text-sm leading-6 text-muted-foreground">{texto}</div>;
}

function montarEtapasOnboarding(planoSelecionado: boolean, empresasAtivas: number): EtapaOnboarding[] {
  return [
    {
      titulo: "Escolher plano e trial",
      descricao: "Plano comercial selecionado antes de ativar cobrança real.",
      status: planoSelecionado ? "concluida" : "pronta"
    },
    {
      titulo: "Configurar vertical",
      descricao: "Nutri, Beauty ou operação mista orientam menus, demo e métricas iniciais.",
      status: "pronta"
    },
    {
      titulo: "Criar usuários e permissões",
      descricao: "Administrador, profissional e suporte entram com perfis mínimos.",
      status: empresasAtivas > 0 ? "concluida" : "pendente"
    },
    {
      titulo: "Serviços, agenda e dados demo",
      descricao: "Serviços principais, agenda de primeira semana e massa demo opcional.",
      status: "pronta"
    }
  ];
}

function classeEtapa(status: EtapaOnboarding["status"]) {
  if (status === "concluida") {
    return "bg-emerald-600 text-white";
  }
  if (status === "pronta") {
    return "bg-sky-600 text-white";
  }
  return "bg-muted text-muted-foreground";
}

function rotuloEtapa(status: EtapaOnboarding["status"]) {
  if (status === "concluida") {
    return "Concluída";
  }
  if (status === "pronta") {
    return "Pronta";
  }
  return "Pendente";
}

function resolverVerticalMaisTracao(vendas: DashboardVendasAdminSaas | null) {
  const planoDestaque = vendas?.planosVendidos?.[0];
  if (!planoDestaque) {
    return "Aguardando primeiras assinaturas para apontar vertical de maior tração.";
  }

  const codigo = planoDestaque.codigo.toLowerCase();
  if (codigo.includes("beauty")) {
    return `Beauty Pro lidera tração comercial com ${formatarNumero(planoDestaque.assinaturasAtivas)} assinaturas ativas.`;
  }
  if (codigo.includes("nutri")) {
    return `Nutri Pro lidera tração comercial com ${formatarNumero(planoDestaque.assinaturasAtivas)} assinaturas ativas.`;
  }
  return `${planoDestaque.nome} lidera tração comercial com ${formatarNumero(planoDestaque.assinaturasAtivas)} assinaturas ativas.`;
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(valor);
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR", { maximumFractionDigits: 1 }).format(valor);
}

function formatarDataHora(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(valor));
}
