"use client";

import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  CheckCircle2,
  CreditCard,
  Download,
  LoaderCircle,
  PlayCircle,
  RefreshCw,
  ShieldCheck,
  TerminalSquare,
  Target
} from "lucide-react";

import {
  consultarObservabilidadePagamentosSandbox,
  listarPagamentosSandbox,
  listarPlanos,
  reconciliarDivergenciasPagamentosSandbox,
  prepararCheckoutPagamentoSandbox,
  exportarObservabilidadePagamentosSandboxCsv,
  registrarWebhookAsaasSandbox,
  type ObservabilidadePagamentosSandboxDivergencia,
  type ReconciliacaoDivergenciasPagamentosSandboxResult,
  type PagamentoSandboxResumo,
  type Plano,
  type WebhookAsaasSandboxRequest
} from "@/features/admin-planos/api/planos-client";

type AdminSaasPagamentosR31ViewProps = {
  empresaId: string;
};

const eventosSandbox: Array<WebhookAsaasSandboxRequest["event"]> = [
  "PAYMENT_RECEIVED",
  "PAYMENT_OVERDUE",
  "PAYMENT_DELETED"
];

const opcoesSeveridade = ["", "BAIXA", "MEDIA", "ALTA"];
const opcoesEventoTipo = ["", "CHECKOUT_PREPARADO", "PAYMENT_RECEIVED", "PAYMENT_OVERDUE", "PAYMENT_DELETED", "PAYMENT_REFUNDED"];
const opcoesStatusAssinatura = ["", "AGUARDANDO_PAGAMENTO", "ATIVA", "CANCELADA"];
const opcoesTipoDivergencia = [
  "",
  "ASSINATURA_SEM_COBRANCA",
  "ASSINATURA_ATIVA_SEM_CONFIRMACAO_PAGAMENTO",
  "COBRANCA_RECEBIDA_SEM_WEBHOOK",
  "ASSINATURA_CANCELADA_COM_EVENTO_ATIVO"
];

export function AdminSaasPagamentosR31View({ empresaId }: AdminSaasPagamentosR31ViewProps) {
  const queryClient = useQueryClient();

  const [planoId, setPlanoId] = useState("");
  const [responsavel, setResponsavel] = useState({
    nome: "Admin AtendePro",
    email: "admin@atendepro.local",
    documento: "12345678000190",
    telefone: "11999990000"
  });
  const [webhookToken, setWebhookToken] = useState("");
  const [filtroStatusAssinatura, setFiltroStatusAssinatura] = useState("");
  const [filtroEventoTipo, setFiltroEventoTipo] = useState("");
  const [filtroTipoDivergencia, setFiltroTipoDivergencia] = useState("");
  const [filtroSeveridade, setFiltroSeveridade] = useState("");

  const planosQuery = useQuery({
    queryKey: ["admin-planos", "r31-pagamentos"],
    queryFn: () => listarPlanos({ pagina: 0, tamanho: 30 })
  });

  const pagamentosQuery = useQuery({
    queryKey: ["admin-pagamentos-sandbox", empresaId],
    queryFn: () => listarPagamentosSandbox({ pagina: 0, tamanho: 12, empresaId }),
    enabled: Boolean(empresaId)
  });

  const observabilidadeQuery = useQuery({
    queryKey: [
      "admin-pagamentos-observabilidade-sandbox",
      empresaId,
      filtroStatusAssinatura,
      filtroEventoTipo,
      filtroTipoDivergencia,
      filtroSeveridade
    ],
    queryFn: () => consultarObservabilidadePagamentosSandbox({
      empresaId,
      statusAssinatura: filtroStatusAssinatura || undefined,
      eventoTipo: filtroEventoTipo || undefined,
      tipoDivergencia: filtroTipoDivergencia || undefined,
      severidade: filtroSeveridade || undefined
    }),
    enabled: Boolean(empresaId)
  });

  const observabilidade = observabilidadeQuery.data;
  const indicadores = observabilidade?.indicadores;
  const divergencias = observabilidade?.divergencias ?? [];

  const planos = useMemo(() => (planosQuery.data?.itens ?? []).filter((plano) => plano.ativo), [planosQuery.data?.itens]);
  const planoSelecionado = planos.find((plano) => plano.id === planoId) ?? planos[0] ?? null;
  const pagamentos = pagamentosQuery.data?.itens ?? [];
  const pagamentoEmFoco = pagamentos[0] ?? null;

  const checkoutMutation = useMutation({
    mutationFn: () => {
      if (!planoSelecionado) {
        throw new Error("Selecione um plano ativo para preparar o checkout sandbox.");
      }
      return prepararCheckoutPagamentoSandbox({
        empresaId,
        planoId: planoSelecionado.id,
        emailResponsavel: responsavel.email,
        nomeResponsavel: responsavel.nome,
        documentoResponsavel: responsavel.documento,
        telefoneResponsavel: responsavel.telefone,
        formaPagamentoPreferida: "PIX"
      });
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin-pagamentos-sandbox", empresaId] });
      await queryClient.invalidateQueries({ queryKey: ["admin-pagamentos-observabilidade-sandbox", empresaId] });
      await queryClient.invalidateQueries({ queryKey: ["admin-saas-auditoria-r28"] });
    }
  });

  const webhookMutation = useMutation({
    mutationFn: (event: WebhookAsaasSandboxRequest["event"]) => {
      if (!pagamentoEmFoco?.cobrancaExternaId || !pagamentoEmFoco.assinaturaExternaId) {
        throw new Error("Prepare um checkout sandbox antes de simular webhook.");
      }

      return registrarWebhookAsaasSandbox({
        event,
        paymentId: pagamentoEmFoco.cobrancaExternaId,
        subscriptionId: pagamentoEmFoco.assinaturaExternaId,
        token: webhookToken || undefined,
        payload: JSON.stringify({ event, sandbox: true, source: "admin-saas-r31-r32" })
      });
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin-pagamentos-sandbox", empresaId] });
      await queryClient.invalidateQueries({ queryKey: ["admin-pagamentos-observabilidade-sandbox", empresaId] });
      await queryClient.invalidateQueries({ queryKey: ["admin-saas-auditoria-r28"] });
    }
  });

  const reconciliarMutation = useMutation({
    mutationFn: (divergencia: ObservabilidadePagamentosSandboxDivergencia) => {
      const evento = eventoReconciliacao(divergencia);
      if (!evento) {
        throw new Error("Nao existe acao automatica de reconciliacao para esta divergencia ainda.");
      }
      if (!divergencia.assinaturaExternaId || !divergencia.cobrancaExternaId) {
        throw new Error("Divergencia sem identificadores externos para webhook de reconstrucao.");
      }

      return registrarWebhookAsaasSandbox({
        event: evento,
        paymentId: divergencia.cobrancaExternaId,
        subscriptionId: divergencia.assinaturaExternaId,
        token: webhookToken || undefined,
        payload: JSON.stringify({
          event: evento,
          source: "admin-saas-r32",
          reconciliar: true,
          tipoDivergencia: divergencia.tipoDivergencia
        })
      });
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin-pagamentos-sandbox", empresaId] });
      await queryClient.invalidateQueries({ queryKey: ["admin-pagamentos-observabilidade-sandbox", empresaId] });
    }
  });

  const reconciliarLoteMutation = useMutation({
    mutationFn: () => reconciliarDivergenciasPagamentosSandbox({
      empresaId,
      statusAssinatura: filtroStatusAssinatura || undefined,
      eventoTipo: filtroEventoTipo || undefined,
      tipoDivergencia: filtroTipoDivergencia || undefined,
      severidade: filtroSeveridade || undefined,
      token: webhookToken || undefined
    }),
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin-pagamentos-sandbox", empresaId] });
      await queryClient.invalidateQueries({ queryKey: ["admin-pagamentos-observabilidade-sandbox", empresaId] });
      await queryClient.invalidateQueries({ queryKey: ["admin-saas-auditoria-r28"] });
    }
  });

  const exportarObservabilidadeCsvMutation = useMutation({
    mutationFn: async () => {
      const blob = await exportarObservabilidadePagamentosSandboxCsv({
        empresaId,
        statusAssinatura: filtroStatusAssinatura || undefined,
        eventoTipo: filtroEventoTipo || undefined,
        tipoDivergencia: filtroTipoDivergencia || undefined,
        severidade: filtroSeveridade || undefined
      });
      const dataArquivo = new Date().toISOString().slice(0, 10);
      const nomeArquivo = `observabilidade-pagamentos-${dataArquivo}.csv`;
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement("a");
      link.href = url;
      link.download = nomeArquivo;
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    }
  });

  if (!empresaId) {
    return (
      <section className="rounded-xl border bg-card p-5 text-sm text-muted-foreground shadow-sm">
        Selecione uma empresa para operar pagamentos sandbox no Admin SaaS.
      </section>
    );
  }

  return (
    <section className="grid gap-4">
      <section className="rounded-xl border bg-card p-4 shadow-sm">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div>
            <div className="inline-flex items-center gap-2 rounded-full border bg-background px-3 py-1 text-xs font-semibold text-primary">
              <ShieldCheck className="h-4 w-4" /> R31-R32 sandbox
            </div>
            <h2 className="mt-3 text-2xl font-semibold tracking-tight text-card-foreground">
              Pagamentos sandbox com observabilidade e reconciliação
            </h2>
            <p className="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
              Prepare checkout, simule webhook e acompanhe divergencias de reconciliação por indicador e severidade, sem impactar produção.
            </p>
          </div>
          <div className="flex flex-wrap gap-2">
            <button
              type="button"
              onClick={() => {
                pagamentosQuery.refetch();
                observabilidadeQuery.refetch();
              }}
              className="inline-flex h-9 items-center gap-2 rounded-md border bg-background px-3 text-xs font-semibold text-card-foreground transition-colors hover:border-primary/50"
            >
              <RefreshCw className={`h-4 w-4 ${pagamentosQuery.isFetching ? "animate-spin" : ""}`} /> Atualizar tudo
            </button>
            <button
              type="button"
              onClick={() => {
                setFiltroStatusAssinatura("");
                setFiltroEventoTipo("");
                setFiltroTipoDivergencia("");
                setFiltroSeveridade("");
              }}
              className="inline-flex h-9 items-center gap-2 rounded-md border border-white/20 bg-background px-3 text-xs font-semibold text-card-foreground transition-colors hover:border-primary/50"
            >
              Limpar filtros
            </button>
          </div>
        </div>
      </section>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)]">
        <section className="rounded-xl border bg-card p-4 shadow-sm">
          <CabecalhoR31 icon={CreditCard} titulo="Preparar checkout sandbox" descricao="Usa o endpoint R30 e mantém produção bloqueada." />
          <div className="mt-4 grid gap-3">
            <label className="grid gap-1 text-sm">
              <span className="font-medium text-card-foreground">Plano</span>
              <select
                value={planoSelecionado?.id ?? ""}
                onChange={(event) => setPlanoId(event.target.value)}
                className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus-visible:ring-2 focus-visible:ring-ring"
              >
                {planos.map((plano) => (
                  <option key={plano.id} value={plano.id}>
                    {plano.nome} - {formatarMoeda(plano.valorMensal)}
                  </option>
                ))}
              </select>
            </label>
            <div className="grid gap-3 md:grid-cols-2">
              <CampoResponsavel label="Nome" value={responsavel.nome} onChange={(valor) => setResponsavel((atual) => ({ ...atual, nome: valor }))} />
              <CampoResponsavel label="Email" value={responsavel.email} onChange={(valor) => setResponsavel((atual) => ({ ...atual, email: valor }))} />
              <CampoResponsavel label="Documento" value={responsavel.documento} onChange={(valor) => setResponsavel((atual) => ({ ...atual, documento: valor }))} />
              <CampoResponsavel label="Telefone" value={responsavel.telefone} onChange={(valor) => setResponsavel((atual) => ({ ...atual, telefone: valor }))} />
            </div>
            <button
              type="button"
              disabled={checkoutMutation.isPending || !planoSelecionado}
              onClick={() => checkoutMutation.mutate()}
              className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-primary px-4 text-sm font-semibold text-primary-foreground transition-colors hover:bg-primary/90 disabled:opacity-60"
            >
              {checkoutMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <PlayCircle className="h-4 w-4" />}
              Preparar checkout sandbox
            </button>
            <FeedbackOperacao
              error={checkoutMutation.error}
              success={checkoutMutation.data ? `Checkout ${checkoutMutation.data.status} criado em ${checkoutMutation.data.ambiente}.` : null}
            />
          </div>
        </section>

        <section className="rounded-xl border bg-card p-4 shadow-sm">
          <CabecalhoR31 icon={TerminalSquare} titulo="Simular webhook Asaas" descricao="Reprocessa o pagamento em sandbox para validar reconciliation." />
          <div className="mt-4 grid gap-3">
            <ResumoPagamento pagamento={pagamentoEmFoco} />
            <label className="grid gap-1 text-sm">
              <span className="font-medium text-card-foreground">Token de webhook local</span>
              <input
                value={webhookToken}
                onChange={(event) => setWebhookToken(event.target.value)}
                placeholder="Opcional se PAGAMENTOS_ASAAS_WEBHOOK_TOKEN estiver vazio"
                className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus-visible:ring-2 focus-visible:ring-ring"
              />
            </label>
            <div className="flex flex-wrap gap-2">
              {eventosSandbox.map((event) => (
                <button
                  key={event}
                  type="button"
                  disabled={webhookMutation.isPending || !pagamentoEmFoco}
                  onClick={() => webhookMutation.mutate(event)}
                  className="inline-flex h-9 items-center gap-2 rounded-md border bg-background px-3 text-xs font-semibold text-card-foreground transition-colors hover:border-primary/50 disabled:opacity-60"
                >
                  {event}
                </button>
              ))}
            </div>
            <FeedbackOperacao error={webhookMutation.error} success={webhookMutation.data ? webhookMutation.data.mensagem : null} />
          </div>
        </section>
      </div>

      <section className="rounded-xl border bg-card p-4 shadow-sm">
        <CabecalhoR31 icon={Target} titulo="Observabilidade e reconciliacao em sandbox" descricao="Use filtros por status, severidade e tipo para conduzir ações de reconciliação rápida." />
        <div className="mt-4 grid gap-3">
          <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-4">
            <IndicadorPlano titulo="Total checkouts" valor={indicadores?.totalCheckoutsPreparados ?? 0} />
            <IndicadorPlano titulo="Cobrancas recebidas" valor={indicadores?.totalCobrancasRecebidas ?? 0} />
            <IndicadorPlano titulo="Webhooks processados" valor={indicadores?.totalWebhooksProcessados ?? 0} />
            <IndicadorPlano titulo="Divergencias" valor={indicadores?.totalDivergencias ?? 0} />
          </div>

          <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-4">
            <IndicadorPlano titulo="Cobrancas pendentes" valor={indicadores?.totalCobrancasPendentes ?? 0} />
            <IndicadorPlano titulo="Cobrancas vencidas" valor={indicadores?.totalCobrancasVencidas ?? 0} />
            <IndicadorPlano titulo="Nao processados" valor={indicadores?.totalWebhooksNaoProcessados ?? 0} />
            <IndicadorPlano titulo="Webhooks duplicados" valor={indicadores?.totalWebhooksDuplicados ?? 0} />
          </div>

          <div className="flex justify-end">
            <button
              type="button"
              disabled={exportarObservabilidadeCsvMutation.isPending}
              onClick={() => exportarObservabilidadeCsvMutation.mutate()}
              className="inline-flex h-9 items-center gap-2 rounded-md border bg-background px-3 text-xs font-semibold text-card-foreground transition-colors hover:border-primary/50 disabled:opacity-60"
            >
              {exportarObservabilidadeCsvMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <Download className="h-4 w-4" />}
              Exportar observabilidade CSV
            </button>
            <button
              type="button"
              disabled={reconciliarLoteMutation.isPending}
              onClick={() => reconciliarLoteMutation.mutate()}
              className="inline-flex h-9 items-center gap-2 rounded-md border bg-background px-3 text-xs font-semibold text-card-foreground transition-colors hover:border-primary/50 disabled:opacity-60"
            >
              {reconciliarLoteMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <PlayCircle className="h-4 w-4" />}
              Reconciliar divergencias em lote
            </button>
          </div>
          <FeedbackOperacao
            error={reconciliarLoteMutation.error}
            success={reconciliarLoteMutation.data ? mensagemResumoReconciliacaoLote(reconciliarLoteMutation.data) : null}
          />
          <FeedbackOperacao
            error={exportarObservabilidadeCsvMutation.error}
            success={exportarObservabilidadeCsvMutation.isSuccess ? "Relatorio CSV da observabilidade exportado com sucesso." : null}
          />

          <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-4">
            <FiltroCampo label="Status assinatura" value={filtroStatusAssinatura} onChange={setFiltroStatusAssinatura} options={opcoesStatusAssinatura} />
            <FiltroCampo label="Evento do webhook" value={filtroEventoTipo} onChange={setFiltroEventoTipo} options={opcoesEventoTipo} />
            <FiltroCampo label="Tipo divergencia" value={filtroTipoDivergencia} onChange={setFiltroTipoDivergencia} options={opcoesTipoDivergencia} />
            <FiltroCampo label="Severidade" value={filtroSeveridade} onChange={setFiltroSeveridade} options={opcoesSeveridade} />
          </div>

          <div className="grid gap-3">
            {observabilidadeQuery.isLoading ? <EstadoInline texto="Carregando analise observacional de pagamentos sandbox." /> : null}
            {observabilidadeQuery.error instanceof Error ? (
              <EstadoInline texto={observabilidadeQuery.error.message} />
            ) : null}
            {(!observabilidadeQuery.isLoading &&
              !(observabilidadeQuery.data?.divergencias ?? []).length) ? (
              <EstadoInline texto="Nenhuma divergencia no periodo atual." />
            ) : null}
            {divergencias.map((divergencia) => (
              <LinhaDivergencia
                key={`${divergencia.pagamentoAssinaturaId}-${divergencia.tipoDivergencia}-${divergencia.descricao ?? "sem-descricao"}`}
                divergencia={divergencia}
                mutation={reconciliarMutation}
              />
            ))}
          </div>
        </div>
      </section>

      <section className="rounded-xl border bg-card p-4 shadow-sm">
        <CabecalhoR31 icon={CheckCircle2} titulo="Pagamentos recentes" descricao="Status operacional persistido pelo módulo de pagamentos sandbox." />
        <div className="mt-4 grid gap-3">
          {pagamentosQuery.isLoading ? <EstadoInline texto="Carregando pagamentos sandbox" /> : null}
          {!pagamentosQuery.isLoading && pagamentos.length === 0 ? <EstadoInline texto="Nenhum checkout sandbox preparado ainda." /> : null}
          {pagamentos.map((pagamento) => <LinhaPagamento key={pagamento.pagamentoAssinaturaId} pagamento={pagamento} />)}
        </div>
      </section>
    </section>
  );
}

function CabecalhoR31({ icon: Icon, titulo, descricao }: { icon: typeof CreditCard; titulo: string; descricao: string }) {
  return (
    <div className="flex items-start gap-3">
      <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary">
        <Icon className="h-5 w-5" />
      </span>
      <div>
        <h3 className="text-base font-semibold text-card-foreground">{titulo}</h3>
        <p className="mt-1 text-sm leading-6 text-muted-foreground">{descricao}</p>
      </div>
    </div>
  );
}

function mensagemResumoReconciliacaoLote(resultado: ReconciliacaoDivergenciasPagamentosSandboxResult) {
  return `Reconciliação em lote concluída: ${resultado.totalProcessadas} processadas, ${resultado.totalDuplicadas} duplicadas, ${resultado.totalIgnoradas} ignoradas, ${resultado.totalFalhas} falhas de ${resultado.totalEncontradas} divergencias.`;
}

function IndicadorPlano({ titulo, valor }: { titulo: string; valor: number }) {
  return (
    <article className="rounded-lg border bg-background p-3">
      <p className="text-xs text-muted-foreground">{titulo}</p>
      <p className="mt-1 text-2xl font-semibold text-card-foreground">{formatarNumero(valor)}</p>
    </article>
  );
}

function CampoResponsavel({ label, value, onChange }: { label: string; value: string; onChange: (value: string) => void }) {
  return (
    <label className="grid gap-1 text-sm">
      <span className="font-medium text-card-foreground">{label}</span>
      <input
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus-visible:ring-2 focus-visible:ring-ring"
      />
    </label>
  );
}

function FiltroCampo({
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
    <label className="grid gap-1 text-sm">
      <span className="font-medium text-card-foreground">{label}</span>
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus-visible:ring-2 focus-visible:ring-ring"
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

function ResumoPagamento({ pagamento }: { pagamento?: PagamentoSandboxResumo | null }) {
  if (!pagamento) {
    return <EstadoInline texto="Prepare um checkout sandbox para habilitar a simulacao de webhook." />;
  }

  return (
    <div className="grid gap-2 rounded-lg border bg-background p-3 text-sm">
      <LinhaDetalhe label="Status assinatura" value={pagamento.statusAssinatura} />
      <LinhaDetalhe label="Status cobranca" value={pagamento.statusCobranca ?? "sem cobranca"} />
      <LinhaDetalhe label="Assinatura externa" value={pagamento.assinaturaExternaId ?? "-"} />
      <LinhaDetalhe label="Cobranca externa" value={pagamento.cobrancaExternaId ?? "-"} />
    </div>
  );
}

function LinhaPagamento({ pagamento }: { pagamento: PagamentoSandboxResumo }) {
  return (
    <article className="grid gap-3 rounded-lg border bg-background p-3 lg:grid-cols-[minmax(0,1fr)_auto] lg:items-center">
      <div>
        <div className="flex flex-wrap items-center gap-2">
          <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-emerald-800">{pagamento.ambiente}</span>
          <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-card-foreground">{pagamento.statusAssinatura}</span>
          <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{pagamento.statusCobranca ?? "SEM_COBRANCA"}</span>
        </div>
        <p className="mt-2 text-sm font-semibold text-card-foreground">{pagamento.checkoutExternoId ?? pagamento.pagamentoAssinaturaId}</p>
        <p className="mt-1 text-xs text-muted-foreground">
          Provedor {pagamento.provedor} - evento {pagamento.ultimoEventoTipo ?? "sem evento"} - {formatarDataHora(pagamento.atualizadoEm)}
        </p>
      </div>
      <div className="text-sm font-semibold text-card-foreground">{formatarMoeda(pagamento.valor ?? 0)}</div>
    </article>
  );
}

function LinhaDivergencia({
  divergencia,
  mutation
}: {
  divergencia: ObservabilidadePagamentosSandboxDivergencia;
  mutation: {
    mutate: (divergencia: ObservabilidadePagamentosSandboxDivergencia) => void;
    isPending: boolean;
    error: unknown;
  };
}) {
  const podeReconectar = Boolean(divergencia.assinaturaExternaId && divergencia.cobrancaExternaId && eventoReconciliacao(divergencia));
  const evento = eventoReconciliacao(divergencia);
  const corSeveridade = classeSeveridade(divergencia.severidade);

  return (
    <article className={`grid gap-3 rounded-lg border p-3 ${corSeveridade}`}>
      <div className="flex flex-wrap items-center justify-between gap-2">
        <div className="flex flex-wrap items-center gap-2">
          <span className="rounded-md border px-2 py-1 text-xs font-semibold text-card-foreground">{divergencia.tipoDivergencia}</span>
          <span className="rounded-md border px-2 py-1 text-xs font-semibold text-muted-foreground">{divergencia.severidade}</span>
          {evento ? <span className="rounded-md border px-2 py-1 text-xs font-semibold text-muted-foreground">Reconciliar: {evento}</span> : null}
        </div>
        <button
          type="button"
          disabled={mutation.isPending || !podeReconectar}
          onClick={() => mutation.mutate(divergencia)}
          className="inline-flex h-8 items-center gap-1 rounded-md border bg-white px-2.5 text-xs font-semibold text-card-foreground transition-colors hover:border-primary/60 disabled:opacity-60"
        >
          {mutation.isPending ? <LoaderCircle className="h-3.5 w-3.5 animate-spin" /> : <PlayCircle className="h-3.5 w-3.5" />} Reconhecer divergencia
        </button>
      </div>
      <p className="text-sm text-muted-foreground">{divergencia.descricao}</p>
      <div className="grid gap-2 md:grid-cols-2">
        <LinhaDetalhe label="Assinatura" value={divergencia.assinaturaExternaId ?? "-"} />
        <LinhaDetalhe label="Cobranca" value={divergencia.cobrancaExternaId ?? "-"} />
      </div>
      <div className="grid gap-2 md:grid-cols-2">
        <LinhaDetalhe label="Status assinatura" value={divergencia.statusAssinatura} />
        <LinhaDetalhe label="Status cobranca" value={divergencia.statusCobranca ?? "SEM_COBRANCA"} />
        <LinhaDetalhe label="Último evento" value={divergencia.eventoTipo ?? "SEM_EVENTO"} />
        <LinhaDetalhe label="Processado" value={divergencia.eventoProcessado === null ? "N/A" : divergencia.eventoProcessado ? "SIM" : "NÃO"} />
      </div>
      {mutation.error instanceof Error ? <p className="text-xs text-red-700">{mutation.error.message}</p> : null}
    </article>
  );
}

function FeedbackOperacao({ error, success }: { error: unknown; success: string | null }) {
  if (error instanceof Error) {
    return <p className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm font-medium text-red-900">{error.message}</p>;
  }
  if (success) {
    return <p className="rounded-md border border-emerald-200 bg-emerald-50 px-3 py-2 text-sm font-medium text-emerald-900">{success}</p>;
  }
  return null;
}

function EstadoInline({ texto }: { texto: string }) {
  return <div className="rounded-lg border bg-background p-3 text-sm text-muted-foreground">{texto}</div>;
}

function LinhaDetalhe({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex flex-wrap items-center justify-between gap-2">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium text-card-foreground">{value}</span>
    </div>
  );
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR", { maximumFractionDigits: 0 }).format(valor);
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(valor);
}

function formatarDataHora(valor?: string | null) {
  if (!valor) {
    return "-";
  }
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "short", timeStyle: "short" }).format(new Date(valor));
}

function classeSeveridade(severidade: string) {
  if (severidade === "ALTA") {
    return "border-red-300 bg-red-50/80";
  }
  if (severidade === "MEDIA") {
    return "border-amber-300 bg-amber-50/80";
  }
  return "border-blue-200 bg-blue-50/80";
}

function eventoReconciliacao(divergencia: ObservabilidadePagamentosSandboxDivergencia) {
  if (divergencia.tipoDivergencia === "COBRANCA_RECEBIDA_SEM_WEBHOOK") {
    return "PAYMENT_RECEIVED" as const;
  }
  if (divergencia.tipoDivergencia === "ASSINATURA_ATIVA_SEM_CONFIRMACAO_PAGAMENTO") {
    return "PAYMENT_RECEIVED" as const;
  }
  if (divergencia.tipoDivergencia === "ASSINATURA_CANCELADA_COM_EVENTO_ATIVO") {
    return "PAYMENT_DELETED" as const;
  }
  if (divergencia.tipoDivergencia === "ASSINATURA_SEM_COBRANCA") {
    return null;
  }
  return "PAYMENT_OVERDUE" as const;
}
