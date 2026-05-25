"use client";

import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  AlertTriangle,
  Apple,
  CalendarDays,
  ChevronRight,
  ClipboardList,
  FileText,
  Gauge,
  LoaderCircle,
  Sparkles,
  Stethoscope,
  Users
} from "lucide-react";

import { consultarVisaoNutriPro, type AtalhoNutriPro, type IndicadorNutriPro, type PacienteNutriResumo } from "@/features/nutri-pro/api/nutri-pro-client";
import { cn } from "@/lib/utils";

type NutriProOperacionalViewProps = {
  empresaId: string;
};

type Icone = typeof Apple;

const iconesIndicadores: Record<string, Icone> = {
  pacientes: Users,
  agendaHoje: CalendarDays,
  agenda7Dias: CalendarDays,
  servicos: Stethoscope,
  documentos: FileText,
  precificacao: Gauge,
  alertas: AlertTriangle,
  planos: Apple
};

const iconesAtalhos: Record<string, Icone> = {
  "gasto-energetico": Gauge,
  "exames-laboratoriais": ClipboardList,
  "plano-alimentar": Apple,
  prontuario: Users,
  avaliacao: Stethoscope,
  documentos: FileText
};

export function NutriProOperacionalView({ empresaId }: NutriProOperacionalViewProps) {
  const visaoQuery = useQuery({
    queryKey: ["nutri-pro-visao", empresaId],
    queryFn: () => consultarVisaoNutriPro(empresaId),
    enabled: Boolean(empresaId)
  });

  const indicadoresPrincipais = useMemo(
    () => (visaoQuery.data?.indicadores ?? []).filter((indicador) => ["pacientes", "agendaHoje", "agenda7Dias", "precificacao"].includes(indicador.codigo)),
    [visaoQuery.data]
  );

  const indicadoresApoio = useMemo(
    () => (visaoQuery.data?.indicadores ?? []).filter((indicador) => !["pacientes", "agendaHoje", "agenda7Dias", "precificacao"].includes(indicador.codigo)),
    [visaoQuery.data]
  );

  if (!empresaId) {
    return <EstadoNutriPro titulo="Selecione uma empresa" descricao="Escolha uma empresa para carregar a área operacional do Nutri Pro." />;
  }

  if (visaoQuery.isLoading) {
    return (
      <section className="rounded-lg border border-emerald-200 bg-emerald-50/45 p-4">
        <div className="flex min-h-44 items-center justify-center text-sm font-medium text-emerald-800">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando Nutri Pro
        </div>
      </section>
    );
  }

  if (visaoQuery.isError || !visaoQuery.data) {
    return <EstadoNutriPro titulo="Não foi possível carregar o Nutri Pro" descricao="Confira a sessão atual e tente novamente." alerta />;
  }

  const visao = visaoQuery.data;

  return (
    <section className="grid gap-4 rounded-lg border border-emerald-200 bg-emerald-50/45 p-4">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
        <div className="min-w-0">
          <p className="text-sm font-semibold text-emerald-800">Nutri Pro operacional</p>
          <h4 className="mt-1 text-xl font-semibold text-card-foreground">{visao.empresaNome}</h4>
          <p className="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">{visao.mensagemStatus}</p>
        </div>
        <span className={cn("inline-flex w-fit items-center gap-2 rounded-md border px-3 py-2 text-xs font-semibold", visao.statusOperacional === "OPERACIONAL" ? "border-emerald-200 bg-white text-emerald-800" : "border-amber-200 bg-amber-50 text-amber-800")}>
          <Sparkles className="h-4 w-4" />
          {visao.statusOperacionalRotulo}
        </span>
      </div>

      <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        {indicadoresPrincipais.map((indicador) => (
          <CardIndicadorNutri key={indicador.codigo} indicador={indicador} />
        ))}
      </div>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_360px]">
        <section className="grid gap-4">
          <div className="rounded-lg border bg-white p-4 shadow-sm">
            <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Ações prioritárias</p>
                <p className="text-sm leading-6 text-muted-foreground">Comece pelos três pontos que mais aparecem no atendimento nutricional.</p>
              </div>
              <span className="w-fit rounded-md border bg-emerald-50 px-2 py-1 text-xs font-semibold text-emerald-800">R10 em construção</span>
            </div>
            <div className="mt-4 grid gap-3 md:grid-cols-3">
              {visao.atalhosPrioritarios.map((atalho) => (
                <CardAtalhoNutri key={atalho.codigo} atalho={atalho} principal />
              ))}
            </div>
          </div>

          <div className="grid gap-3 lg:grid-cols-2">
            <GrupoNutri titulo="Indicadores de apoio" itens={indicadoresApoio} />
            <div className="rounded-lg border bg-white p-4 shadow-sm">
              <p className="text-sm font-semibold text-card-foreground">Próximas evoluções</p>
              <div className="mt-3 grid gap-2">
                {visao.proximasEvolucoes.map((atalho) => (
                  <CardAtalhoNutri key={atalho.codigo} atalho={atalho} />
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="rounded-lg border bg-white p-4 shadow-sm">
          <div className="flex items-center justify-between gap-3 border-b pb-4">
            <div>
              <p className="text-sm font-semibold text-card-foreground">Pacientes recentes</p>
              <p className="text-xs font-medium text-muted-foreground">Área NUTRI do núcleo comum</p>
            </div>
            <Users className="h-5 w-5 text-emerald-800" />
          </div>
          <div className="mt-3 grid max-h-[360px] gap-2 overflow-y-auto pr-1">
            {visao.pacientesRecentes.length ? (
              visao.pacientesRecentes.map((paciente) => <LinhaPacienteNutri key={paciente.id} paciente={paciente} />)
            ) : (
              <div className="rounded-md border bg-background p-4 text-sm text-muted-foreground">Nenhum paciente de nutrição encontrado nesta empresa.</div>
            )}
          </div>
        </section>
      </div>
    </section>
  );
}

function CardIndicadorNutri({ indicador }: { indicador: IndicadorNutriPro }) {
  const Icon = iconesIndicadores[indicador.codigo] ?? Sparkles;
  const destaque = classeStatusIndicador(indicador.status);

  return (
    <article className={cn("rounded-lg border p-4 shadow-sm", destaque)}>
      <div className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-emerald-800">
          <Icon className="h-5 w-5" />
        </span>
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{rotuloStatus(indicador.status)}</span>
      </div>
      <p className="mt-4 text-sm font-medium text-muted-foreground">{indicador.titulo}</p>
      <p className="mt-1 text-2xl font-semibold text-card-foreground">{formatarNumero(indicador.valor)}</p>
      <p className="mt-2 text-xs leading-5 text-muted-foreground">{indicador.descricao}</p>
    </article>
  );
}

function GrupoNutri({ titulo, itens }: { titulo: string; itens: IndicadorNutriPro[] }) {
  return (
    <div className="rounded-lg border bg-white p-4 shadow-sm">
      <p className="text-sm font-semibold text-card-foreground">{titulo}</p>
      <div className="mt-3 grid gap-2">
        {itens.map((indicador) => {
          const Icon = iconesIndicadores[indicador.codigo] ?? Sparkles;
          return (
            <div key={indicador.codigo} className="flex items-start justify-between gap-3 rounded-md border bg-background p-3">
              <span className="flex min-w-0 items-start gap-3">
                <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-primary/10 text-primary">
                  <Icon className="h-4 w-4" />
                </span>
                <span>
                  <span className="block text-sm font-semibold text-card-foreground">{indicador.titulo}</span>
                  <span className="mt-1 block text-xs leading-5 text-muted-foreground">{indicador.descricao}</span>
                </span>
              </span>
              <span className="shrink-0 text-lg font-semibold text-card-foreground">{formatarNumero(indicador.valor)}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function CardAtalhoNutri({ atalho, principal = false }: { atalho: AtalhoNutriPro; principal?: boolean }) {
  const Icon = iconesAtalhos[atalho.codigo] ?? Sparkles;

  return (
    <button
      type="button"
      className={cn(
        "group min-h-24 rounded-lg border bg-background p-4 text-left transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
        principal ? "border-emerald-300 bg-emerald-50/70 hover:border-emerald-500" : "hover:border-primary/45"
      )}
      aria-label={`${atalho.titulo}: ${rotuloStatusAtalho(atalho.status)}`}
    >
      <span className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-emerald-800">
          <Icon className="h-5 w-5" />
        </span>
        <ChevronRight className="h-4 w-4 text-muted-foreground transition-transform group-hover:translate-x-0.5" />
      </span>
      <span className="mt-3 block text-sm font-semibold text-card-foreground">{atalho.titulo}</span>
      <span className="mt-2 block text-xs leading-5 text-muted-foreground">{atalho.descricao}</span>
      <span className="mt-3 inline-flex rounded-md border bg-white px-2 py-1 text-xs font-semibold text-emerald-800">{rotuloStatusAtalho(atalho.status)}</span>
    </button>
  );
}

function LinhaPacienteNutri({ paciente }: { paciente: PacienteNutriResumo }) {
  return (
    <article className="rounded-md border bg-background p-3">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-card-foreground">{paciente.nome}</p>
          <p className="mt-1 text-xs font-medium text-muted-foreground">{paciente.telefone ?? "Sem telefone"}</p>
        </div>
        <span className={cn("rounded-md border px-2 py-1 text-xs font-semibold", paciente.ativo ? "bg-emerald-50 text-emerald-800" : "bg-slate-50 text-slate-700")}>
          {paciente.ativo ? "Ativo" : "Inativo"}
        </span>
      </div>
      {paciente.observacoes ? <p className="mt-2 line-clamp-2 text-xs leading-5 text-muted-foreground">{paciente.observacoes}</p> : null}
    </article>
  );
}

function EstadoNutriPro({ titulo, descricao, alerta = false }: { titulo: string; descricao: string; alerta?: boolean }) {
  return (
    <section className={cn("flex min-h-52 flex-col items-center justify-center rounded-lg border p-6 text-center", alerta ? "border-amber-200 bg-amber-50" : "border-emerald-200 bg-emerald-50")}>
      {alerta ? <AlertTriangle className="h-8 w-8 text-amber-700" /> : <Apple className="h-8 w-8 text-emerald-800" />}
      <h4 className="mt-3 text-base font-semibold text-card-foreground">{titulo}</h4>
      <p className="mt-1 max-w-md text-sm leading-6 text-muted-foreground">{descricao}</p>
    </section>
  );
}

function classeStatusIndicador(status: string) {
  if (status === "ALERTA") {
    return "border-amber-300 bg-amber-50";
  }
  if (status === "SAUDAVEL" || status === "OPERACIONAL") {
    return "border-emerald-200 bg-emerald-50";
  }
  return "border bg-background";
}

function rotuloStatus(status: string) {
  const rotulos: Record<string, string> = {
    OPERACIONAL: "Operacional",
    PREPARADO: "Preparado",
    ALERTA: "Alerta",
    SAUDAVEL: "Saudável",
    PLANEJADO: "Planejado"
  };
  return rotulos[status] ?? status;
}

function rotuloStatusAtalho(status: string) {
  const rotulos: Record<string, string> = {
    PLANEJADO_R10: "Planejado na R10",
    PROXIMA_TASK: "Próxima task"
  };
  return rotulos[status] ?? status;
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR").format(valor);
}
