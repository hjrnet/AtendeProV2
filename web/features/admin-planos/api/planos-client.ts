import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type Plano = {
  id: string;
  codigo: string;
  nome: string;
  descricao: string | null;
  valorMensal: number;
  limiteUsuarios: number;
  limiteClientes: number;
  limiteProfissionais: number;
  ativo: boolean;
  estudante: boolean;
  marcaDaguaAcademica: string | null;
  modulos: string[];
  criadoEm: string;
  atualizadoEm: string;
};

export type PlanosPaginados = {
  itens: Plano[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type SalvarPlanoRequest = {
  codigo: string;
  nome: string;
  descricao?: string | null;
  valorMensal: number;
  limiteUsuarios: number;
  limiteClientes: number;
  limiteProfissionais: number;
  ativo: boolean;
  estudante: boolean;
  marcaDaguaAcademica?: string | null;
  modulos: string[];
};

export type AdminSaasDashboard = {
  mrr: number;
  empresasAtivas: number;
  empresasBloqueadas: number;
  trialsAtivos: number;
  chamadosAbertos: number;
  atualizadoEm: string;
};

export type PlanoVendidoAdminSaas = {
  planoId: string;
  codigo: string;
  nome: string;
  totalAssinaturas: number;
  assinaturasAtivas: number;
  mrr: number;
};

export type DashboardVendasAdminSaas = {
  mrr: number;
  trialsIniciados: number;
  trialsConvertidos: number;
  taxaConversaoTrial: number;
  assinaturasAtivas: number;
  assinaturasCanceladas: number;
  taxaChurn: number;
  planosVendidos: PlanoVendidoAdminSaas[];
  atualizadoEm: string;
};


export type ChecklistAuditoriaAdminSaas = {
  codigo: string;
  titulo: string;
  status: "OK" | "ACAO_REQUERIDA";
  detalhe: string;
  severidade: "BAIXA" | "MEDIA" | "ALTA" | "CRITICA";
};

export type EventoAuditoriaAdminSaas = {
  id: string;
  tipo: string;
  severidade: "BAIXA" | "MEDIA" | "ALTA" | "CRITICA";
  descricao: string;
  empresaId: string | null;
  empresaNome: string | null;
  usuarioId: string | null;
  referenciaTipo: string | null;
  referenciaId: string | null;
  metadados: string;
  criadoEm: string;
};

export type AuditoriaOperacionalAdminSaas = {
  eventosUltimos7Dias: number;
  eventosCriticosUltimos7Dias: number;
  empresasBloqueadas: number;
  trialsExpirando7Dias: number;
  chamadosCriticosAbertos: number;
  checklist: ChecklistAuditoriaAdminSaas[];
  eventosRecentes: EventoAuditoriaAdminSaas[];
  atualizadoEm: string;
};
export type PerfilDemoAdminSaas = "NUTRI" | "BEAUTY" | "GESTOR" | "INVESTIDOR" | "SUPORTE";

export type ResetDemoAdminSaasRequest = {
  perfil: PerfilDemoAdminSaas;
  confirmarReset: boolean;
  motivo?: string | null;
};

export type ResetDemoAdminSaas = {
  perfil: PerfilDemoAdminSaas;
  perfilRotulo: string;
  status: "RESET_PREPARADO" | "RESET_EXECUTADO";
  executado: boolean;
  ambiente: string;
  etapas: string[];
  credenciais: string[];
  avisos: string[];
  atualizadoEm: string;
};

export type CheckoutPagamentoSandboxRequest = {
  empresaId: string;
  planoId: string;
  emailResponsavel: string;
  nomeResponsavel: string;
  documentoResponsavel: string;
  telefoneResponsavel?: string | null;
  formaPagamentoPreferida?: string | null;
};

export type CheckoutPagamentoSandbox = {
  checkoutId: string;
  pagamentoAssinaturaId: string;
  assinaturaId: string;
  status: string;
  urlPagamento: string;
  ambiente: string;
  provedor: string;
};

export type WebhookAsaasSandboxRequest = {
  event: "PAYMENT_RECEIVED" | "PAYMENT_OVERDUE" | "PAYMENT_DELETED";
  paymentId: string;
  subscriptionId: string;
  payload?: string | null;
  token?: string | null;
};

export type WebhookPagamentoSandbox = {
  eventoId: string;
  tipo: string;
  processado: boolean;
  duplicado: boolean;
  mensagem: string;
};

export type PagamentoSandboxResumo = {
  pagamentoAssinaturaId: string;
  empresaId: string;
  planoId: string;
  assinaturaInternaId: string | null;
  provedor: string;
  ambiente: string;
  statusAssinatura: string;
  clienteExternoId: string | null;
  assinaturaExternaId: string | null;
  checkoutExternoId: string | null;
  cobrancaId: string | null;
  cobrancaExternaId: string | null;
  statusCobranca: string | null;
  valor: number | null;
  vencimento: string | null;
  formaPagamento: string | null;
  ultimoEventoId: string | null;
  ultimoEventoTipo: string | null;
  ultimoEventoProcessado: boolean;
  ultimoEventoEm: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type PagamentosSandboxPaginados = {
  itens: PagamentoSandboxResumo[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type ObservabilidadePagamentosSandboxIndicador = {
  totalCheckoutsPreparados: number;
  totalCobrancasPendentes: number;
  totalCobrancasRecebidas: number;
  totalCobrancasVencidas: number;
  totalCobrancasCanceladas: number;
  totalWebhooksProcessados: number;
  totalWebhooksNaoProcessados: number;
  totalWebhooksDuplicados: number;
  totalDivergencias: number;
};

export type ObservabilidadePagamentosSandboxDivergencia = {
  pagamentoAssinaturaId: string;
  empresaId: string;
  planoId: string | null;
  assinaturaInternaId: string | null;
  tipoDivergencia: string;
  severidade: "BAIXA" | "MEDIA" | "ALTA";
  descricao: string;
  statusAssinatura: string;
  statusCobranca: string | null;
  assinaturaExternaId: string | null;
  cobrancaExternaId: string | null;
  eventoTipo: string | null;
  eventoProcessado: boolean | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type ObservabilidadePagamentosSandbox = {
  indicadores: ObservabilidadePagamentosSandboxIndicador;
  divergencias: ObservabilidadePagamentosSandboxDivergencia[];
};

export type ReconciliacaoDivergenciaPagamentosSandbox = {
  pagamentoAssinaturaId: string;
  tipoDivergencia: string;
  tipoEvento: string | null;
  processado: boolean;
  duplicado: boolean;
  ignorado: boolean;
  motivo: string;
  mensagem: string;
};

export type ReconciliacaoDivergenciasPagamentosSandboxResult = {
  totalEncontradas: number;
  totalProcessadas: number;
  totalIgnoradas: number;
  totalDuplicadas: number;
  totalFalhas: number;
  itens: ReconciliacaoDivergenciaPagamentosSandbox[];
};

export type ReconciliacaoPagamentosSandboxRequest = {
  empresaId: string;
  statusAssinatura?: string;
  eventoTipo?: string;
  tipoDivergencia?: string;
  severidade?: string;
  token?: string | null;
};

const adminApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function listarPlanos(params: { pagina: number; tamanho: number; busca?: string }) {
  return adminApi.get<PlanosPaginados>("/api/admin-saas/planos", {
    query: {
      pagina: params.pagina,
      tamanho: params.tamanho,
      busca: params.busca || undefined
    }
  });
}

export function criarPlano(request: SalvarPlanoRequest) {
  return adminApi.post<Plano>("/api/admin-saas/planos", request);
}

export function atualizarPlano(planoId: string, request: SalvarPlanoRequest) {
  return adminApi.put<Plano>(`/api/admin-saas/planos/${planoId}`, request);
}

export function consultarDashboardAdminSaas() {
  return adminApi.get<AdminSaasDashboard>("/api/admin-saas/dashboard");
}

export function consultarDashboardVendasAdminSaas() {
  return adminApi.get<DashboardVendasAdminSaas>("/api/admin-saas/dashboard/vendas");
}

export function resetarDemoAdminSaas(request: ResetDemoAdminSaasRequest) {
  return adminApi.post<ResetDemoAdminSaas>("/api/admin-saas/demo/reset", request);
}
export function consultarAuditoriaOperacionalAdminSaas() {
  return adminApi.get<AuditoriaOperacionalAdminSaas>("/api/admin-saas/auditoria/operacional");
}

export function prepararCheckoutPagamentoSandbox(request: CheckoutPagamentoSandboxRequest) {
  return adminApi.post<CheckoutPagamentoSandbox>("/api/admin-saas/pagamentos/checkout/sandbox", request);
}

export function listarPagamentosSandbox(params: { pagina: number; tamanho: number; empresaId?: string; status?: string }) {
  return adminApi.get<PagamentosSandboxPaginados>("/api/admin-saas/pagamentos/assinaturas", {
    query: {
      pagina: params.pagina,
      tamanho: params.tamanho,
      empresaId: params.empresaId || undefined,
      status: params.status || undefined
    }
  });
}

export function registrarWebhookAsaasSandbox(request: WebhookAsaasSandboxRequest) {
  const { token, ...body } = request;
  return adminApi.post<WebhookPagamentoSandbox>("/api/admin-saas/pagamentos/webhooks/asaas", body, {
    headers: token ? { "X-AtendePro-Webhook-Token": token } : undefined
  });
}

export function consultarObservabilidadePagamentosSandbox(params: {
  empresaId?: string;
  statusAssinatura?: string;
  eventoTipo?: string;
  tipoDivergencia?: string;
  severidade?: string;
}) {
  return adminApi.get<ObservabilidadePagamentosSandbox>("/api/admin-saas/pagamentos/observabilidade", {
    query: {
      empresaId: params.empresaId || undefined,
      statusAssinatura: params.statusAssinatura || undefined,
      eventoTipo: params.eventoTipo || undefined,
      tipoDivergencia: params.tipoDivergencia || undefined,
      severidade: params.severidade || undefined
    }
  });
}

export function reconciliarDivergenciasPagamentosSandbox(params: ReconciliacaoPagamentosSandboxRequest) {
  const { empresaId, token, ...body } = params;
  return adminApi.post<ReconciliacaoDivergenciasPagamentosSandboxResult>(
    "/api/admin-saas/pagamentos/observabilidade/reconciliar",
    {
      empresaId,
      ...body
    },
    {
      headers: token ? { "X-AtendePro-Webhook-Token": token } : undefined
    }
  );
}
