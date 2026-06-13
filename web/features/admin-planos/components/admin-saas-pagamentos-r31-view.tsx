"use client";

import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { CheckCircle2, CreditCard, LoaderCircle, PlayCircle, RefreshCw, ShieldCheck, TerminalSquare } from "lucide-react";

import {
  listarPagamentosSandbox,
  listarPlanos,
  prepararCheckoutPagamentoSandbox,
  registrarWebhookAsaasSandbox,
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

  const planosQuery = useQuery({
    queryKey: ["admin-planos", "r31-pagamentos"],
    queryFn: () => listarPlanos({ pagina: 0, tamanho: 30 })
  });
  const pagamentosQuery = useQuery({
    queryKey: ["admin-pagamentos-sandbox", empresaId],
    queryFn: () => listarPagamentosSandbox({ pagina: 0, tamanho: 12, empresaId }),
    enabled: Boolean(empresaId)
  });
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
        payload: JSON.stringify({ event, sandbox: true, source: "admin-saas-r31" })
      });
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({ queryKey: ["admin-pagamentos-sandbox", empresaId] });
      await queryClient.invalidateQueries({ queryKey: ["admin-saas-auditoria-r28"] });
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
              <ShieldCheck className="h-4 w-4" /> R31 sandbox seguro
            </div>
            <h2 className="mt-3 text-2xl font-semibold tracking-tight text-card-foreground">Pagamentos sandbox no cockpit Admin SaaS</h2>
            <p className="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
              Prepare checkout, registre webhooks simulados e acompanhe status operacional sem acionar cobranca real.
            </p>
          </div>
          <button
            type="button"
            onClick={() => pagamentosQuery.refetch()}
            className="inline-flex h-9 items-center gap-2 rounded-md border bg-background px-3 text-xs font-semibold text-card-foreground transition-colors hover:border-primary/50"
          >
            <RefreshCw className={`h-4 w-4 ${pagamentosQuery.isFetching ? "animate-spin" : ""}`} /> Atualizar
          </button>
        </div>
      </section>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)]">
        <section className="rounded-xl border bg-card p-4 shadow-sm">
          <CabecalhoR31 icon={CreditCard} titulo="Preparar checkout sandbox" descricao="Usa o endpoint R30 e mantem producao bloqueada." />
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
              <CampoResponsavel label="Nome" value={responsavel.nome} onChange={(nome) => setResponsavel((atual) => ({ ...atual, nome }))} />
              <CampoResponsavel label="Email" value={responsavel.email} onChange={(email) => setResponsavel((atual) => ({ ...atual, email }))} />
              <CampoResponsavel label="Documento" value={responsavel.documento} onChange={(documento) => setResponsavel((atual) => ({ ...atual, documento }))} />
              <CampoResponsavel label="Telefone" value={responsavel.telefone} onChange={(telefone) => setResponsavel((atual) => ({ ...atual, telefone }))} />
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
            <FeedbackOperacao error={checkoutMutation.error} success={checkoutMutation.data ? `Checkout ${checkoutMutation.data.status} criado em ${checkoutMutation.data.ambiente}.` : null} />
          </div>
        </section>

        <section className="rounded-xl border bg-card p-4 shadow-sm">
          <CabecalhoR31 icon={TerminalSquare} titulo="Simular webhook Asaas" descricao="Reprocessa o pagamento em sandbox usando a ultima cobranca preparada." />
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
        <CabecalhoR31 icon={CheckCircle2} titulo="Pagamentos recentes" descricao="Status operacional persistido pelo modulo de pagamentos sandbox." />
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

function LinhaDetalhe({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex flex-wrap items-center justify-between gap-2">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium text-card-foreground">{value}</span>
    </div>
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

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" }).format(valor);
}

function formatarDataHora(valor?: string | null) {
  if (!valor) {
    return "-";
  }
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "short", timeStyle: "short" }).format(new Date(valor));
}
