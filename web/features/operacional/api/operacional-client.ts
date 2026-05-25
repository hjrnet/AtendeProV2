import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type EmpresaResumo = {
  id: string;
  nomeFantasia: string;
  razaoSocial: string | null;
  documento: string;
  email: string | null;
  telefone: string | null;
  ativo: boolean;
  criadoEm: string;
};

export type EmpresasPaginadas = {
  itens: EmpresaResumo[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type DashboardEmpresa = {
  empresaId: string;
  clientesAtivos: number;
  compromissosHoje: number;
  compromissosProximos7Dias: number;
  servicosAtivos: number;
  produtosEstoqueBaixo: number;
  produtosVencendo30Dias: number;
  equipamentosManutencao30Dias: number;
  custosGeraisAtivos: number;
  custosAlimentacaoTransporteAtivos: number;
  atualizadoEm: string;
};

export type TipoResultadoBusca =
  | "CLIENTE_PACIENTE"
  | "COMPROMISSO_AGENDA"
  | "SERVICO_PROCEDIMENTO"
  | "CUSTO"
  | "PRODUTO_ESTOQUE"
  | "EQUIPAMENTO";

export type ResultadoBuscaGlobal = {
  id: string;
  tipo: TipoResultadoBusca;
  titulo: string;
  descricao: string;
  categoria: string;
  status: string;
  destino: string;
  dataReferencia: string | null;
};

export type BuscaGlobal = {
  empresaId: string;
  busca: string | null;
  categoria: string | null;
  status: string | null;
  limitePorTipo: number;
  totalItens: number;
  itens: ResultadoBuscaGlobal[];
};

const operacionalApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function listarEmpresas(params: { pagina?: number; tamanho?: number }) {
  return operacionalApi.get<EmpresasPaginadas>("/api/empresas", {
    query: {
      pagina: params.pagina ?? 0,
      tamanho: params.tamanho ?? 20
    }
  });
}

export function consultarDashboardEmpresa(empresaId: string) {
  return operacionalApi.get<DashboardEmpresa>("/api/dashboard/empresa", {
    query: { empresaId }
  });
}

export function buscarGlobal(params: {
  empresaId: string;
  busca?: string;
  categoria?: string;
  status?: string;
  limitePorTipo?: number;
}) {
  return operacionalApi.get<BuscaGlobal>("/api/busca/global", {
    query: {
      empresaId: params.empresaId,
      busca: params.busca || undefined,
      categoria: params.categoria || undefined,
      status: params.status || undefined,
      limitePorTipo: params.limitePorTipo ?? 5
    }
  });
}
