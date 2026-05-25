import Link from "next/link";
import {
  ArrowLeft,
  ArrowRight,
  BadgeCheck,
  CalendarDays,
  FileCheck2,
  HeartPulse,
  LineChart,
  ShieldCheck,
  Sparkles
} from "lucide-react";
import type { VerticalPublica } from "@/features/marketing/data/verticais-publicas";

type PaginaVerticalPublicaProps = {
  vertical: VerticalPublica;
};

export function PaginaVerticalPublica({ vertical }: PaginaVerticalPublicaProps) {
  return (
    <main className="min-h-screen bg-[#f5fbf8] text-[#102524]">
      <section className="relative isolate min-h-[86svh] overflow-hidden border-b border-[#cddfda] bg-[#edf7f3]">
        <CenaVertical vertical={vertical} />
        <div className="relative mx-auto flex max-w-6xl flex-col px-4 pb-40 pt-5 sm:px-6 sm:pb-14 lg:px-8">
          <header className="flex items-center justify-between gap-4">
            <Link
              href="/"
              className="inline-flex items-center gap-2 rounded-md text-sm font-semibold text-[#123c3a] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
            >
              <ArrowLeft className="h-4 w-4" aria-hidden="true" />
              AtendePro
            </Link>
            <Link
              href="/login"
              className="inline-flex h-10 items-center justify-center gap-2 rounded-md border border-[#a8cbc4] bg-white px-4 text-sm font-semibold text-[#123c3a] shadow-sm hover:bg-[#f8fcfb] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
            >
              Entrar
              <ArrowRight className="h-4 w-4" aria-hidden="true" />
            </Link>
          </header>
          <div className="grid items-center gap-10 py-10 lg:min-h-[70svh] lg:grid-cols-[0.82fr_1fr]">
            <div className="max-w-2xl">
              <p className="inline-flex items-center gap-2 rounded-full border border-[#b9d8d1] bg-white/80 px-3 py-1 text-sm font-semibold text-[#0f766e] shadow-sm">
                <Sparkles className="h-4 w-4" aria-hidden="true" />
                {vertical.categoria}
              </p>
              <h1 className="mt-6 text-5xl font-semibold leading-[1.02] text-[#102524] sm:text-6xl lg:text-7xl">
                {vertical.nome}
              </h1>
              <p className="mt-5 max-w-xl text-lg leading-8 text-[#405a56]">{vertical.resumo}</p>
              <div className="mt-8 flex flex-col gap-3 sm:flex-row">
                <Link
                  href="/login"
                  className="inline-flex h-12 items-center justify-center gap-2 rounded-md bg-[#0f766e] px-5 text-sm font-semibold text-white shadow-lg shadow-[#0f766e]/20 transition hover:bg-[#0d625c] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
                >
                  Ver demonstração
                  <ArrowRight className="h-4 w-4" aria-hidden="true" />
                </Link>
                <Link
                  href="/app"
                  className="inline-flex h-12 items-center justify-center gap-2 rounded-md border border-[#a8cbc4] bg-white/90 px-5 text-sm font-semibold text-[#123c3a] shadow-sm transition hover:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
                >
                  Abrir painel local
                  <BadgeCheck className="h-4 w-4" aria-hidden="true" />
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>
      <section className="bg-white">
        <div className="mx-auto grid max-w-6xl gap-8 px-4 py-12 sm:px-6 lg:grid-cols-[0.9fr_1.1fr] lg:px-8">
          <div>
            <p className="text-sm font-semibold uppercase text-[#0f766e]">{vertical.conselho}</p>
            <h2 className="mt-3 text-3xl font-semibold text-[#102524]">Fluxo preparado para operação profissional.</h2>
            <p className="mt-4 text-base leading-7 text-[#4b625e]">{vertical.destaque}</p>
          </div>
          <div className="grid gap-3 sm:grid-cols-2">
            {vertical.capacidades.map((capacidade) => (
              <article key={capacidade} className="flex items-start gap-3 rounded-lg border border-[#d7e5e1] bg-[#fbfdfc] p-4 shadow-sm">
                <ShieldCheck className="mt-0.5 h-5 w-5 text-[#0f766e]" aria-hidden="true" />
                <p className="text-sm font-medium leading-6 text-[#203a36]">{capacidade}</p>
              </article>
            ))}
          </div>
        </div>
      </section>
      <section className="bg-[#eef7f3]">
        <div className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="text-sm font-semibold uppercase text-[#0f766e]">Documentos e jornada</p>
              <h2 className="mt-3 text-3xl font-semibold text-[#102524]">Da entrada ao histórico.</h2>
            </div>
          </div>
          <div className="mt-7 grid gap-4 lg:grid-cols-[1fr_1fr]">
            <article className="rounded-lg border border-[#d2e3df] bg-white p-5 shadow-sm">
              <FileCheck2 className="h-5 w-5 text-[#0f766e]" aria-hidden="true" />
              <h3 className="mt-4 text-lg font-semibold text-[#102524]">Documentos previstos</h3>
              <div className="mt-4 grid gap-2 sm:grid-cols-2">
                {vertical.documentos.map((documento) => (
                  <span key={documento} className="rounded-md bg-[#f0f8f4] px-3 py-2 text-sm font-medium text-[#21423d]">
                    {documento}
                  </span>
                ))}
              </div>
            </article>
            <article className="rounded-lg border border-[#d2e3df] bg-white p-5 shadow-sm">
              <CalendarDays className="h-5 w-5 text-[#0f766e]" aria-hidden="true" />
              <h3 className="mt-4 text-lg font-semibold text-[#102524]">Jornada operacional</h3>
              <div className="mt-4 flex flex-wrap gap-2">
                {vertical.jornada.map((etapa, index) => (
                  <span key={etapa} className="rounded-full border border-[#c9ddd7] bg-[#f8fcfa] px-3 py-2 text-sm font-medium text-[#244641]">
                    {index + 1}. {etapa}
                  </span>
                ))}
              </div>
            </article>
          </div>
        </div>
      </section>
    </main>
  );
}

function CenaVertical({ vertical }: PaginaVerticalPublicaProps) {
  return (
    <div className="absolute inset-0" aria-hidden="true">
      <div className="absolute inset-x-0 bottom-0 h-28 bg-[#edf7f3]" />
      <div className="absolute inset-x-4 bottom-5 rounded-lg border border-[#bcd6d0] bg-white/90 p-3 shadow-xl shadow-[#0f2f2b]/15 sm:hidden">
        <div className="flex items-center justify-between gap-3">
          <span className="text-xs font-semibold uppercase text-[#0f766e]">{vertical.cenario}</span>
          <span className="rounded-full bg-[#dff5ed] px-2 py-1 text-xs font-semibold text-[#0f5f59]">Demo</span>
        </div>
        <div className="mt-3 grid grid-cols-3 gap-2">
          {vertical.metricas.map((metrica) => (
            <div key={metrica.rotulo} className="rounded-md border border-[#d7e5e1] bg-[#f9fcfb] p-2">
              <p className="text-[11px] text-[#6a7f7a]">{metrica.rotulo}</p>
              <p className="mt-1 text-lg font-semibold text-[#102524]">{metrica.valor}</p>
            </div>
          ))}
        </div>
      </div>
      <div className="absolute right-[-1rem] top-20 hidden w-[54rem] rotate-[-3deg] rounded-lg border border-[#bcd6d0] bg-white/90 p-4 shadow-2xl shadow-[#0f2f2b]/20 backdrop-blur sm:block">
        <div className="grid gap-4 lg:grid-cols-[1fr_0.74fr]">
          <div className="rounded-lg border border-[#d5e5e0] bg-[#f9fcfb] p-4">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold uppercase text-[#0f766e]">{vertical.cenario}</span>
              <span className="rounded-full bg-[#dff5ed] px-3 py-1 text-xs font-semibold text-[#0f5f59]">Operacional</span>
            </div>
            <div className="mt-5 grid grid-cols-3 gap-3">
              {vertical.metricas.map((metrica) => (
                <div key={metrica.rotulo} className="rounded-md border border-[#d7e5e1] bg-white p-3">
                  <p className="text-xs text-[#6a7f7a]">{metrica.rotulo}</p>
                  <p className="mt-2 text-2xl font-semibold text-[#102524]">{metrica.valor}</p>
                </div>
              ))}
            </div>
            <div className="mt-5 space-y-3">
              {vertical.jornada.slice(0, 4).map((etapa, index) => (
                <div key={etapa} className="flex items-center justify-between rounded-md border border-[#d8e7e2] bg-white px-4 py-3">
                  <p className="text-sm font-semibold text-[#153530]">{etapa}</p>
                  <span className="rounded-full bg-[#e0f4ec] px-3 py-1 text-xs font-semibold text-[#0f6b55]">
                    Etapa {index + 1}
                  </span>
                </div>
              ))}
            </div>
          </div>
          <div className="space-y-4">
            <div className="rounded-lg border border-[#d5e5e0] bg-[#123c3a] p-4 text-white">
              <LineChart className="h-5 w-5 text-[#85e0cd]" aria-hidden="true" />
              <p className="mt-4 text-sm text-[#b9ded6]">Indicador central</p>
              <p className="mt-2 text-3xl font-semibold">{vertical.metricas[0]?.valor}</p>
              <p className="mt-2 text-xs text-[#cce8e1]">{vertical.destaque}</p>
            </div>
            <div className="rounded-lg border border-[#d5e5e0] bg-white p-4">
              <p className="text-xs font-semibold uppercase text-[#0f766e]">Documentos</p>
              <div className="mt-4 space-y-2">
                {vertical.documentos.slice(0, 3).map((documento) => (
                  <div key={documento} className="flex items-center gap-2 rounded-md bg-[#f1f8f5] px-3 py-2 text-sm text-[#1f3c38]">
                    <FileCheck2 className="h-4 w-4 text-[#0f766e]" aria-hidden="true" />
                    {documento}
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
