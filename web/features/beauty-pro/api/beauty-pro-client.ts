import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type StatusOperacionalBeautyPro = "OPERACIONAL" | "CONFIGURACAO_PENDENTE";

export type IndicadorBeautyPro = {
  codigo: string;
  titulo: string;
  valor: number;
  descricao: string;
  status: string;
};

export type AtalhoBeautyPro = {
  codigo: string;
  titulo: string;
  descricao: string;
  status: string;
  destino: string;
};

export type ClienteBeautyResumo = {
  id: string;
  nome: string;
  telefone: string | null;
  observacoes: string | null;
  ativo: boolean;
  atualizadoEm: string;
};

export type ClientesBeautyPro = {
  itens: ClienteBeautyResumo[];
};

export type ClienteBeautyProntuario = {
  id: string;
  empresaId: string;
  nome: string;
  email: string | null;
  telefone: string | null;
  dataNascimento: string | null;
  idade: number | null;
  observacoes: string | null;
  ativo: boolean;
  atualizadoEm: string;
};

export type ObjetivoEsteticoBeautyPro =
  | "ACNE"
  | "MANCHAS"
  | "REJUVENESCIMENTO"
  | "CORPORAL"
  | "RELAXAMENTO"
  | "CAPILAR"
  | "CILIOS_SOBRANCELHAS"
  | "SALAO";

export type FichaEsteticaBeautyPro = {
  id: string;
  empresaId: string;
  clienteId: string;
  objetivo: ObjetivoEsteticoBeautyPro;
  objetivoRotulo: string;
  queixaPrincipal: string;
  historicoEstetico: string | null;
  alergias: string | null;
  medicamentos: string | null;
  gestante: boolean;
  lactante: boolean;
  sensibilidadePele: boolean;
  usaAcidos: boolean;
  exposicaoSolarIntensa: boolean;
  procedimentosRecentes: string | null;
  contraindicacoes: string | null;
  observacoes: string | null;
  possuiAlertaContraindicacao: boolean;
  alertaContraindicacoes: string;
  criadoEm: string;
  atualizadoEm: string;
};

export type ResumoProntuarioBeautyPro = {
  fichasEsteticas: number;
  consultasFuturas: number;
  documentos: number;
  statusFichaEstetica: string;
  statusContraindicacoes: string;
  ultimaConsultaEm: string | null;
};

export type ProntuarioBeautyPro = {
  cliente: ClienteBeautyProntuario;
  resumo: ResumoProntuarioBeautyPro;
  fichaAtual: FichaEsteticaBeautyPro | null;
};

export type FichasEsteticasBeautyPro = {
  itens: FichaEsteticaBeautyPro[];
};

export type SalvarFichaEsteticaBeautyProInput = {
  objetivo: ObjetivoEsteticoBeautyPro;
  queixaPrincipal: string;
  historicoEstetico?: string | null;
  alergias?: string | null;
  medicamentos?: string | null;
  gestante: boolean;
  lactante: boolean;
  sensibilidadePele: boolean;
  usaAcidos: boolean;
  exposicaoSolarIntensa: boolean;
  procedimentosRecentes?: string | null;
  contraindicacoes?: string | null;
  observacoes?: string | null;
};

export type VisaoBeautyPro = {
  empresaId: string;
  empresaNome: string;
  statusOperacional: StatusOperacionalBeautyPro;
  statusOperacionalRotulo: string;
  mensagemStatus: string;
  indicadores: IndicadorBeautyPro[];
  atalhosPrioritarios: AtalhoBeautyPro[];
  proximasEvolucoes: AtalhoBeautyPro[];
  clientesRecentes: ClienteBeautyResumo[];
  atualizadoEm: string;
};

const beautyProApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function consultarVisaoBeautyPro(empresaId: string) {
  return beautyProApi.get<VisaoBeautyPro>("/api/beauty-pro/visao", {
    query: { empresaId }
  });
}

export function listarClientesBeautyPro(params: { empresaId: string; busca?: string }) {
  return beautyProApi.get<ClientesBeautyPro>("/api/beauty-pro/clientes", {
    query: {
      empresaId: params.empresaId,
      busca: params.busca || undefined
    }
  });
}

export function consultarProntuarioBeautyPro(params: { empresaId: string; clienteId: string }) {
  return beautyProApi.get<ProntuarioBeautyPro>(`/api/beauty-pro/clientes/${params.clienteId}/prontuario`, {
    query: { empresaId: params.empresaId }
  });
}

export function listarFichasEsteticasBeautyPro(params: { empresaId: string; clienteId: string }) {
  return beautyProApi.get<FichasEsteticasBeautyPro>(`/api/beauty-pro/clientes/${params.clienteId}/fichas-esteticas`, {
    query: { empresaId: params.empresaId }
  });
}

export function criarFichaEsteticaBeautyPro(params: {
  empresaId: string;
  clienteId: string;
  dados: SalvarFichaEsteticaBeautyProInput;
}) {
  return beautyProApi.post<FichaEsteticaBeautyPro>(
    `/api/beauty-pro/clientes/${params.clienteId}/fichas-esteticas`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function atualizarFichaEsteticaBeautyPro(params: {
  empresaId: string;
  clienteId: string;
  fichaId: string;
  dados: SalvarFichaEsteticaBeautyProInput;
}) {
  return beautyProApi.put<FichaEsteticaBeautyPro>(
    `/api/beauty-pro/clientes/${params.clienteId}/fichas-esteticas/${params.fichaId}`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}
