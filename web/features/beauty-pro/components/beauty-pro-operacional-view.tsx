"use client";

import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  AlertTriangle,
  CalendarDays,
  ClipboardList,
  FileText,
  Gauge,
  LoaderCircle,
  PackageCheck,
  Scissors,
  Sparkles,
  UserRoundCheck,
  Wrench
} from "lucide-react";

import {
  consultarVisaoBeautyPro,
  type AtalhoBeautyPro,
  type ClienteBeautyResumo,
  type IndicadorBeautyPro
} from "@/features/beauty-pro/api/beauty-pro-client";
import { cn } from "@/lib/utils";

type Icone = typeof Scissors;

const iconesIndicadores: Record<string, Icone> = {
  clientes: UserRoundCheck,
  agendaHoje: CalendarDays,
  agenda7Dias: CalendarDays,
  servicos: Scissors,
  produtos: PackageCheck,
  equipamentos: Wrench,
  precificacao: Gauge,
  alertas: AlertTriangle
};

const iconesAtalhos: Record<string, Icone> = {
  "ficha-estetica": ClipboardList,
  protocolos: Sparkles,
  termos: FileText,
  produtos: PackageCheck,
  "fotos-placeholder": UserRoundCheck,
  dashboard: Gauge
};

export function BeautyProOperacionalView({ empresaId }: { empresaId: string }) {
  const visaoQuery = useQuery({
    queryKey: ["beauty-pro-visao", empresaId],
    queryFn: () => consultarVisaoBeautyPro(empresaId),
    enabled: Boolean(empresaId)
  });

  const indicadoresPrincipais = useMemo(
    () => (visaoQuery.data?.indicadores ?? []).filter((indicador) => ["clientes", "agendaHoje", "agenda7Dias", "precificacao"].includes(indicador.codigo)),
    [visaoQuery.data]
  );

  const indicadoresApoio = useMemo(
    () => (visaoQuery.data?.indicadores ?? []).filter((indicador) => !["clientes", "agendaHoje", "agenda7Dias", "precificacao"].includes(indicador.codigo)),
    [visaoQuery.data]
  );

  if (!empresaId) {
    return <EstadoBeautyPro titulo="Selecione uma empresa" descricao="Escolha uma empresa para carregar a área operacional do Beauty Pro." />;
  }

  if (visaoQuery.isLoading) {
    return (
      <section className="rounded-lg border border-rose-200 bg-rose-50/45 p-4">
        <div className="flex min-h-44 items-center justify-center text-sm font-medium text-rose-800">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando Beauty Pro
        </div>
      </section>
    );
  }

  if (visaoQuery.isError || !visaoQuery.data) {
    return <EstadoBeautyPro titulo="Não foi possível carregar o Beauty Pro" descricao="Confira a sessão atual e tente novamente." alerta />;
  }

  const visao = visaoQuery.data;

  return (
    <section className="grid gap-4 rounded-lg border border-rose-200 bg-rose-50/45 p-4">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
        <div className="min-w-0">
          <p className="text-sm font-semibold text-rose-900">Beauty Pro operacional</p>
          <h4 className="mt-1 text-xl font-semibold text-card-foreground">{visao.empresaNome}</h4>
          <p className="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">{visao.mensagemStatus}</p>
        </div>
        <span className={cn("inline-flex w-fit items-center gap-2 rounded-md border px-3 py-2 text-xs font-semibold", visao.statusOperacional === "OPERACIONAL" ? "border-rose-200 bg-white text-rose-900" : "border-amber-200 bg-amber-50 text-amber-800")}>
          <Sparkles className="h-4 w-4" />
          {visao.statusOperacionalRotulo}
        </span>
      </div>

      <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        {indicadoresPrincipais.map((indicador) => (
          <CardIndicadorBeauty key={indicador.codigo} indicador={indicador} />
        ))}
      </div>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_360px]">
        <section className="grid gap-4">
          <div className="rounded-lg border bg-white p-4 shadow-sm">
            <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Contratos operacionais preparados</p>
                <p className="text-sm leading-6 text-muted-foreground">A base da vertical já aponta os próximos fluxos reais: ficha, protocolos, sessões e termos.</p>
              </div>
              <span className="w-fit rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">R10 em construção</span>
            </div>
            <div className="mt-4 grid gap-3 md:grid-cols-3">
              {visao.atalhosPrioritarios.map((atalho) => (
                <CardAtalhoBeauty key={atalho.codigo} atalho={atalho} principal />
              ))}
            </div>
          </div>

          <div className="grid gap-3 lg:grid-cols-2">
            <GrupoBeauty titulo="Indicadores de apoio" itens={indicadoresApoio} />
            <div className="rounded-lg border bg-white p-4 shadow-sm">
              <p className="text-sm font-semibold text-card-foreground">Próximas evoluções</p>
              <div className="mt-3 grid gap-2">
                {visao.proximasEvolucoes.map((atalho) => (
                  <CardAtalhoBeauty key={atalho.codigo} atalho={atalho} />
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="rounded-lg border bg-white p-4 shadow-sm">
          <div className="flex items-center justify-between gap-3 border-b pb-4">
            <div>
              <p className="text-sm font-semibold text-card-foreground">Clientes Beauty</p>
              <p className="text-xs font-medium text-muted-foreground">Base recente para protocolos e retornos</p>
            </div>
            <UserRoundCheck className="h-5 w-5 text-rose-900" />
          </div>
          <div className="mt-3 grid max-h-[420px] gap-2 overflow-y-auto pr-1">
            {visao.clientesRecentes.length ? (
              visao.clientesRecentes.map((cliente) => <LinhaClienteBeauty key={cliente.id} cliente={cliente} />)
            ) : (
              <div className="rounded-md border bg-background p-4 text-sm text-muted-foreground">Nenhum cliente Beauty encontrado nesta empresa.</div>
            )}
          </div>
        </section>
      </div>
    </section>
  );
}

function CardIndicadorBeauty({ indicador }: { indicador: IndicadorBeautyPro }) {
  const Icon = iconesIndicadores[indicador.codigo] ?? Sparkles;

  return (
    <article className={cn("rounded-lg border p-4 shadow-sm", classeStatusIndicador(indicador.status))}>
      <div className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-rose-900">
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

function GrupoBeauty({ titulo, itens }: { titulo: string; itens: IndicadorBeautyPro[] }) {
  return (
    <div className="rounded-lg border bg-white p-4 shadow-sm">
      <p className="text-sm font-semibold text-card-foreground">{titulo}</p>
      <div className="mt-3 grid gap-2">
        {itens.map((indicador) => {
          const Icon = iconesIndicadores[indicador.codigo] ?? Sparkles;
          return (
            <div key={indicador.codigo} className="flex items-start justify-between gap-3 rounded-md border bg-background p-3">
              <span className="flex min-w-0 items-start gap-3">
                <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-rose-100 text-rose-900">
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

function CardAtalhoBeauty({ atalho, principal = false }: { atalho: AtalhoBeautyPro; principal?: boolean }) {
  const Icon = iconesAtalhos[atalho.codigo] ?? Sparkles;

  return (
    <article className={cn("min-h-24 rounded-lg border bg-background p-4", principal ? "border-rose-300 bg-rose-50/70" : "")}>
      <span className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-rose-900">
          <Icon className="h-5 w-5" />
        </span>
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-rose-900">{rotuloStatusAtalho(atalho.status)}</span>
      </span>
      <span className="mt-3 block text-sm font-semibold text-card-foreground">{atalho.titulo}</span>
      <span className="mt-2 block text-xs leading-5 text-muted-foreground">{atalho.descricao}</span>
    </article>
  );
}

function LinhaClienteBeauty({ cliente }: { cliente: ClienteBeautyResumo }) {
  return (
    <article className="rounded-md border bg-background p-3">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-card-foreground">{cliente.nome}</p>
          <p className="mt-1 text-xs font-medium text-muted-foreground">{cliente.telefone ?? "Sem telefone"}</p>
        </div>
        <span className={cn("rounded-md border px-2 py-1 text-xs font-semibold", cliente.ativo ? "bg-rose-50 text-rose-900" : "bg-slate-50 text-slate-700")}>
          {cliente.ativo ? "Ativo" : "Inativo"}
        </span>
      </div>
      {cliente.observacoes ? <p className="mt-2 line-clamp-2 text-xs leading-5 text-muted-foreground">{cliente.observacoes}</p> : null}
    </article>
  );
}

function EstadoBeautyPro({ titulo, descricao, alerta = false }: { titulo: string; descricao: string; alerta?: boolean }) {
  return (
    <section className={cn("flex min-h-52 flex-col items-center justify-center rounded-lg border p-6 text-center", alerta ? "border-amber-200 bg-amber-50" : "border-rose-200 bg-rose-50")}>
      {alerta ? <AlertTriangle className="h-8 w-8 text-amber-700" /> : <Scissors className="h-8 w-8 text-rose-900" />}
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
    return "border-rose-200 bg-rose-50";
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
    DISPONIVEL: "Disponível",
    PLANEJADO_R10: "Planejado na R10",
    PROXIMA_TASK: "Próxima task"
  };
  return rotulos[status] ?? status;
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR").format(valor);
}
