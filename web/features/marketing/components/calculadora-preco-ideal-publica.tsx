"use client";

import { useMemo, useState } from "react";
import Link from "next/link";
import { ArrowLeft, ArrowRight, BadgeAlert, BadgeCheck, Calculator, CheckCircle2, Sparkles } from "lucide-react";

type CampoCalculadora = {
  id: keyof ValoresCalculadoraPrecoIdeal;
  rotulo: string;
  prefixo?: string;
  sufixo?: string;
  min?: number;
};

type ValoresCalculadoraPrecoIdeal = {
  precoPraticado: number;
  custoInsumos: number;
  custoSala: number;
  tempoMinutos: number;
  custoHoraProfissional: number;
  deslocamento: number;
  taxas: number;
  custosFixosRateados: number;
  margemDesejada: number;
};

type StatusPrecoIdeal = "SAUDAVEL" | "MARGEM_BAIXA" | "PREJUIZO";

const valoresIniciais: ValoresCalculadoraPrecoIdeal = {
  precoPraticado: 180,
  custoInsumos: 35,
  custoSala: 25,
  tempoMinutos: 60,
  custoHoraProfissional: 60,
  deslocamento: 10,
  taxas: 8,
  custosFixosRateados: 18,
  margemDesejada: 30
};

const camposCalculadora: CampoCalculadora[] = [
  { id: "precoPraticado", rotulo: "Preço praticado", prefixo: "R$" },
  { id: "custoInsumos", rotulo: "Custo de insumos", prefixo: "R$" },
  { id: "custoSala", rotulo: "Custo de sala/recurso", prefixo: "R$" },
  { id: "tempoMinutos", rotulo: "Duração", sufixo: "min", min: 1 },
  { id: "custoHoraProfissional", rotulo: "Custo da hora profissional", prefixo: "R$" },
  { id: "deslocamento", rotulo: "Deslocamento", prefixo: "R$" },
  { id: "taxas", rotulo: "Taxas", prefixo: "R$" },
  { id: "custosFixosRateados", rotulo: "Custos fixos rateados", prefixo: "R$" },
  { id: "margemDesejada", rotulo: "Margem desejada", sufixo: "%", min: 1 }
];

const moeda = new Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL"
});

export function CalculadoraPrecoIdealPublica() {
  const [valores, setValores] = useState<ValoresCalculadoraPrecoIdeal>(valoresIniciais);

  const resultado = useMemo(() => calcularPrecoIdeal(valores), [valores]);

  function atualizarValor(campo: keyof ValoresCalculadoraPrecoIdeal, valor: string) {
    setValores((valorAtual) => ({
      ...valorAtual,
      [campo]: Number(valor.replace(",", ".")) || 0
    }));
  }

  return (
    <main className="min-h-screen bg-[#f5fbf8] text-[#102524]">
      <section className="border-b border-[#cddfda] bg-[#edf7f3]">
        <div className="mx-auto max-w-6xl px-4 pb-10 pt-5 sm:px-6 lg:px-8">
          <header className="flex items-center justify-between gap-4">
            <Link
              href="/"
              className="inline-flex items-center gap-2 rounded-md text-sm font-semibold text-[#123c3a] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
            >
              <ArrowLeft className="h-4 w-4" aria-hidden="true" />
              AtendePro
            </Link>
            <Link
              href="/planos"
              className="inline-flex h-10 items-center justify-center gap-2 rounded-md border border-[#a8cbc4] bg-white px-4 text-sm font-semibold text-[#123c3a] shadow-sm hover:bg-[#f8fcfb] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#0f766e]"
            >
              Planos
              <ArrowRight className="h-4 w-4" aria-hidden="true" />
            </Link>
          </header>
          <div className="mt-10 grid gap-6 lg:grid-cols-[0.94fr_1.06fr] lg:items-start">
            <div>
              <p className="inline-flex items-center gap-2 rounded-full border border-[#b9d8d1] bg-white/80 px-3 py-1 text-sm font-semibold text-[#0f766e] shadow-sm">
                <Sparkles className="h-4 w-4" aria-hidden="true" />
                Ferramenta gratuita
              </p>
              <h1 className="mt-5 text-4xl font-semibold leading-[1.05] text-[#102524] sm:text-5xl lg:text-6xl">
                Calculadora de Preço Ideal
              </h1>
              <p className="mt-4 max-w-2xl text-base leading-7 text-[#405a56]">
                Estime custo real, preço mínimo, preço recomendado e risco de margem em poucos campos.
              </p>
            </div>
            <PainelResultado resultado={resultado} />
          </div>
        </div>
      </section>
      <section className="bg-white">
        <div className="mx-auto grid max-w-6xl gap-6 px-4 py-10 sm:px-6 lg:grid-cols-[0.94fr_1.06fr] lg:px-8">
          <form className="rounded-lg border border-[#d7e5e1] bg-[#fbfdfc] p-5 shadow-sm">
            <div className="flex items-center gap-3">
              <span className="flex h-10 w-10 items-center justify-center rounded-md bg-[#0f766e] text-white">
                <Calculator className="h-5 w-5" aria-hidden="true" />
              </span>
              <div>
                <h2 className="text-xl font-semibold text-[#102524]">Custos do procedimento</h2>
                <p className="text-sm text-[#536b67]">Use valores aproximados para uma primeira análise.</p>
              </div>
            </div>
            <div className="mt-6 grid gap-4 sm:grid-cols-2">
              {camposCalculadora.map((campo) => (
                <label key={campo.id} className="block">
                  <span className="text-sm font-medium text-[#244641]">{campo.rotulo}</span>
                  <div className="mt-2 flex rounded-md border border-[#cfe0dc] bg-white focus-within:ring-2 focus-within:ring-[#0f766e]">
                    {campo.prefixo ? <span className="flex items-center px-3 text-sm font-medium text-[#607773]">{campo.prefixo}</span> : null}
                    <input
                      type="number"
                      min={campo.min ?? 0}
                      step="0.01"
                      value={valores[campo.id]}
                      onChange={(event) => atualizarValor(campo.id, event.target.value)}
                      className="min-h-11 w-full rounded-md bg-transparent px-3 text-sm font-semibold text-[#102524] outline-none"
                    />
                    {campo.sufixo ? <span className="flex items-center px-3 text-sm font-medium text-[#607773]">{campo.sufixo}</span> : null}
                  </div>
                </label>
              ))}
            </div>
          </form>
          <div className="space-y-4">
            <article className="rounded-lg border border-[#d7e5e1] bg-[#f8fcfa] p-5 shadow-sm">
              <h2 className="text-xl font-semibold text-[#102524]">Leitura rápida</h2>
              <div className="mt-5 grid gap-3 sm:grid-cols-2">
                <IndicadorResultado rotulo="Custo total" valor={formatarMoeda(resultado.custoTotal)} />
                <IndicadorResultado rotulo="Preço mínimo" valor={formatarMoeda(resultado.precoMinimo)} />
                <IndicadorResultado rotulo="Preço recomendado" valor={formatarMoeda(resultado.precoRecomendado)} destaque />
                <IndicadorResultado rotulo="Lucro estimado" valor={formatarMoeda(resultado.lucroEstimado)} />
              </div>
            </article>
            <article className="rounded-lg border border-[#d7e5e1] bg-white p-5 shadow-sm">
              <h2 className="text-xl font-semibold text-[#102524]">Composição do custo</h2>
              <div className="mt-4 space-y-3">
                <LinhaCusto rotulo="Tempo profissional" valor={resultado.custoTempoProfissional} />
                <LinhaCusto rotulo="Insumos, sala e recursos" valor={valores.custoInsumos + valores.custoSala} />
                <LinhaCusto rotulo="Deslocamento, taxas e fixos" valor={valores.deslocamento + valores.taxas + valores.custosFixosRateados} />
              </div>
            </article>
          </div>
        </div>
      </section>
    </main>
  );
}

function calcularPrecoIdeal(valores: ValoresCalculadoraPrecoIdeal) {
  const custoTempoProfissional = (valores.custoHoraProfissional / 60) * valores.tempoMinutos;
  const custoTotal =
    valores.custoInsumos +
    valores.custoSala +
    custoTempoProfissional +
    valores.deslocamento +
    valores.taxas +
    valores.custosFixosRateados;
  const margemDecimal = Math.min(Math.max(valores.margemDesejada, 1), 85) / 100;
  const precoMinimo = custoTotal;
  const precoRecomendado = custoTotal / (1 - margemDecimal);
  const lucroEstimado = valores.precoPraticado - custoTotal;
  const margemAtual = valores.precoPraticado > 0 ? (lucroEstimado / valores.precoPraticado) * 100 : 0;
  const status: StatusPrecoIdeal =
    valores.precoPraticado < custoTotal
      ? "PREJUIZO"
      : valores.precoPraticado < precoRecomendado
        ? "MARGEM_BAIXA"
        : "SAUDAVEL";

  return {
    custoTempoProfissional,
    custoTotal,
    precoMinimo,
    precoRecomendado,
    lucroEstimado,
    margemAtual,
    status
  };
}

function PainelResultado({ resultado }: { resultado: ReturnType<typeof calcularPrecoIdeal> }) {
  const status = {
    SAUDAVEL: {
      titulo: "Preço saudável",
      descricao: "O preço praticado cobre os custos e alcança a margem desejada.",
      classe: "border-[#b6dfcf] bg-[#f0fbf6] text-[#0f5f59]",
      icon: CheckCircle2
    },
    MARGEM_BAIXA: {
      titulo: "Margem baixa",
      descricao: "O preço cobre os custos, mas está abaixo do recomendado para a margem desejada.",
      classe: "border-[#f0d58a] bg-[#fff8df] text-[#8a5a00]",
      icon: BadgeAlert
    },
    PREJUIZO: {
      titulo: "Venda abaixo do custo",
      descricao: "O preço informado não cobre o custo total estimado.",
      classe: "border-[#f0b7a8] bg-[#fff1ed] text-[#9a3412]",
      icon: BadgeAlert
    }
  }[resultado.status];
  const Icon = status.icon;

  return (
    <article className={`rounded-lg border p-5 shadow-sm ${status.classe}`}>
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-semibold uppercase">Resultado</p>
          <h2 className="mt-2 text-2xl font-semibold">{status.titulo}</h2>
        </div>
        <Icon className="h-6 w-6" aria-hidden="true" />
      </div>
      <p className="mt-3 text-sm leading-6">{status.descricao}</p>
      <div className="mt-5 rounded-md bg-white/70 p-4">
        <p className="text-sm font-medium">Preço recomendado</p>
        <p className="mt-2 text-4xl font-semibold">{formatarMoeda(resultado.precoRecomendado)}</p>
        <p className="mt-2 text-sm">Margem atual estimada: {resultado.margemAtual.toFixed(1)}%</p>
      </div>
    </article>
  );
}

function IndicadorResultado({ rotulo, valor, destaque = false }: { rotulo: string; valor: string; destaque?: boolean }) {
  return (
    <div className={destaque ? "rounded-md border border-[#9fc8bf] bg-[#eaf8f2] p-4" : "rounded-md border border-[#d7e5e1] bg-white p-4"}>
      <p className="text-sm text-[#607773]">{rotulo}</p>
      <p className="mt-2 text-xl font-semibold text-[#102524]">{valor}</p>
    </div>
  );
}

function LinhaCusto({ rotulo, valor }: { rotulo: string; valor: number }) {
  return (
    <div className="flex items-center justify-between gap-3 rounded-md bg-[#f5fbf8] px-4 py-3">
      <span className="text-sm font-medium text-[#244641]">{rotulo}</span>
      <span className="text-sm font-semibold text-[#102524]">{formatarMoeda(valor)}</span>
    </div>
  );
}

function formatarMoeda(valor: number) {
  return moeda.format(Number.isFinite(valor) ? valor : 0);
}
