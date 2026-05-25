import Link from "next/link";
import { ArrowLeft, ArrowRight, BadgeCheck, Check, ShieldCheck, Sparkles, X } from "lucide-react";
import { comparativoPlanos, planosPublicos } from "@/features/marketing/data/planos-publicos";

const colunasComparativo = [
  { chave: "start", nome: "Start" },
  { chave: "care", nome: "Care" },
  { chave: "pro", nome: "Pro" },
  { chave: "business", nome: "Business" },
  { chave: "premium", nome: "Premium" }
] as const;

export function PaginaPlanosPublica() {
  return (
    <main className="min-h-screen bg-[#f5fbf8] text-[#102524]">
      <section className="relative isolate overflow-hidden border-b border-[#cddfda] bg-[#edf7f3]">
        <div className="absolute inset-0" aria-hidden="true">
          <div className="absolute right-[-2rem] top-24 hidden w-[46rem] rotate-[-2deg] rounded-lg border border-[#bcd6d0] bg-white/90 p-4 shadow-2xl shadow-[#0f2f2b]/20 backdrop-blur lg:block">
            <div className="grid grid-cols-3 gap-3">
              {planosPublicos.slice(2, 5).map((plano) => (
                <div key={plano.codigo} className="rounded-lg border border-[#d5e5e0] bg-[#f9fcfb] p-4">
                  <p className="text-xs font-semibold uppercase text-[#0f766e]">{plano.nome}</p>
                  <p className="mt-3 text-2xl font-semibold text-[#102524]">{plano.preco}</p>
                  <p className="mt-2 text-xs leading-5 text-[#607773]">Trial 30 dias</p>
                </div>
              ))}
            </div>
            <div className="mt-4 rounded-lg border border-[#d5e5e0] bg-[#123c3a] p-4 text-white">
              <p className="text-sm text-[#b9ded6]">Comparação ativa</p>
              <p className="mt-2 text-3xl font-semibold">10 planos</p>
              <p className="mt-2 text-xs text-[#cce8e1]">Entrada acessível, verticais Pro e operação para equipes.</p>
            </div>
          </div>
        </div>
        <div className="relative mx-auto max-w-6xl px-4 pb-12 pt-5 sm:px-6 lg:px-8">
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
          <div className="max-w-3xl py-12 lg:min-h-[62svh] lg:py-20">
            <p className="inline-flex items-center gap-2 rounded-full border border-[#b9d8d1] bg-white/80 px-3 py-1 text-sm font-semibold text-[#0f766e] shadow-sm">
              <Sparkles className="h-4 w-4" aria-hidden="true" />
              Trial de 30 dias nos planos comerciais
            </p>
            <h1 className="mt-6 text-5xl font-semibold leading-[1.02] text-[#102524] sm:text-6xl lg:text-7xl">
              Planos AtendePro
            </h1>
            <p className="mt-5 max-w-2xl text-lg leading-8 text-[#405a56]">
              Escolha uma entrada simples, uma vertical profissional ou uma operação completa para equipe. Os valores
              são fictícios para demonstração local.
            </p>
            <div className="mt-8 flex flex-col gap-3 sm:flex-row">
              <Link
                href="/contato"
                className="inline-flex h-12 items-center justify-center gap-2 rounded-md bg-[#0f766e] px-5 text-sm font-semibold text-white shadow-lg shadow-[#0f766e]/20 transition hover:bg-[#0d625c] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
              >
                Solicitar contato
                <ArrowRight className="h-4 w-4" aria-hidden="true" />
              </Link>
              <Link
                href="/"
                className="inline-flex h-12 items-center justify-center gap-2 rounded-md border border-[#a8cbc4] bg-white/90 px-5 text-sm font-semibold text-[#123c3a] shadow-sm transition hover:bg-white focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
              >
                Voltar ao produto
                <BadgeCheck className="h-4 w-4" aria-hidden="true" />
              </Link>
            </div>
          </div>
        </div>
      </section>
      <section className="bg-white">
        <div className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-end sm:justify-between">
            <div>
              <p className="text-sm font-semibold uppercase text-[#0f766e]">Planos comerciais</p>
              <h2 className="mt-3 text-3xl font-semibold text-[#102524]">Compare por momento de operação.</h2>
            </div>
            <p className="max-w-xl text-sm leading-6 text-[#536b67]">
              Sem checkout nesta fase: a página apresenta os planos e direciona para a demonstração local.
            </p>
          </div>
          <div className="mt-7 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {planosPublicos.map((plano) => (
              <article
                key={plano.codigo}
                className={
                  plano.recomendado
                    ? "rounded-lg border-2 border-[#0f766e] bg-[#f8fcfa] p-5 shadow-lg shadow-[#0f766e]/10"
                    : "rounded-lg border border-[#d7e5e1] bg-[#fbfdfc] p-5 shadow-sm"
                }
              >
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <h3 className="text-xl font-semibold text-[#102524]">{plano.nome}</h3>
                    <p className="mt-2 text-sm leading-6 text-[#536b67]">{plano.publico}</p>
                  </div>
                  {plano.recomendado ? (
                    <span className="rounded-full bg-[#dff5ed] px-3 py-1 text-xs font-semibold text-[#0f5f59]">Indicado</span>
                  ) : null}
                </div>
                <p className="mt-5 text-3xl font-semibold text-[#102524]">
                  {plano.preco}
                  <span className="text-sm font-medium text-[#6a7f7a]">/mês</span>
                </p>
                {plano.destaque ? <p className="mt-3 text-sm font-medium text-[#8a5a00]">{plano.destaque}</p> : null}
                <div className="mt-5 space-y-2">
                  {plano.limites.map((limite) => (
                    <p key={limite} className="flex items-center gap-2 text-sm text-[#244641]">
                      <ShieldCheck className="h-4 w-4 text-[#0f766e]" aria-hidden="true" />
                      {limite}
                    </p>
                  ))}
                </div>
                <div className="mt-5 border-t border-[#d7e5e1] pt-4">
                  <p className="text-xs font-semibold uppercase text-[#0f766e]">Inclui</p>
                  <div className="mt-3 space-y-2">
                    {plano.recursos.map((recurso) => (
                      <p key={recurso} className="flex items-start gap-2 text-sm leading-6 text-[#3d5652]">
                        <Check className="mt-1 h-4 w-4 text-[#0f766e]" aria-hidden="true" />
                        {recurso}
                      </p>
                    ))}
                  </div>
                </div>
              </article>
            ))}
          </div>
        </div>
      </section>
      <section className="bg-[#eef7f3]">
        <div className="mx-auto max-w-6xl px-4 py-12 sm:px-6 lg:px-8">
          <p className="text-sm font-semibold uppercase text-[#0f766e]">Comparativo rápido</p>
          <h2 className="mt-3 text-3xl font-semibold text-[#102524]">O que muda entre os planos.</h2>
          <div className="mt-7 overflow-x-auto rounded-lg border border-[#d2e3df] bg-white shadow-sm">
            <table className="min-w-[760px] w-full border-collapse text-left text-sm">
              <thead className="bg-[#f8fcfa] text-[#244641]">
                <tr>
                  <th className="border-b border-[#d2e3df] px-4 py-3 font-semibold">Recurso</th>
                  {colunasComparativo.map((coluna) => (
                    <th key={coluna.chave} className="border-b border-[#d2e3df] px-4 py-3 font-semibold">
                      {coluna.nome}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {comparativoPlanos.map((linha) => (
                  <tr key={linha.recurso} className="border-b border-[#e1ebe8] last:border-b-0">
                    <td className="px-4 py-3 font-medium text-[#102524]">{linha.recurso}</td>
                    {colunasComparativo.map((coluna) => {
                      const ativo = linha[coluna.chave];

                      return (
                        <td key={coluna.chave} className="px-4 py-3">
                          {ativo ? (
                            <span className="inline-flex items-center gap-1 rounded-full bg-[#dff5ed] px-2 py-1 text-xs font-semibold text-[#0f5f59]">
                              <Check className="h-3.5 w-3.5" aria-hidden="true" />
                              Sim
                            </span>
                          ) : (
                            <span className="inline-flex items-center gap-1 rounded-full bg-[#f1f5f4] px-2 py-1 text-xs font-semibold text-[#667b77]">
                              <X className="h-3.5 w-3.5" aria-hidden="true" />
                              Não
                            </span>
                          )}
                        </td>
                      );
                    })}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </main>
  );
}
