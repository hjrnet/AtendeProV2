import Link from "next/link";
import {
  ArrowRight,
  BadgeCheck,
  CalendarDays,
  ClipboardList,
  FileCheck2,
  HeartPulse,
  LineChart,
  LockKeyhole,
  Sparkles,
  Stethoscope,
  WalletCards
} from "lucide-react";
import { verticaisPublicas } from "@/features/marketing/data/verticais-publicas";

const dores = [
  {
    titulo: "Rotina espalhada",
    descricao: "Agenda, prontuário, custos e documentos ficam em ferramentas diferentes.",
    icon: CalendarDays
  },
  {
    titulo: "Preço sem clareza",
    descricao: "Procedimentos são vendidos sem enxergar custo real, margem e risco de prejuízo.",
    icon: WalletCards
  },
  {
    titulo: "Operação pouco profissional",
    descricao: "A apresentação para o cliente não acompanha a qualidade do atendimento.",
    icon: ClipboardList
  }
];

const solucoes = [
  "Agenda e clientes no núcleo comum",
  "Precificação com custo real e alertas",
  "Documentos com carimbo profissional",
  "Verticais para Nutri Pro e Beauty Pro",
  "Admin SaaS com planos e permissões",
  "Base preparada para app do paciente"
];

const indicadores = [
  { rotulo: "Simulações", valor: "15", detalhe: "cenários demo com margem e alerta" },
  { rotulo: "Verticais", valor: "5", detalhe: "áreas profissionais conectadas" },
  { rotulo: "Núcleo", valor: "360°", detalhe: "operação, custos e documentos" }
];

export function LandingPagePublica() {
  return (
    <main className="min-h-screen bg-[#f5fbf8] text-[#112926]">
      <HeroSection />
      <section id="problema" className="border-y border-[#cddfda] bg-white">
        <div className="mx-auto grid max-w-6xl gap-5 px-4 py-10 sm:px-6 lg:grid-cols-3 lg:px-8">
          {dores.map((dor) => {
            const Icon = dor.icon;

            return (
              <article key={dor.titulo} className="rounded-lg border border-[#d7e5e1] bg-[#fbfdfc] p-5 shadow-sm">
                <Icon className="h-5 w-5 text-[#0f766e]" aria-hidden="true" />
                <h2 className="mt-4 text-lg font-semibold text-[#102524]">{dor.titulo}</h2>
                <p className="mt-2 text-sm leading-6 text-[#536b67]">{dor.descricao}</p>
              </article>
            );
          })}
        </div>
      </section>
      <section id="solucao" className="bg-[#eef7f3]">
        <div className="mx-auto grid max-w-6xl gap-8 px-4 py-12 sm:px-6 lg:grid-cols-[0.9fr_1.1fr] lg:px-8">
          <div>
            <p className="text-sm font-semibold uppercase text-[#0f766e]">Solução operacional</p>
            <h2 className="mt-3 text-3xl font-semibold text-[#102524] sm:text-4xl">
              Um SaaS para a rotina real de clínicas, consultórios e estúdios.
            </h2>
            <p className="mt-4 text-base leading-7 text-[#4b625e]">
              O AtendePro conecta atendimento, gestão e apresentação profissional em uma operação
              multiempresa pronta para crescer por módulos.
            </p>
          </div>
          <div className="grid gap-3 sm:grid-cols-2">
            {solucoes.map((item) => (
              <article key={item} className="flex items-start gap-3 rounded-lg border border-[#cfe2dd] bg-white p-4 shadow-sm">
                <BadgeCheck className="mt-0.5 h-5 w-5 text-[#0f766e]" aria-hidden="true" />
                <p className="text-sm font-medium leading-6 text-[#203a36]">{item}</p>
              </article>
            ))}
          </div>
        </div>
      </section>
      <section className="bg-white">
        <div className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="flex flex-col gap-5 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="text-sm font-semibold uppercase text-[#0f766e]">Verticais conectadas</p>
              <h2 className="mt-3 text-3xl font-semibold text-[#102524]">Núcleo comum, operação especializada.</h2>
            </div>
            <Link
              href="/login"
              className="inline-flex h-11 items-center justify-center gap-2 rounded-md bg-[#123c3a] px-5 text-sm font-semibold text-white shadow-sm transition hover:bg-[#0f302f] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
            >
              Entrar na demonstração
              <ArrowRight className="h-4 w-4" aria-hidden="true" />
            </Link>
          </div>
          <div className="mt-7 grid gap-3 sm:grid-cols-2 lg:grid-cols-5">
            {verticaisPublicas.map((vertical) => (
              <Link
                key={vertical.slug}
                href={`/verticais/${vertical.slug}`}
                className="rounded-lg border border-[#d6e5e0] bg-[#f8fcfa] p-4 transition hover:-translate-y-0.5 hover:border-[#9fc8bf] hover:bg-white hover:shadow-md focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
              >
                <Stethoscope className="h-5 w-5 text-[#0f766e]" aria-hidden="true" />
                <h3 className="mt-4 text-base font-semibold text-[#14302d]">{vertical.nome}</h3>
                <p className="mt-2 text-sm leading-6 text-[#5a706c]">{vertical.categoria}</p>
              </Link>
            ))}
          </div>
        </div>
      </section>
      <section className="border-y border-[#d5e5e0] bg-[#102524] text-white">
        <div className="mx-auto grid max-w-6xl gap-4 px-4 py-9 sm:grid-cols-3 sm:px-6 lg:px-8">
          {indicadores.map((indicador) => (
            <article key={indicador.rotulo} className="rounded-lg border border-white/15 bg-white/[0.06] p-5">
              <p className="text-sm text-[#a9d5cc]">{indicador.rotulo}</p>
              <p className="mt-2 text-3xl font-semibold">{indicador.valor}</p>
              <p className="mt-2 text-sm leading-6 text-[#cfe5df]">{indicador.detalhe}</p>
            </article>
          ))}
        </div>
      </section>
      <section className="bg-[#f5fbf8]">
        <div className="mx-auto flex max-w-6xl flex-col gap-5 px-4 py-12 sm:px-6 lg:flex-row lg:items-center lg:justify-between lg:px-8">
          <div>
            <p className="text-sm font-semibold uppercase text-[#0f766e]">AtendePro</p>
            <h2 className="mt-3 text-3xl font-semibold text-[#102524]">Comece pela operação. Cresça por módulos.</h2>
            <p className="mt-3 max-w-2xl text-base leading-7 text-[#536b67]">
              A demonstração local já mostra núcleo operacional, Nutri Pro, Beauty Pro, precificação e dados fictícios
              para avaliação profissional.
            </p>
          </div>
          <Link
            href="/contato"
            className="inline-flex h-11 items-center justify-center gap-2 rounded-md bg-[#0f766e] px-5 text-sm font-semibold text-white shadow-sm transition hover:bg-[#0d625c] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
          >
            Solicitar contato
            <ArrowRight className="h-4 w-4" aria-hidden="true" />
          </Link>
        </div>
      </section>
    </main>
  );
}

function HeroSection() {
  return (
    <section className="relative isolate overflow-hidden border-b border-[#cddfda] bg-[#edf7f3]">
      <div className="mx-auto flex max-w-6xl flex-col px-4 pb-8 pt-5 sm:px-6 sm:pb-10 lg:min-h-[min(680px,74svh)] lg:px-8">
        <header className="flex items-center justify-between gap-4">
          <Link href="/" className="flex items-center gap-3 rounded-md focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]">
            <span className="flex h-10 w-10 items-center justify-center rounded-md bg-[#0f766e] text-white">
              <HeartPulse className="h-5 w-5" aria-hidden="true" />
            </span>
            <span className="text-lg font-semibold text-[#102524]">AtendePro</span>
          </Link>
          <nav className="hidden items-center gap-2 sm:flex" aria-label="Navegação pública">
            <a href="#problema" className="rounded-md px-3 py-2 text-sm font-medium text-[#34504c] hover:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]">
              Problema
            </a>
            <a href="#solucao" className="rounded-md px-3 py-2 text-sm font-medium text-[#34504c] hover:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]">
              Solução
            </a>
            <Link href="/planos" className="rounded-md px-3 py-2 text-sm font-medium text-[#34504c] hover:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]">
              Planos
            </Link>
            <Link
              href="/calculadora-preco-ideal"
              className="rounded-md px-3 py-2 text-sm font-medium text-[#34504c] hover:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
            >
              Calculadora
            </Link>
            <Link href="/contato" className="rounded-md px-3 py-2 text-sm font-medium text-[#34504c] hover:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]">
              Contato
            </Link>
            <Link
              href="/login"
              className="inline-flex h-10 items-center justify-center gap-2 rounded-md border border-[#a8cbc4] bg-white px-4 text-sm font-semibold text-[#123c3a] shadow-sm hover:bg-[#f8fcfb] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
            >
              Entrar
              <LockKeyhole className="h-4 w-4" aria-hidden="true" />
            </Link>
          </nav>
        </header>
        <div className="grid flex-1 items-center gap-8 py-7 sm:py-9 lg:py-10 xl:grid-cols-[0.76fr_1fr] xl:gap-10">
          <div className="min-w-0 max-w-2xl">
            <p className="inline-flex max-w-full items-start gap-2 rounded-full border border-[#b9d8d1] bg-white/80 px-3 py-1 text-xs font-semibold leading-5 text-[#0f766e] shadow-sm sm:items-center sm:text-sm">
              <Sparkles className="mt-0.5 h-4 w-4 shrink-0 sm:mt-0" aria-hidden="true" />
              <span className="min-w-0 whitespace-normal">
                SaaS multiárea para saúde, estética e espaços profissionais
              </span>
            </p>
            <h1 className="mt-6 text-5xl font-semibold leading-[1.02] text-[#102524] sm:text-6xl">
              AtendePro
            </h1>
            <p className="mt-5 max-w-xl text-lg leading-8 text-[#405a56]">
              Um SaaS profissional para organizar atendimento, pacientes, agenda, documentos, custos e preço real
              em uma operação clara, premium e pronta para crescer.
            </p>
            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <Link
                href="/login"
                className="inline-flex h-12 items-center justify-center gap-2 rounded-md bg-[#0f766e] px-5 text-sm font-semibold text-white shadow-lg shadow-[#0f766e]/20 transition hover:bg-[#0d625c] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
              >
                Acessar demonstração
                <ArrowRight className="h-4 w-4" aria-hidden="true" />
              </Link>
              <Link
                href="/app"
                className="inline-flex h-12 items-center justify-center gap-2 rounded-md border border-[#a8cbc4] bg-white/90 px-5 text-sm font-semibold text-[#123c3a] shadow-sm transition hover:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
              >
                Ver painel local
                <BadgeCheck className="h-4 w-4" aria-hidden="true" />
              </Link>
            </div>
          </div>
          <HeroProductScene />
        </div>
      </div>
    </section>
  );
}

function HeroProductScene() {
  return (
    <div className="w-full min-w-0 justify-self-center xl:justify-self-end" aria-label="Prévia visual do painel AtendePro">
      <div className="mx-auto max-w-3xl rounded-lg border border-[#bcd6d0] bg-white/95 p-3 shadow-2xl shadow-[#0f2f2b]/15 backdrop-blur sm:p-4">
        <div className="grid min-w-0 gap-4 md:grid-cols-[1fr_0.78fr]">
          <div className="min-w-0 rounded-lg border border-[#d5e5e0] bg-[#f9fcfb] p-4">
            <div className="flex flex-wrap items-center justify-between gap-3">
              <span className="text-xs font-semibold uppercase text-[#0f766e]">Painel operacional</span>
              <span className="rounded-full bg-[#dff5ed] px-3 py-1 text-xs font-semibold text-[#0f5f59]">Online</span>
            </div>
            <div className="mt-5 grid gap-3 sm:grid-cols-3">
              {["Agenda hoje", "Clientes ativos", "Alertas"].map((item, index) => (
                <div key={item} className="rounded-md border border-[#d7e5e1] bg-white p-3">
                  <p className="text-xs text-[#6a7f7a]">{item}</p>
                  <p className="mt-2 text-2xl font-semibold text-[#102524]">{index === 0 ? "12" : index === 1 ? "86" : "3"}</p>
                </div>
              ))}
            </div>
            <div className="mt-5 space-y-3">
              {["Consulta nutricional", "Limpeza de pele premium", "Sala estética por hora"].map((item, index) => (
                <div key={item} className="flex flex-col gap-3 rounded-md border border-[#d8e7e2] bg-white px-4 py-3 sm:flex-row sm:items-center sm:justify-between">
                  <div className="min-w-0">
                    <p className="text-sm font-semibold text-[#153530]">{item}</p>
                    <p className="text-xs text-[#667b77]">{index === 1 ? "Margem baixa detectada" : "Operação saudável"}</p>
                  </div>
                  <span
                    className={
                      index === 1
                        ? "w-fit rounded-full bg-[#fff3cd] px-3 py-1 text-xs font-semibold text-[#8a5a00]"
                        : "w-fit rounded-full bg-[#e0f4ec] px-3 py-1 text-xs font-semibold text-[#0f6b55]"
                    }
                  >
                    {index === 1 ? "Atenção" : "Saudável"}
                  </span>
                </div>
              ))}
            </div>
          </div>
          <div className="grid min-w-0 gap-4 sm:grid-cols-2 md:grid-cols-1">
            <div className="rounded-lg border border-[#d5e5e0] bg-[#123c3a] p-4 text-white">
              <LineChart className="h-5 w-5 text-[#85e0cd]" aria-hidden="true" />
              <p className="mt-4 text-sm text-[#b9ded6]">Preço recomendado</p>
              <p className="mt-2 text-3xl font-semibold">R$ 157,14</p>
              <p className="mt-2 text-xs text-[#cce8e1]">Cálculo com custo real e margem desejada.</p>
            </div>
            <div className="rounded-lg border border-[#d5e5e0] bg-white p-4">
              <p className="text-xs font-semibold uppercase text-[#0f766e]">Documentos</p>
              <div className="mt-4 space-y-2">
                {["Plano alimentar", "Termo Beauty", "Solicitação de exames"].map((item) => (
                  <div key={item} className="flex items-center gap-2 rounded-md bg-[#f1f8f5] px-3 py-2 text-sm text-[#1f3c38]">
                    <FileCheck2 className="h-4 w-4 shrink-0 text-[#0f766e]" aria-hidden="true" />
                    <span className="min-w-0">{item}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
