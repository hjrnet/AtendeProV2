import { criarApiClient } from "@/lib/api";
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

const precificacaoApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function calcularPrecoRecomendado(request: CalculoPrecoRecomendadoRequest) {
  return precificacaoApi.post<PrecoRecomendadoResponse>("/api/precificacao/calculos/preco-recomendado", request);
}

export function calcularMargemLucro(request: CalculoMargemLucroRequest) {
  return precificacaoApi.post<MargemLucroResponse>("/api/precificacao/calculos/margem-lucro", request);
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
