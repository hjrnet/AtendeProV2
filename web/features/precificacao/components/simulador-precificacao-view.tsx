"use client";

import type { InputHTMLAttributes } from "react";
import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { useForm, type UseFormRegisterReturn } from "react-hook-form";
import {
  AlertTriangle,
  BadgeDollarSign,
  Calculator,
  CheckCircle2,
  LoaderCircle,
  Percent,
  ReceiptText,
  TrendingUp
} from "lucide-react";

import { Button } from "@/components/ui/button";
import { ApiError } from "@/lib/api";
import {
  calcularMargemLucro,
  calcularPrecoRecomendado,
  type MargemLucroResponse,
  type PrecoRecomendadoResponse
} from "@/features/precificacao/api/precificacao-client";
import { precificacaoFormSchema, type PrecificacaoFormData } from "@/features/precificacao/lib/precificacao-form-schema";

type SimuladorPrecificacaoViewProps = {
  empresaId: string;
};

type ResultadoSimulacao = {
  recomendado: PrecoRecomendadoResponse;
  margem: MargemLucroResponse;
};

const VALORES_INICIAIS: PrecificacaoFormData = {
  nomeProcedimento: "Consulta profissional",
  duracaoMinutos: 60,
  custoInsumos: 45,
  custoSalaPorHora: 60,
  valorHoraProfissional: 120,
  custoDeslocamento: 25,
  custoAlimentacao: 15,
  taxas: 10,
  margemDesejadaPercentual: 30,
  precoVenda: 240
};

export function SimuladorPrecificacaoView({ empresaId }: SimuladorPrecificacaoViewProps) {
  const [erro, setErro] = useState<string | null>(null);
  const form = useForm<PrecificacaoFormData>({ defaultValues: VALORES_INICIAIS });

  const simulacaoMutation = useMutation({
    mutationFn: async (dados: PrecificacaoFormData): Promise<ResultadoSimulacao> => {
      const base = {
        empresaId,
        nomeProcedimento: dados.nomeProcedimento,
        duracaoMinutos: dados.duracaoMinutos,
        custoInsumos: dados.custoInsumos,
        custoSalaPorHora: dados.custoSalaPorHora,
        valorHoraProfissional: dados.valorHoraProfissional,
        custoDeslocamento: dados.custoDeslocamento,
        custoAlimentacao: dados.custoAlimentacao,
        taxas: dados.taxas
      };
      const [recomendado, margem] = await Promise.all([
        calcularPrecoRecomendado({ ...base, margemDesejadaPercentual: dados.margemDesejadaPercentual }),
        calcularMargemLucro({ ...base, precoVenda: dados.precoVenda })
      ]);
      return { recomendado, margem };
    },
    onError: (error) => setErro(error instanceof ApiError ? error.message : "Nao foi possivel simular o preco.")
  });

  const resultado = simulacaoMutation.data;

  const simular = form.handleSubmit((valores) => {
    setErro(null);
    form.clearErrors();
    const validacao = precificacaoFormSchema.safeParse(valores);

    if (!validacao.success) {
      validacao.error.issues.forEach((issue) => {
        const campo = issue.path.at(0) as keyof PrecificacaoFormData | undefined;
        if (campo) {
          form.setError(campo, { message: issue.message });
        }
      });
      return;
    }

    if (!empresaId) {
      setErro("Selecione uma empresa para simular precificacao.");
      return;
    }

    simulacaoMutation.mutate(validacao.data);
  });

  return (
    <section className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_390px]">
      <div className="rounded-lg border bg-card p-4 shadow-sm">
        <div className="mb-4 flex items-center justify-between gap-3 border-b pb-4">
          <div>
            <p className="text-sm font-medium text-primary">Precificacao</p>
            <h2 className="mt-1 text-xl font-semibold text-card-foreground">Simulador de preco</h2>
          </div>
          <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 text-primary">
            <Calculator className="h-5 w-5" />
          </span>
        </div>

        <form className="grid gap-4" onSubmit={simular}>
          <CampoTexto
            id="nomeProcedimento"
            label="Procedimento"
            erro={form.formState.errors.nomeProcedimento?.message}
            registro={form.register("nomeProcedimento")}
          />

          <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">
            <CampoNumero
              id="duracaoMinutos"
              label="Duracao"
              erro={form.formState.errors.duracaoMinutos?.message}
              registro={form.register("duracaoMinutos")}
            />
            <CampoNumero
              id="custoInsumos"
              label="Insumos"
              erro={form.formState.errors.custoInsumos?.message}
              registro={form.register("custoInsumos")}
            />
            <CampoNumero
              id="custoSalaPorHora"
              label="Sala por hora"
              erro={form.formState.errors.custoSalaPorHora?.message}
              registro={form.register("custoSalaPorHora")}
            />
            <CampoNumero
              id="valorHoraProfissional"
              label="Hora profissional"
              erro={form.formState.errors.valorHoraProfissional?.message}
              registro={form.register("valorHoraProfissional")}
            />
            <CampoNumero
              id="custoDeslocamento"
              label="Deslocamento"
              erro={form.formState.errors.custoDeslocamento?.message}
              registro={form.register("custoDeslocamento")}
            />
            <CampoNumero
              id="custoAlimentacao"
              label="Alimentacao"
              erro={form.formState.errors.custoAlimentacao?.message}
              registro={form.register("custoAlimentacao")}
            />
            <CampoNumero
              id="taxas"
              label="Taxas"
              erro={form.formState.errors.taxas?.message}
              registro={form.register("taxas")}
            />
            <CampoNumero
              id="margemDesejadaPercentual"
              label="Margem desejada"
              erro={form.formState.errors.margemDesejadaPercentual?.message}
              registro={form.register("margemDesejadaPercentual")}
            />
            <CampoNumero
              id="precoVenda"
              label="Preco praticado"
              erro={form.formState.errors.precoVenda?.message}
              registro={form.register("precoVenda")}
            />
          </div>

          {erro ? <div className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{erro}</div> : null}

          <Button type="submit" disabled={simulacaoMutation.isPending || !empresaId}>
            {simulacaoMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <Calculator className="h-4 w-4" />}
            Simular preco
          </Button>
        </form>
      </div>

      <aside className="rounded-lg border bg-card p-4 shadow-sm">
        <div className="mb-4 flex items-center justify-between gap-3 border-b pb-4">
          <div>
            <p className="text-sm font-medium text-primary">Resultado</p>
            <h2 className="mt-1 text-lg font-semibold text-card-foreground">
              {resultado?.recomendado.nomeProcedimento ?? "Aguardando simulacao"}
            </h2>
          </div>
          <span className="flex h-10 w-10 items-center justify-center rounded-md bg-secondary text-secondary-foreground">
            <BadgeDollarSign className="h-5 w-5" />
          </span>
        </div>

        {resultado ? (
          <div className="grid gap-3">
            <MetricaResultado icon={ReceiptText} label="Custo real" value={formatarMoeda(resultado.recomendado.custoTotal)} />
            <MetricaResultado icon={BadgeDollarSign} label="Preco minimo" value={formatarMoeda(resultado.recomendado.precoMinimo)} />
            <MetricaResultado icon={TrendingUp} label="Preco recomendado" value={formatarMoeda(resultado.recomendado.precoRecomendado)} />
            <MetricaResultado icon={Percent} label="Margem real" value={`${formatarNumero(resultado.margem.margemRealPercentual)}%`} />
            <MetricaResultado icon={CheckCircle2} label="Lucro estimado" value={formatarMoeda(resultado.margem.lucroEstimado)} />

            <div className="mt-1 border-t pt-3">
              {resultado.margem.alertas.length === 0 ? (
                <div className="flex items-center gap-2 rounded-md border border-green-200 bg-green-50 px-3 py-2 text-sm font-medium text-green-700">
                  <CheckCircle2 className="h-4 w-4" />
                  Precificacao saudavel
                </div>
              ) : (
                <div className="grid gap-2">
                  {resultado.margem.alertas.map((alerta) => (
                    <div key={alerta.codigo} className="flex items-start gap-2 rounded-md border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-800">
                      <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0" />
                      <span>{alerta.mensagem}</span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>
        ) : (
          <div className="flex min-h-72 items-center justify-center rounded-lg border bg-background px-4 text-center text-sm font-medium text-muted-foreground">
            Nenhuma simulacao calculada
          </div>
        )}
      </aside>
    </section>
  );
}

type Icone = typeof Calculator;

function MetricaResultado({ icon: Icon, label, value }: { icon: Icone; label: string; value: string }) {
  return (
    <div className="rounded-md border bg-background px-3 py-2">
      <div className="flex items-center justify-between gap-3">
        <span className="text-muted-foreground">{label}</span>
        <Icon className="h-4 w-4 text-primary" />
      </div>
      <p className="mt-1 text-lg font-semibold text-card-foreground">{value}</p>
    </div>
  );
}

type CampoProps = InputHTMLAttributes<HTMLInputElement> & {
  label: string;
  erro?: string;
  registro: UseFormRegisterReturn;
};

function CampoTexto({ label, erro, registro, ...props }: CampoProps) {
  return (
    <label className="grid gap-1 text-sm font-medium text-card-foreground">
      {label}
      <input
        className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
        {...registro}
        {...props}
      />
      {erro ? <span className="text-xs text-red-600">{erro}</span> : null}
    </label>
  );
}

function CampoNumero(props: CampoProps) {
  return <CampoTexto type="number" min={0} step="0.01" {...props} />;
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(valor);
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR", { maximumFractionDigits: 2 }).format(valor);
}
