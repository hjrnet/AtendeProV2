import { ApiError, criarApiClient, type ApiErrorPayload } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type AlertaPrecificacao = {
  codigo: string;
  nivel: "CRITICO" | "ATENCAO";
  mensagem: string;
};

export type ItemCustoPrecificacao = {
  descricao: string;
  categoria: string;
  valor: number;
};

export type CalculoPrecoRecomendadoRequest = {
  empresaId: string;
  nomeProcedimento: string;
  duracaoMinutos: number;
  custoInsumos: number;
  custoSalaPorHora: number;
  valorHoraProfissional: number;
  custoDeslocamento: number;
  custoAlimentacao: number;
  taxas: number;
  margemDesejadaPercentual: number;
};

export type CalculoMargemLucroRequest = Omit<CalculoPrecoRecomendadoRequest, "margemDesejadaPercentual"> & {
  precoVenda: number;
};

export type SalvarSimulacaoPrecificacaoRequest = CalculoPrecoRecomendadoRequest & {
  servicoProcedimentoId?: string | null;
  precoVenda: number;
};

export type PrecoRecomendadoResponse = {
  empresaId: string;
  servicoProcedimentoId: string | null;
  nomeProcedimento: string;
  duracaoMinutos: number;
  precoBaseServico: number | null;
  custoTotal: number;
  precoMinimo: number;
  margemDesejadaPercentual: number;
  precoRecomendado: number;
  itensCusto: ItemCustoPrecificacao[];
  calculadoEm: string;
};

export type MargemLucroResponse = {
  empresaId: string;
  servicoProcedimentoId: string | null;
  nomeProcedimento: string;
  duracaoMinutos: number;
  precoBaseServico: number | null;
  custoTotal: number;
  precoMinimo: number;
  precoVenda: number;
  lucroEstimado: number;
  margemRealPercentual: number;
  status: "PREJUIZO" | "EQUILIBRIO" | "MARGEM_BAIXA" | "SAUDAVEL";
  alertas: AlertaPrecificacao[];
  itensCusto: ItemCustoPrecificacao[];
  calculadoEm: string;
};

export type SimulacaoPrecificacao = {
  id: string;
  empresaId: string;
  servicoProcedimentoId: string | null;
  nomeProcedimento: string;
  duracaoMinutos: number;
  custoInsumos: number;
  custoSalaPorHora: number;
  valorHoraProfissional: number;
  custoDeslocamento: number;
  custoAlimentacao: number;
  taxas: number;
  margemDesejadaPercentual: number;
  precoVenda: number;
  custoTotal: number;
  precoMinimo: number;
  precoRecomendado: number;
  lucroEstimado: number;
  margemRealPercentual: number;
  statusMargem: MargemLucroResponse["status"];
  ativo: boolean;
  criadoEm: string;
  atualizadoEm: string;
};

export type SimulacoesPrecificacaoPaginadas = {
  itens: SimulacaoPrecificacao[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type DistribuicaoStatusPrecificacao = {
  status: MargemLucroResponse["status"];
  total: number;
};

export type SimulacaoDashboardPrecificacao = {
  nomeProcedimento: string;
  custoTotal: number;
  precoRecomendado: number;
  precoVenda: number;
  margemRealPercentual: number;
  atualizadoEm: string;
};

export type DashboardPrecificacao = {
  empresaId: string;
  totalSimulacoes: number;
  custoMedio: number;
  precoMedioRecomendado: number;
  precoMedioVenda: number;
  lucroMedio: number;
  margemMediaPercentual: number;
  simulacoesSaudaveis: number;
  simulacoesComAlerta: number;
  distribuicaoStatus: DistribuicaoStatusPrecificacao[];
  simulacoesRecentes: SimulacaoDashboardPrecificacao[];
  atualizadoEm: string;
};

const precificacaoApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export function calcularPrecoRecomendado(request: CalculoPrecoRecomendadoRequest) {
  return precificacaoApi.post<PrecoRecomendadoResponse>("/api/precificacao/calculos/preco-recomendado", request);
}

export function calcularMargemLucro(request: CalculoMargemLucroRequest) {
  return precificacaoApi.post<MargemLucroResponse>("/api/precificacao/calculos/margem-lucro", request);
}

export function consultarDashboardPrecificacao(empresaId: string) {
  return precificacaoApi.get<DashboardPrecificacao>("/api/precificacao/dashboard", {
    query: { empresaId }
  });
}

export function listarSimulacoesPrecificacao(params: { empresaId: string; pagina: number; tamanho: number; busca?: string }) {
  return precificacaoApi.get<SimulacoesPrecificacaoPaginadas>("/api/precificacao/simulacoes", {
    query: {
      empresaId: params.empresaId,
      pagina: params.pagina,
      tamanho: params.tamanho,
      busca: params.busca || undefined
    }
  });
}

export function salvarSimulacaoPrecificacao(request: SalvarSimulacaoPrecificacaoRequest) {
  return precificacaoApi.post<SimulacaoPrecificacao>("/api/precificacao/simulacoes", request);
}

export function atualizarSimulacaoPrecificacao(simulacaoId: string, request: SalvarSimulacaoPrecificacaoRequest) {
  return precificacaoApi.put<SimulacaoPrecificacao>(`/api/precificacao/simulacoes/${simulacaoId}`, request);
}

export async function baixarRelatorioPrecificacao(simulacaoId: string) {
  const token = carregarSessaoAutenticada()?.accessToken;
  const response = await fetch(new URL(`/api/precificacao/simulacoes/${simulacaoId}/relatorio.pdf`, API_BASE_URL), {
    headers: {
      Accept: "application/pdf",
      ...(token ? { Authorization: `Bearer ${token}` } : {})
    }
  });

  if (response.status === 401) {
    limparSessaoAutenticada();
  }

  if (!response.ok) {
    throw await criarErroRelatorio(response);
  }

  return response.blob();
}

async function criarErroRelatorio(response: Response) {
  const contentType = response.headers.get("Content-Type") ?? "";
  if (contentType.includes("application/json")) {
    try {
      const payload = (await response.json()) as ApiErrorPayload;
      return new ApiError(response.status, payload.mensagem ?? "Não foi possível gerar o relatório.", payload);
    } catch {
      return new ApiError(response.status, "Não foi possível gerar o relatório.");
    }
  }
  return new ApiError(response.status, "Não foi possível gerar o relatório.");
}
