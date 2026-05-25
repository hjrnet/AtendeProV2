"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import {
  Activity,
  AlertTriangle,
  BadgeDollarSign,
  Boxes,
  Building2,
  CalendarCheck,
  ChevronRight,
  LayoutDashboard,
  LoaderCircle,
  LogOut,
  PackageCheck,
  Search,
  ShieldCheck,
  Stethoscope,
  Users,
  Wrench
} from "lucide-react";

import { Button } from "@/components/ui/button";
import { AdminPlanosView } from "@/features/admin-planos/components/admin-planos-view";
import { limparSessaoAutenticada, type SessaoAutenticada } from "@/features/auth/lib/auth-storage";
import {
  buscarGlobal,
  consultarDashboardEmpresa,
  listarEmpresas,
  type DashboardEmpresa,
  type ResultadoBuscaGlobal
} from "@/features/operacional/api/operacional-client";
import { DashboardPrecificacaoView } from "@/features/precificacao/components/dashboard-precificacao-view";
import { SimuladorPrecificacaoView } from "@/features/precificacao/components/simulador-precificacao-view";
import { VerticaisProfissionaisView } from "@/features/verticais/components/verticais-profissionais-view";

type PainelProtegidoViewProps = {
  sessao: SessaoAutenticada;
};

const categoriasBusca = ["", "NUTRI", "GERAL", "Insumos", "Esterilizacao"];
const statusBusca = ["", "ATIVO", "INATIVO", "AGENDADO", "CONFIRMADO", "REALIZADO"];

export function PainelProtegidoView({ sessao }: PainelProtegidoViewProps) {
  const router = useRouter();
  const [empresaSelecionadaId, setEmpresaSelecionadaId] = useState(sessao.usuario.empresaId ?? "");
  const [busca, setBusca] = useState("");
  const [categoria, setCategoria] = useState("");
  const [status, setStatus] = useState("ATIVO");

  const perfilPrincipal = useMemo(() => normalizarPerfil(sessao.usuario.perfis.at(0)), [sessao]);
  const ehUsuarioSaas = !sessao.usuario.empresaId;

  const empresasQuery = useQuery({
    queryKey: ["empresas-operacionais"],
    queryFn: () => listarEmpresas({ pagina: 0, tamanho: 20 }),
    enabled: ehUsuarioSaas
  });

  useEffect(() => {
    if (!empresaSelecionadaId && empresasQuery.data?.itens.length) {
      setEmpresaSelecionadaId(empresasQuery.data.itens[0].id);
    }
  }, [empresaSelecionadaId, empresasQuery.data]);

  const empresaAtivaId = sessao.usuario.empresaId ?? empresaSelecionadaId;

  const dashboardQuery = useQuery({
    queryKey: ["dashboard-empresa", empresaAtivaId],
    queryFn: () => consultarDashboardEmpresa(empresaAtivaId),
    enabled: Boolean(empresaAtivaId)
  });

  const buscaQuery = useQuery({
    queryKey: ["busca-global", empresaAtivaId, busca, categoria, status],
    queryFn: () =>
      buscarGlobal({
        empresaId: empresaAtivaId,
        busca,
        categoria,
        status,
        limitePorTipo: 4
      }),
    enabled: Boolean(empresaAtivaId) && (busca.trim().length >= 2 || Boolean(categoria) || Boolean(status))
  });

  function sair() {
    limparSessaoAutenticada();
    router.replace("/login");
  }

  const dashboard = dashboardQuery.data;
  const resultados = buscaQuery.data?.itens ?? [];
  const empresaSelecionada = empresasQuery.data?.itens.find((empresa) => empresa.id === empresaAtivaId);

  return (
    <main className="min-h-screen bg-background">
      <div className="mx-auto grid w-full max-w-[1440px] lg:grid-cols-[248px_minmax(0,1fr)]">
        <aside className="hidden min-h-screen border-r bg-card px-5 py-6 lg:block">
          <div className="flex items-center gap-3">
            <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary text-primary-foreground">
              <Stethoscope className="h-5 w-5" />
            </span>
            <div>
              <p className="text-sm font-semibold text-card-foreground">AtendePro</p>
              <p className="text-xs font-medium text-muted-foreground">Operacional</p>
            </div>
          </div>

          <nav className="mt-8 grid gap-2">
            <LinkPainel href="#operacao" icon={LayoutDashboard} label="Operacao" />
            <LinkPainel href="#verticais" icon={Stethoscope} label="Verticais" />
            <LinkPainel href="#precificacao" icon={BadgeDollarSign} label="Precificacao" />
            <LinkPainel href="#busca" icon={Search} label="Busca global" />
            <LinkPainel href="#admin" icon={PackageCheck} label="Admin SaaS" />
          </nav>
        </aside>

        <section className="min-w-0 px-4 py-5 sm:px-6 lg:px-8">
          <header className="flex flex-col gap-4 border-b pb-5 xl:flex-row xl:items-center xl:justify-between">
            <div className="min-w-0">
              <div className="flex flex-wrap items-center gap-2 text-sm font-medium text-muted-foreground">
                <span className="inline-flex items-center gap-2 rounded-md border bg-card px-3 py-1.5">
                  <ShieldCheck className="h-4 w-4 text-primary" />
                  {perfilPrincipal}
                </span>
                <span className="inline-flex items-center gap-2 rounded-md border bg-card px-3 py-1.5">
                  <Building2 className="h-4 w-4 text-primary" />
                  {empresaSelecionada?.nomeFantasia ?? (sessao.usuario.empresaId ? "Empresa atual" : "Admin SaaS")}
                </span>
              </div>
              <h1 className="mt-3 text-2xl font-semibold tracking-normal text-foreground sm:text-3xl">
                Painel operacional
              </h1>
            </div>

            <div className="flex flex-col gap-2 sm:flex-row sm:items-center">
              {ehUsuarioSaas ? (
                <label className="grid gap-1 text-sm font-medium text-card-foreground">
                  Empresa
                  <select
                    value={empresaSelecionadaId}
                    onChange={(event) => setEmpresaSelecionadaId(event.target.value)}
                    className="h-10 min-w-64 rounded-md border bg-card px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                  >
                    {(empresasQuery.data?.itens ?? []).map((empresa) => (
                      <option key={empresa.id} value={empresa.id}>
                        {empresa.nomeFantasia}
                      </option>
                    ))}
                  </select>
                </label>
              ) : null}
              <Button type="button" variant="outline" onClick={sair}>
                <LogOut className="h-4 w-4" />
                Sair
              </Button>
            </div>
          </header>

          <section id="operacao" className="py-5">
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

            <div className="mt-4 grid gap-4 xl:grid-cols-[minmax(0,1fr)_360px]">
              <section className="min-w-0 border-y py-4">
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

              <section className="grid content-start gap-3 border-y py-4">
                <div className="flex items-center justify-between gap-3">
                  <div>
                    <p className="text-sm font-medium text-primary">Sessao</p>
                    <h2 className="text-lg font-semibold text-foreground">{sessao.usuario.nome}</h2>
                  </div>
                  <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 text-primary">
                    <ShieldCheck className="h-5 w-5" />
                  </span>
                </div>
                <div className="grid gap-2 text-sm">
                  <LinhaSessao label="Email" value={sessao.usuario.email} />
                  <LinhaSessao label="Authorities" value={String(sessao.usuario.authorities.length)} />
                </div>
              </section>
            </div>
          </section>

          <section id="verticais" className="border-t py-5">
            <VerticaisProfissionaisView />
          </section>

          <section id="precificacao" className="border-t py-5">
            <div className="grid gap-4">
              <DashboardPrecificacaoView empresaId={empresaAtivaId} />
              <SimuladorPrecificacaoView empresaId={empresaAtivaId} />
            </div>
          </section>

          <section id="busca" className="border-t py-5">
            <div className="flex flex-col gap-3 xl:flex-row xl:items-end">
              <label className="grid min-w-0 flex-1 gap-1 text-sm font-medium text-card-foreground">
                Busca
                <span className="relative">
                  <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                  <input
                    value={busca}
                    onChange={(event) => setBusca(event.target.value)}
                    className="h-10 w-full rounded-md border bg-card pl-10 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                    placeholder="Cliente, procedimento, lote, equipamento"
                  />
                </span>
              </label>

              <div className="grid grid-cols-2 gap-3 sm:w-[420px]">
                <FiltroSelect label="Categoria" value={categoria} onChange={setCategoria} options={categoriasBusca} />
                <FiltroSelect label="Status" value={status} onChange={setStatus} options={statusBusca} />
              </div>
            </div>

            <div className="mt-4 grid gap-3">
              {buscaQuery.isFetching ? (
                <div className="flex h-28 items-center justify-center rounded-lg border bg-card text-sm text-muted-foreground">
                  <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
                  Buscando
                </div>
              ) : resultados.length === 0 ? (
                <div className="flex h-28 items-center justify-center rounded-lg border bg-card text-sm font-medium text-muted-foreground">
                  Nenhum resultado operacional
                </div>
              ) : (
                resultados.map((resultado) => <ResultadoBuscaCard key={`${resultado.tipo}-${resultado.id}`} resultado={resultado} />)
              )}
            </div>
          </section>

          <section id="admin" className="border-t py-5">
            <div className="mb-4 flex items-center gap-3">
              <span className="flex h-10 w-10 items-center justify-center rounded-md bg-secondary text-secondary-foreground">
                <PackageCheck className="h-5 w-5" />
              </span>
              <div>
                <p className="text-sm font-medium text-primary">Admin SaaS</p>
                <h2 className="text-xl font-semibold text-foreground">Planos e limites</h2>
              </div>
            </div>
            <AdminPlanosView />
          </section>
        </section>
      </div>
    </main>
  );
}

type Icone = typeof LayoutDashboard;

function LinkPainel({ href, icon: Icon, label }: { href: string; icon: Icone; label: string }) {
  return (
    <a
      href={href}
      className="flex h-10 items-center gap-3 rounded-md px-3 text-sm font-medium text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
    >
      <Icon className="h-4 w-4" />
      {label}
    </a>
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
    <article className="rounded-lg border bg-card p-4">
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
    <div className="flex items-center justify-between gap-3 rounded-md border bg-card px-3 py-2">
      <span className="text-muted-foreground">{label}</span>
      <span className="min-w-0 truncate font-semibold text-card-foreground">{value}</span>
    </div>
  );
}

function FiltroSelect({
  label,
  value,
  onChange,
  options
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: string[];
}) {
  return (
    <label className="grid gap-1 text-sm font-medium text-card-foreground">
      {label}
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="h-10 rounded-md border bg-card px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
      >
        {options.map((option) => (
          <option key={option || "todos"} value={option}>
            {option || "Todos"}
          </option>
        ))}
      </select>
    </label>
  );
}

function ResultadoBuscaCard({ resultado }: { resultado: ResultadoBuscaGlobal }) {
  return (
    <article className="rounded-lg border bg-card p-4">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="text-base font-semibold text-card-foreground">{resultado.titulo}</h3>
            <span className="rounded-md bg-primary/10 px-2 py-1 text-xs font-semibold text-primary">
              {rotuloTipo(resultado.tipo)}
            </span>
          </div>
          <p className="mt-2 line-clamp-2 text-sm text-muted-foreground">{resultado.descricao || resultado.categoria}</p>
        </div>
        <div className="flex shrink-0 flex-wrap gap-2 text-xs font-semibold">
          <span className="rounded-md border px-2 py-1 text-muted-foreground">{resultado.categoria}</span>
          <span className="rounded-md border px-2 py-1 text-muted-foreground">{resultado.status}</span>
        </div>
      </div>
    </article>
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

function normalizarPerfil(perfil?: string) {
  return perfil?.replace("_", " ") ?? "Acesso";
}

function rotuloTipo(tipo: ResultadoBuscaGlobal["tipo"]) {
  return {
    CLIENTE_PACIENTE: "Cliente",
    COMPROMISSO_AGENDA: "Agenda",
    SERVICO_PROCEDIMENTO: "Servico",
    CUSTO: "Custo",
    PRODUTO_ESTOQUE: "Estoque",
    EQUIPAMENTO: "Equipamento"
  }[tipo];
}
