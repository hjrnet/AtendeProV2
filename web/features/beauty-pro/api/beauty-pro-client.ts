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

export type TipoProtocoloBeautyPro = "FACIAL" | "CORPORAL" | "CAPILAR" | "CILIOS_SOBRANCELHAS" | "SALAO" | "PERSONALIZADO";

export type StatusPacoteBeautyPro = "ATIVO" | "CONCLUIDO" | "CANCELADO" | "PAUSADO";

export type SessaoProtocoloBeautyPro = {
  id: string;
  empresaId: string;
  protocoloId: string;
  clienteId: string;
  agendaCompromissoId: string | null;
  numeroSessao: number;
  realizadaEm: string;
  descricaoExecucao: string;
  evolucaoCliente: string | null;
  produtosUtilizados: string | null;
  orientacoes: string | null;
  criadoEm: string;
};

export type ProtocoloBeautyPro = {
  id: string;
  empresaId: string;
  clienteId: string;
  servicoProcedimentoId: string | null;
  nome: string;
  tipo: TipoProtocoloBeautyPro;
  tipoRotulo: string;
  objetivo: string;
  quantidadeSessoesPrevistas: number;
  sessoesRealizadas: number;
  sessoesRestantes: number;
  status: StatusPacoteBeautyPro;
  statusRotulo: string;
  observacoes: string | null;
  sessoes: SessaoProtocoloBeautyPro[];
  criadoEm: string;
  atualizadoEm: string;
};

export type ProtocolosBeautyPro = {
  itens: ProtocoloBeautyPro[];
};

export type StatusTermoBeautyPro = "GERADO" | "ACEITO" | "CANCELADO";

export type TipoPlaceholderEvolucaoBeautyPro = "FACE_NEUTRA" | "CORPORAL_NEUTRO" | "AREA_TRATADA" | "TEXTUAL";

export type TermoConsentimentoBeautyPro = {
  id: string;
  empresaId: string;
  clienteId: string;
  protocoloId: string | null;
  titulo: string;
  conteudo: string;
  status: StatusTermoBeautyPro;
  statusRotulo: string;
  aceiteProfissional: boolean;
  criadoEm: string;
  atualizadoEm: string;
};

export type EvidenciaEvolucaoBeautyPro = {
  id: string;
  empresaId: string;
  clienteId: string;
  protocoloId: string | null;
  sessaoId: string | null;
  tipoPlaceholder: TipoPlaceholderEvolucaoBeautyPro;
  tipoPlaceholderRotulo: string;
  titulo: string;
  descricao: string;
  observacoesPrivacidade: string | null;
  avisoPrivacidade: string;
  criadoEm: string;
};

export type ProdutoBeautyEstoque = {
  id: string;
  nome: string;
  categoria: string | null;
  lote: string | null;
  validade: string | null;
  unidade: string;
  quantidadeAtual: number;
  estoqueMinimo: number;
  estoqueBaixo: boolean;
  validadeEmAlerta: boolean;
};

export type ProdutoUtilizadoBeautyPro = {
  id: string;
  empresaId: string;
  clienteId: string;
  protocoloId: string | null;
  sessaoId: string | null;
  produtoEstoqueId: string | null;
  nomeProduto: string;
  lote: string | null;
  validade: string | null;
  quantidade: number;
  unidade: string;
  alertaValidade: boolean;
  alertaEstoqueBaixo: boolean;
  statusRotulo: string;
  observacoes: string | null;
  criadoEm: string;
};

export type SegurancaOperacionalBeautyPro = {
  termos: TermoConsentimentoBeautyPro[];
  evidencias: EvidenciaEvolucaoBeautyPro[];
  produtosUtilizados: ProdutoUtilizadoBeautyPro[];
  produtosEstoque: ProdutoBeautyEstoque[];
};

export type CriarProtocoloBeautyProInput = {
  servicoProcedimentoId?: string | null;
  nome: string;
  tipo: TipoProtocoloBeautyPro;
  objetivo: string;
  quantidadeSessoesPrevistas: number;
  observacoes?: string | null;
};

export type RegistrarSessaoProtocoloBeautyProInput = {
  agendaCompromissoId?: string | null;
  realizadaEm?: string | null;
  descricaoExecucao: string;
  evolucaoCliente?: string | null;
  produtosUtilizados?: string | null;
  orientacoes?: string | null;
};

export type CriarTermoConsentimentoBeautyProInput = {
  protocoloId?: string | null;
  titulo: string;
  conteudo: string;
  aceiteProfissional: boolean;
};

export type CriarEvidenciaEvolucaoBeautyProInput = {
  protocoloId?: string | null;
  sessaoId?: string | null;
  tipoPlaceholder: TipoPlaceholderEvolucaoBeautyPro;
  titulo: string;
  descricao: string;
  observacoesPrivacidade?: string | null;
};

export type VincularProdutoBeautyProInput = {
  protocoloId?: string | null;
  sessaoId?: string | null;
  produtoEstoqueId?: string | null;
  nomeProduto?: string | null;
  lote?: string | null;
  validade?: string | null;
  quantidade: number;
  unidade: string;
  observacoes?: string | null;
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

export function listarProtocolosBeautyPro(params: { empresaId: string; clienteId: string }) {
  return beautyProApi.get<ProtocolosBeautyPro>(`/api/beauty-pro/clientes/${params.clienteId}/protocolos`, {
    query: { empresaId: params.empresaId }
  });
}

export function criarProtocoloBeautyPro(params: {
  empresaId: string;
  clienteId: string;
  dados: CriarProtocoloBeautyProInput;
}) {
  return beautyProApi.post<ProtocoloBeautyPro>(
    `/api/beauty-pro/clientes/${params.clienteId}/protocolos`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function registrarSessaoProtocoloBeautyPro(params: {
  empresaId: string;
  clienteId: string;
  protocoloId: string;
  dados: RegistrarSessaoProtocoloBeautyProInput;
}) {
  return beautyProApi.post<SessaoProtocoloBeautyPro>(
    `/api/beauty-pro/clientes/${params.clienteId}/protocolos/${params.protocoloId}/sessoes`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function consultarSegurancaOperacionalBeautyPro(params: { empresaId: string; clienteId: string }) {
  return beautyProApi.get<SegurancaOperacionalBeautyPro>(`/api/beauty-pro/clientes/${params.clienteId}/seguranca-operacional`, {
    query: { empresaId: params.empresaId }
  });
}

export function criarTermoConsentimentoBeautyPro(params: {
  empresaId: string;
  clienteId: string;
  dados: CriarTermoConsentimentoBeautyProInput;
}) {
  return beautyProApi.post<TermoConsentimentoBeautyPro>(`/api/beauty-pro/clientes/${params.clienteId}/termos`, params.dados, {
    query: { empresaId: params.empresaId }
  });
}

export function criarEvidenciaEvolucaoBeautyPro(params: {
  empresaId: string;
  clienteId: string;
  dados: CriarEvidenciaEvolucaoBeautyProInput;
}) {
  return beautyProApi.post<EvidenciaEvolucaoBeautyPro>(`/api/beauty-pro/clientes/${params.clienteId}/evidencias`, params.dados, {
    query: { empresaId: params.empresaId }
  });
}

export function vincularProdutoBeautyPro(params: {
  empresaId: string;
  clienteId: string;
  dados: VincularProdutoBeautyProInput;
}) {
  return beautyProApi.post<ProdutoUtilizadoBeautyPro>(`/api/beauty-pro/clientes/${params.clienteId}/produtos`, params.dados, {
    query: { empresaId: params.empresaId }
  });
}
