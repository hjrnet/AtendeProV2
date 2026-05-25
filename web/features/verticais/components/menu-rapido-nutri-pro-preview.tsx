"use client";

import {
  Apple,
  BookOpenCheck,
  ClipboardCheck,
  ClipboardList,
  FileText,
  FlaskConical,
  Gauge,
  NotebookTabs,
  Ruler,
  ShoppingBasket,
  Sparkles,
  Target
} from "lucide-react";

import { cn } from "@/lib/utils";

type IconeAcaoNutri = typeof Gauge;

type StatusAcaoNutri = "PRIORITARIA" | "PLANEJADA" | "FUTURA";

type AcaoRapidaNutri = {
  titulo: string;
  descricao: string;
  status: StatusAcaoNutri;
  destaque?: boolean;
  icon: IconeAcaoNutri;
};

const acoesAvaliacao: AcaoRapidaNutri[] = [
  {
    titulo: "Adicionar gastos energéticos",
    descricao: "Registrar TMB, GEB, GET e objetivo energético antes do plano.",
    status: "PRIORITARIA",
    destaque: true,
    icon: Gauge
  },
  {
    titulo: "Adicionar exames laboratoriais",
    descricao: "Preparar solicitação e histórico de exames no prontuário.",
    status: "PRIORITARIA",
    destaque: true,
    icon: FlaskConical
  },
  {
    titulo: "Adicionar avaliação antropométrica",
    descricao: "Peso, altura, IMC, objetivo e evolução corporal do paciente.",
    status: "PLANEJADA",
    icon: Ruler
  },
  {
    titulo: "Adicionar anamnese",
    descricao: "Organizar hábitos, rotina, sintomas, histórico e preferências.",
    status: "PLANEJADA",
    icon: ClipboardList
  },
  {
    titulo: "Adicionar questionário pré-consulta",
    descricao: "Coletar informações antes do atendimento nutricional.",
    status: "FUTURA",
    icon: ClipboardCheck
  },
  {
    titulo: "Adicionar recordatório alimentar",
    descricao: "Registrar consumo recente para apoiar a conduta profissional.",
    status: "FUTURA",
    icon: NotebookTabs
  }
];

const acoesAcompanhamento: AcaoRapidaNutri[] = [
  {
    titulo: "Adicionar plano alimentar",
    descricao: "Iniciar plano por paciente com refeições, horários e observações.",
    status: "PRIORITARIA",
    destaque: true,
    icon: Apple
  },
  {
    titulo: "Adicionar prescrições",
    descricao: "Preparar documentos de suplementação, formulações e orientações.",
    status: "PLANEJADA",
    icon: FileText
  },
  {
    titulo: "Adicionar metas",
    descricao: "Definir objetivos de acompanhamento e evolução do paciente.",
    status: "PLANEJADA",
    icon: Target
  },
  {
    titulo: "Adicionar lista de compras",
    descricao: "Gerar lista a partir do plano alimentar quando o plano existir.",
    status: "FUTURA",
    icon: ShoppingBasket
  },
  {
    titulo: "Adicionar diário alimentar",
    descricao: "Preparar registros do paciente para app/portal futuro.",
    status: "FUTURA",
    icon: BookOpenCheck
  }
];

const acoesPrioritarias = [...acoesAvaliacao, ...acoesAcompanhamento].filter((acao) => acao.destaque);

export function MenuRapidoNutriProPreview() {
  return (
    <section className="grid gap-4 rounded-lg border border-emerald-200 bg-emerald-50/45 p-4">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
        <div className="min-w-0">
          <p className="text-sm font-semibold text-emerald-800">Prontuário nutricional</p>
          <h4 className="mt-1 text-lg font-semibold text-card-foreground">Menu Rápido Nutri Pro</h4>
          <p className="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
            Recomendamos realizar anamnese, avaliação antropométrica e cálculo de gasto energético para elaborar um plano alimentar mais preciso.
          </p>
        </div>
        <span className="inline-flex w-fit items-center gap-2 rounded-md border border-emerald-200 bg-white px-3 py-2 text-xs font-semibold text-emerald-800">
          <Sparkles className="h-4 w-4" />
          Prévia de experiência
        </span>
      </div>

      <div className="grid gap-3 md:grid-cols-3">
        {acoesPrioritarias.map((acao) => (
          <CardAcaoPrincipal key={acao.titulo} acao={acao} />
        ))}
      </div>

      <div className="grid gap-3 lg:grid-cols-2">
        <GrupoAcoes titulo="Avaliação" acoes={acoesAvaliacao.filter((acao) => !acao.destaque)} />
        <GrupoAcoes titulo="Prescrição e acompanhamento" acoes={acoesAcompanhamento.filter((acao) => !acao.destaque)} />
      </div>

      <p className="rounded-md border border-emerald-200 bg-white px-3 py-2 text-xs leading-5 text-muted-foreground">
        No Plano Estudante, documentos oficiais devem respeitar marca d'água acadêmica e bloqueios sem CRN. Esta prévia não cria documentos nem cálculos reais.
      </p>
    </section>
  );
}

function CardAcaoPrincipal({ acao }: { acao: AcaoRapidaNutri }) {
  const Icon = acao.icon;

  return (
    <button
      type="button"
      className="min-h-36 rounded-lg border border-emerald-300 bg-white p-4 text-left shadow-sm transition-colors hover:border-emerald-500 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
      aria-label={`${acao.titulo} planejado para o prontuário Nutri Pro`}
    >
      <span className="flex h-11 w-11 items-center justify-center rounded-md bg-emerald-100 text-emerald-800">
        <Icon className="h-5 w-5" />
      </span>
      <span className="mt-3 block text-sm font-semibold text-card-foreground">{acao.titulo}</span>
      <span className="mt-2 block text-sm leading-5 text-muted-foreground">{acao.descricao}</span>
      <StatusAcao status={acao.status} />
    </button>
  );
}

function GrupoAcoes({ titulo, acoes }: { titulo: string; acoes: AcaoRapidaNutri[] }) {
  return (
    <div className="rounded-lg border bg-white p-3">
      <p className="text-sm font-semibold text-card-foreground">{titulo}</p>
      <div className="mt-3 grid gap-2">
        {acoes.map((acao) => (
          <CardAcaoSecundaria key={acao.titulo} acao={acao} />
        ))}
      </div>
    </div>
  );
}

function CardAcaoSecundaria({ acao }: { acao: AcaoRapidaNutri }) {
  const Icon = acao.icon;

  return (
    <button
      type="button"
      className="flex min-h-16 items-start gap-3 rounded-md border bg-background p-3 text-left transition-colors hover:border-primary/45 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
      aria-label={`${acao.titulo} planejado para o prontuário Nutri Pro`}
    >
      <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-primary/10 text-primary">
        <Icon className="h-4 w-4" />
      </span>
      <span className="min-w-0 flex-1">
        <span className="block text-sm font-semibold text-card-foreground">{acao.titulo}</span>
        <span className="mt-1 block text-xs leading-5 text-muted-foreground">{acao.descricao}</span>
        <StatusAcao status={acao.status} compacto />
      </span>
    </button>
  );
}

function StatusAcao({ status, compacto = false }: { status: StatusAcaoNutri; compacto?: boolean }) {
  const config = {
    PRIORITARIA: {
      label: "Prioritária",
      className: "border-emerald-200 bg-emerald-50 text-emerald-800"
    },
    PLANEJADA: {
      label: "Planejada",
      className: "border-sky-200 bg-sky-50 text-sky-800"
    },
    FUTURA: {
      label: "Em breve",
      className: "border-slate-200 bg-slate-50 text-slate-700"
    }
  }[status];

  return (
    <span className={cn("mt-3 inline-flex w-fit rounded-md border px-2 py-1 text-xs font-semibold", compacto ? "mt-2" : "", config.className)}>
      {config.label}
    </span>
  );
}
