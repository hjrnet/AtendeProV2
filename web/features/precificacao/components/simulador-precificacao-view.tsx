"use client";

import type { InputHTMLAttributes } from "react";
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm, type UseFormRegisterReturn } from "react-hook-form";
import {
  AlertTriangle,
  BadgeDollarSign,
  Calculator,
  CheckCircle2,
  ChevronLeft,
  ChevronRight,
  Edit3,
  FileText,
  History,
  LoaderCircle,
  Percent,
  Plus,
  ReceiptText,
  Save,
  Search,
  TrendingUp
} from "lucide-react";

import { Button } from "@/components/ui/button";
import { ApiError } from "@/lib/api";
import {
  atualizarSimulacaoPrecificacao,
  baixarRelatorioPrecificacao,
  calcularMargemLucro,
  calcularPrecoRecomendado,
  listarSimulacoesPrecificacao,
  salvarSimulacaoPrecificacao,
  type MargemLucroResponse,
  type PrecoRecomendadoResponse,
  type SalvarSimulacaoPrecificacaoRequest,
  type SimulacaoPrecificacao
} from "@/features/precificacao/api/precificacao-client";
import { precificacaoFormSchema, type PrecificacaoFormData } from "@/features/precificacao/lib/precificacao-form-schema";

type SimuladorPrecificacaoViewProps = {
  empresaId: string;
};

type ResultadoSimulacao = {
  nomeProcedimento: string;
  custoTotal: number;
  precoMinimo: number;
  precoRecomendado: number;
  margemRealPercentual: number;
  lucroEstimado: number;
  status: MargemLucroResponse["status"];
  alertas: MargemLucroResponse["alertas"];
};

type FiltroHistorico = "TODAS" | "SAUDAVEIS" | "ALERTA" | "PREJUIZO";

const TAMANHO_HISTORICO = 6;
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
  const queryClient = useQueryClient();
  const [erro, setErro] = useState<string | null>(null);
  const [resultado, setResultado] = useState<ResultadoSimulacao | null>(null);
  const [simulacaoEditando, setSimulacaoEditando] = useState<SimulacaoPrecificacao | null>(null);
  const [relatorioGerandoId, setRelatorioGerandoId] = useState<string | null>(null);
  const [buscaHistorico, setBuscaHistorico] = useState("");
  const [filtroHistorico, setFiltroHistorico] = useState<FiltroHistorico>("TODAS");
  const [paginaHistorico, setPaginaHistorico] = useState(0);
  const form = useForm<PrecificacaoFormData>({ defaultValues: VALORES_INICIAIS });

  const historicoQuery = useQuery({
    queryKey: ["precificacao-simulacoes", empresaId, paginaHistorico, buscaHistorico],
    queryFn: () =>
      listarSimulacoesPrecificacao({
        empresaId,
        pagina: paginaHistorico,
        tamanho: TAMANHO_HISTORICO,
        busca: buscaHistorico
      }),
    enabled: Boolean(empresaId)
  });

  const simulacaoMutation = useMutation({
    mutationFn: async (dados: PrecificacaoFormData): Promise<ResultadoSimulacao> => {
      const base = montarBaseRequest(dados, empresaId);
      const [recomendado, margem] = await Promise.all([
        calcularPrecoRecomendado({ ...base, margemDesejadaPercentual: dados.margemDesejadaPercentual }),
        calcularMargemLucro({ ...base, precoVenda: dados.precoVenda })
      ]);
      return resultadoDeCalculos(recomendado, margem);
    },
    onSuccess: (novoResultado) => setResultado(novoResultado),
    onError: (error) => setErro(error instanceof ApiError ? error.message : "Não foi possível simular o preço.")
  });

  const salvarMutation = useMutation({
    mutationFn: (dados: PrecificacaoFormData) => {
      const request = montarSimulacaoRequest(dados, empresaId, simulacaoEditando?.servicoProcedimentoId ?? null);
      return simulacaoEditando
        ? atualizarSimulacaoPrecificacao(simulacaoEditando.id, request)
        : salvarSimulacaoPrecificacao(request);
    },
    onSuccess: async (simulacao) => {
      setResultado(resultadoDeSimulacao(simulacao));
      setSimulacaoEditando(simulacao);
      await queryClient.invalidateQueries({ queryKey: ["precificacao-simulacoes", empresaId] });
      await queryClient.invalidateQueries({ queryKey: ["precificacao-dashboard", empresaId] });
    },
    onError: (error) => setErro(error instanceof ApiError ? error.message : "Não foi possível salvar a simulação.")
  });

  const historico = historicoQuery.data?.itens ?? [];
  const historicoFiltrado = historico.filter((simulacao) => filtrarSimulacaoHistorico(simulacao, filtroHistorico));
  const totalPaginas = historicoQuery.data?.totalPaginas ?? 0;
  const podeVoltar = paginaHistorico > 0;
  const podeAvancar = totalPaginas > 0 && paginaHistorico + 1 < totalPaginas;

  const simular = form.handleSubmit((valores) => {
    const dados = validarFormulario(valores);
    if (!dados) {
      return;
    }
    simulacaoMutation.mutate(dados);
  });

  const salvar = form.handleSubmit((valores) => {
    const dados = validarFormulario(valores);
    if (!dados) {
      return;
    }
    salvarMutation.mutate(dados);
  });

  function validarFormulario(valores: PrecificacaoFormData) {
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
      return null;
    }

    if (!empresaId) {
      setErro("Selecione uma empresa para simular precificação.");
      return null;
    }

    return validacao.data;
  }

  function editarSimulacao(simulacao: SimulacaoPrecificacao) {
    setErro(null);
    setSimulacaoEditando(simulacao);
    setResultado(resultadoDeSimulacao(simulacao));
    form.reset({
      nomeProcedimento: simulacao.nomeProcedimento,
      duracaoMinutos: simulacao.duracaoMinutos,
      custoInsumos: simulacao.custoInsumos,
      custoSalaPorHora: simulacao.custoSalaPorHora,
      valorHoraProfissional: simulacao.valorHoraProfissional,
      custoDeslocamento: simulacao.custoDeslocamento,
      custoAlimentacao: simulacao.custoAlimentacao,
      taxas: simulacao.taxas,
      margemDesejadaPercentual: simulacao.margemDesejadaPercentual,
      precoVenda: simulacao.precoVenda
    });
  }

  function novaSimulacao() {
    setErro(null);
    setResultado(null);
    setSimulacaoEditando(null);
    form.reset(VALORES_INICIAIS);
  }

  async function abrirRelatorio(simulacaoId: string) {
    setErro(null);
    setRelatorioGerandoId(simulacaoId);
    const janela = window.open("about:blank", "_blank");
    if (janela) {
      janela.opener = null;
    }

    try {
      const relatorio = await baixarRelatorioPrecificacao(simulacaoId);
      const url = URL.createObjectURL(relatorio);
      if (janela) {
        janela.location.href = url;
      } else {
        window.open(url, "_blank", "noopener,noreferrer");
      }
      window.setTimeout(() => URL.revokeObjectURL(url), 60000);
    } catch (error) {
      janela?.close();
      setErro(error instanceof ApiError ? error.message : "Não foi possível abrir o relatório.");
    } finally {
      setRelatorioGerandoId(null);
    }
  }

  return (
    <section className="grid gap-4">
      <div className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_390px]">
        <div className="rounded-lg border bg-card p-4 shadow-sm">
          <div className="mb-4 flex items-center justify-between gap-3 border-b pb-4">
            <div>
              <p className="text-sm font-medium text-primary">Precificação</p>
              <h2 className="mt-1 text-xl font-semibold text-card-foreground">Simulador de preço</h2>
            </div>
            <div className="flex gap-2">
              <Button type="button" variant="outline" size="icon" onClick={novaSimulacao} title="Nova simulação">
                <Plus className="h-4 w-4" />
              </Button>
              <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 text-primary">
                <Calculator className="h-5 w-5" />
              </span>
            </div>
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
                label="Duração"
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
                label="Alimentação"
                erro={form.formState.errors.custoAlimentacao?.message}
                registro={form.register("custoAlimentacao")}
              />
              <CampoNumero id="taxas" label="Taxas" erro={form.formState.errors.taxas?.message} registro={form.register("taxas")} />
              <CampoNumero
                id="margemDesejadaPercentual"
                label="Margem desejada"
                erro={form.formState.errors.margemDesejadaPercentual?.message}
                registro={form.register("margemDesejadaPercentual")}
              />
              <CampoNumero
                id="precoVenda"
                label="Preço praticado"
                erro={form.formState.errors.precoVenda?.message}
                registro={form.register("precoVenda")}
              />
            </div>

            {erro ? <div className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{erro}</div> : null}

            <div className="grid gap-2 sm:grid-cols-2">
              <Button type="submit" disabled={simulacaoMutation.isPending || salvarMutation.isPending || !empresaId}>
                {simulacaoMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <Calculator className="h-4 w-4" />}
                Simular preço
              </Button>
              <Button
                type="button"
                variant="secondary"
                disabled={simulacaoMutation.isPending || salvarMutation.isPending || !empresaId}
                onClick={() => void salvar()}
              >
                {salvarMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <Save className="h-4 w-4" />}
                {simulacaoEditando ? "Atualizar simulação" : "Salvar simulação"}
              </Button>
            </div>
          </form>
        </div>

        <aside className="rounded-lg border bg-card p-4 shadow-sm">
          <div className="mb-4 flex items-center justify-between gap-3 border-b pb-4">
            <div>
              <p className="text-sm font-medium text-primary">Resultado</p>
              <h2 className="mt-1 text-lg font-semibold text-card-foreground">
                {resultado?.nomeProcedimento ?? "Aguardando simulação"}
              </h2>
            </div>
            <span className="flex h-10 w-10 items-center justify-center rounded-md bg-secondary text-secondary-foreground">
              <BadgeDollarSign className="h-5 w-5" />
            </span>
          </div>

          {resultado ? (
            <div className="grid gap-3">
              <MetricaResultado icon={ReceiptText} label="Custo real" value={formatarMoeda(resultado.custoTotal)} />
              <MetricaResultado icon={BadgeDollarSign} label="Preço mínimo" value={formatarMoeda(resultado.precoMinimo)} />
              <MetricaResultado icon={TrendingUp} label="Preço recomendado" value={formatarMoeda(resultado.precoRecomendado)} />
              <MetricaResultado icon={Percent} label="Margem real" value={`${formatarNumero(resultado.margemRealPercentual)}%`} />
              <MetricaResultado icon={CheckCircle2} label="Lucro estimado" value={formatarMoeda(resultado.lucroEstimado)} />

              <div className="mt-1 border-t pt-3">
                {resultado.alertas.length === 0 ? (
                  <div className="flex items-center gap-2 rounded-md border border-green-200 bg-green-50 px-3 py-2 text-sm font-medium text-green-700">
                    <CheckCircle2 className="h-4 w-4" />
                    Precificação saudável
                  </div>
                ) : (
                  <div className="grid gap-2">
                    {resultado.alertas.map((alerta) => (
                      <div key={alerta.codigo} className="flex items-start gap-2 rounded-md border border-amber-200 bg-amber-50 px-3 py-2 text-sm text-amber-800">
                        <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0" />
                        <span>{alerta.mensagem}</span>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              {simulacaoEditando ? (
                <Button
                  type="button"
                  variant="outline"
                  disabled={relatorioGerandoId === simulacaoEditando.id}
                  onClick={() => void abrirRelatorio(simulacaoEditando.id)}
                >
                  {relatorioGerandoId === simulacaoEditando.id ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <FileText className="h-4 w-4" />}
                  Relatório PDF
                </Button>
              ) : null}
            </div>
          ) : (
            <div className="flex min-h-72 items-center justify-center rounded-lg border bg-background px-4 text-center text-sm font-medium text-muted-foreground">
              Nenhuma simulação calculada
            </div>
          )}
        </aside>
      </div>

      <div className="rounded-lg border bg-card p-4 shadow-sm">
        <div className="flex flex-col gap-3 border-b pb-4 md:flex-row md:items-center md:justify-between">
          <div className="flex items-center gap-3">
            <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 text-primary">
              <History className="h-5 w-5" />
            </span>
            <div>
              <p className="text-sm font-medium text-primary">Histórico</p>
              <h2 className="mt-1 text-lg font-semibold text-card-foreground">Simulações salvas</h2>
            </div>
          </div>
          <span className="rounded-md border bg-background px-3 py-2 text-sm font-semibold text-card-foreground">
            {historicoQuery.data?.totalItens ?? 0} registros
          </span>
        </div>

        <div className="mt-4 flex flex-col gap-3 sm:flex-row sm:items-center">
          <label className="relative min-w-0 flex-1">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <input
              value={buscaHistorico}
              onChange={(event) => {
                setBuscaHistorico(event.target.value);
                setPaginaHistorico(0);
              }}
              className="h-10 w-full rounded-md border bg-background pl-10 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Buscar simulação"
            />
          </label>
          <div className="flex gap-2">
            <Button type="button" variant="outline" size="icon" onClick={() => setPaginaHistorico((valor) => valor - 1)} disabled={!podeVoltar} title="Página anterior">
              <ChevronLeft className="h-4 w-4" />
            </Button>
            <Button type="button" variant="outline" size="icon" onClick={() => setPaginaHistorico((valor) => valor + 1)} disabled={!podeAvancar} title="Próxima página">
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>

        <div className="mt-3 flex gap-2 overflow-x-auto pb-1" aria-label="Filtros rápidos de status da precificação">
          {[
            { valor: "TODAS", label: "Todas" },
            { valor: "SAUDAVEIS", label: "Saudáveis" },
            { valor: "ALERTA", label: "Em alerta" },
            { valor: "PREJUIZO", label: "Em prejuízo" }
          ].map((filtro) => (
            <button
              key={filtro.valor}
              type="button"
              onClick={() => setFiltroHistorico(filtro.valor as FiltroHistorico)}
              className={`h-9 shrink-0 rounded-md border px-3 text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring ${
                filtroHistorico === filtro.valor ? "border-primary bg-primary text-primary-foreground" : "bg-background text-card-foreground"
              }`}
            >
              {filtro.label}
            </button>
          ))}
        </div>

        <div className="mt-4 max-h-[420px] overflow-y-auto pr-1">
          {historicoQuery.isLoading ? (
            <div className="flex min-h-40 items-center justify-center rounded-lg border bg-background text-sm text-muted-foreground">
              <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
              Carregando simulações
            </div>
          ) : historico.length === 0 ? (
            <div className="flex min-h-40 flex-col items-center justify-center rounded-lg border bg-background p-6 text-center">
              <Calculator className="h-8 w-8 text-primary" />
              <p className="mt-3 text-sm font-semibold text-card-foreground">Nenhuma simulação salva</p>
            </div>
          ) : historicoFiltrado.length === 0 ? (
            <div className="flex min-h-40 flex-col items-center justify-center rounded-lg border bg-background p-6 text-center">
              <Search className="h-8 w-8 text-primary" />
              <p className="mt-3 text-sm font-semibold text-card-foreground">Nenhuma simulação neste filtro</p>
            </div>
          ) : (
            <div className="grid gap-3 lg:grid-cols-2">
              {historicoFiltrado.map((simulacao) => {
                const status = statusVisual(simulacao.statusMargem);
                const Icon = status.icon;

                return (
                  <article
                    key={simulacao.id}
                    className={`rounded-lg border bg-background p-4 ${status.cardClass} ${
                      simulacaoEditando?.id === simulacao.id ? "ring-2 ring-primary/30" : ""
                    }`}
                  >
                    <div className="flex items-start justify-between gap-3">
                      <div className="min-w-0">
                        <div className="flex flex-wrap items-center gap-2">
                          <h3 className="truncate text-base font-semibold text-card-foreground">{simulacao.nomeProcedimento}</h3>
                          <span className={`inline-flex items-center gap-1 rounded-md border px-2 py-1 text-xs font-semibold ${status.badgeClass}`}>
                            <Icon className="h-3.5 w-3.5" />
                            {status.label}
                          </span>
                        </div>
                        <p className={`mt-2 text-sm font-medium ${status.textClass}`}>{status.descricao}</p>
                        <p className="mt-1 text-sm text-muted-foreground">
                          {simulacao.duracaoMinutos} min · margem {formatarNumero(simulacao.margemRealPercentual)}%
                        </p>
                      </div>
                      <div className="flex shrink-0 gap-2">
                        <Button type="button" variant="outline" size="icon" onClick={() => editarSimulacao(simulacao)} title="Editar simulação">
                          <Edit3 className="h-4 w-4" />
                        </Button>
                        <Button
                          type="button"
                          variant="outline"
                          size="icon"
                          onClick={() => void abrirRelatorio(simulacao.id)}
                          disabled={relatorioGerandoId === simulacao.id}
                          title="Abrir relatório PDF"
                        >
                          {relatorioGerandoId === simulacao.id ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <FileText className="h-4 w-4" />}
                        </Button>
                      </div>
                    </div>

                    <div className="mt-4 grid gap-2 sm:grid-cols-3">
                      <MetricaHistorico rotulo="Custo" valor={formatarMoeda(simulacao.custoTotal)} />
                      <MetricaHistorico rotulo="Recomendado" valor={formatarMoeda(simulacao.precoRecomendado)} />
                      <MetricaHistorico rotulo="Venda" valor={formatarMoeda(simulacao.precoVenda)} />
                    </div>
                  </article>
                );
              })}
            </div>
          )}
        </div>
      </div>
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

function MetricaHistorico({ rotulo, valor }: { rotulo: string; valor: string }) {
  return (
    <div className="rounded-md border bg-card px-3 py-2">
      <p className="text-xs font-medium uppercase text-muted-foreground">{rotulo}</p>
      <p className="mt-1 text-sm font-semibold text-card-foreground">{valor}</p>
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

function montarBaseRequest(dados: PrecificacaoFormData, empresaId: string) {
  return {
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
}

function montarSimulacaoRequest(
  dados: PrecificacaoFormData,
  empresaId: string,
  servicoProcedimentoId: string | null
): SalvarSimulacaoPrecificacaoRequest {
  return {
    ...montarBaseRequest(dados, empresaId),
    servicoProcedimentoId,
    margemDesejadaPercentual: dados.margemDesejadaPercentual,
    precoVenda: dados.precoVenda
  };
}

function resultadoDeCalculos(recomendado: PrecoRecomendadoResponse, margem: MargemLucroResponse): ResultadoSimulacao {
  return {
    nomeProcedimento: recomendado.nomeProcedimento,
    custoTotal: recomendado.custoTotal,
    precoMinimo: recomendado.precoMinimo,
    precoRecomendado: recomendado.precoRecomendado,
    margemRealPercentual: margem.margemRealPercentual,
    lucroEstimado: margem.lucroEstimado,
    status: margem.status,
    alertas: margem.alertas
  };
}

function resultadoDeSimulacao(simulacao: SimulacaoPrecificacao): ResultadoSimulacao {
  return {
    nomeProcedimento: simulacao.nomeProcedimento,
    custoTotal: simulacao.custoTotal,
    precoMinimo: simulacao.precoMinimo,
    precoRecomendado: simulacao.precoRecomendado,
    margemRealPercentual: simulacao.margemRealPercentual,
    lucroEstimado: simulacao.lucroEstimado,
    status: simulacao.statusMargem,
    alertas: simulacao.statusMargem === "SAUDAVEL" ? [] : [alertaPorStatus(simulacao.statusMargem)]
  };
}

function alertaPorStatus(status: MargemLucroResponse["status"]) {
  return {
    codigo: status,
    nivel: status === "PREJUIZO" ? "CRITICO" as const : "ATENCAO" as const,
    mensagem: mensagemStatus(status)
  };
}

function filtrarSimulacaoHistorico(simulacao: SimulacaoPrecificacao, filtro: FiltroHistorico) {
  if (filtro === "SAUDAVEIS") {
    return simulacao.statusMargem === "SAUDAVEL";
  }
  if (filtro === "PREJUIZO") {
    return simulacao.statusMargem === "PREJUIZO";
  }
  if (filtro === "ALERTA") {
    return simulacao.statusMargem !== "SAUDAVEL";
  }
  return true;
}

function statusVisual(status: MargemLucroResponse["status"]) {
  const config = {
    PREJUIZO: {
      label: "Em prejuízo",
      descricao: "Venda abaixo do custo real.",
      icon: AlertTriangle,
      cardClass: "border-red-300 bg-red-50/45",
      badgeClass: "border-red-200 bg-red-50 text-red-700",
      textClass: "text-red-700"
    },
    EQUILIBRIO: {
      label: "Sem lucro",
      descricao: "Preço cobre custos, mas não gera lucro.",
      icon: AlertTriangle,
      cardClass: "border-amber-300 bg-amber-50/45",
      badgeClass: "border-amber-200 bg-amber-50 text-amber-800",
      textClass: "text-amber-800"
    },
    MARGEM_BAIXA: {
      label: "Margem baixa",
      descricao: "Preço abaixo do recomendado.",
      icon: AlertTriangle,
      cardClass: "border-amber-300 bg-amber-50/45",
      badgeClass: "border-amber-200 bg-amber-50 text-amber-800",
      textClass: "text-amber-800"
    },
    SAUDAVEL: {
      label: "Saudável",
      descricao: "Preço dentro da margem esperada.",
      icon: CheckCircle2,
      cardClass: "border-emerald-200 bg-emerald-50/35",
      badgeClass: "border-emerald-200 bg-emerald-50 text-emerald-800",
      textClass: "text-emerald-800"
    }
  } satisfies Record<MargemLucroResponse["status"], {
    label: string;
    descricao: string;
    icon: Icone;
    cardClass: string;
    badgeClass: string;
    textClass: string;
  }>;

  return config[status];
}

function rotuloStatus(status: MargemLucroResponse["status"]) {
  const rotulos: Record<MargemLucroResponse["status"], string> = {
    PREJUIZO: "Prejuízo",
    EQUILIBRIO: "Equilíbrio",
    MARGEM_BAIXA: "Margem baixa",
    SAUDAVEL: "Saudável"
  };
  return rotulos[status];
}

function mensagemStatus(status: MargemLucroResponse["status"]) {
  const mensagens: Record<MargemLucroResponse["status"], string> = {
    PREJUIZO: "Preço de venda está abaixo do custo real.",
    EQUILIBRIO: "Preço cobre os custos, mas ainda não gera lucro.",
    MARGEM_BAIXA: "Margem positiva abaixo do recomendado.",
    SAUDAVEL: "Precificação saudável."
  };
  return mensagens[status];
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(valor);
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR", { maximumFractionDigits: 2 }).format(valor);
}
