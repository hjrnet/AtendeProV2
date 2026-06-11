import { Platform } from "react-native";
import { obterSessaoAutenticada } from "@/lib/auth";

type MetodoHttp = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

type OpcoesRequisicao = Omit<RequestInit, "body"> & {
  body?: unknown;
  query?: Record<string, string | number | boolean | null | undefined>;
};

export type ConfigApi = {
  getAccessToken?: () => Promise<string | null> | string | null;
  apiBaseUrl?: string;
};

export type ResultadoPaginado<T> = {
  itens: T[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type LoginRequest = {
  email: string;
  senha: string;
};

export type UsuarioLoginApi = {
  id: string;
  empresaId: string | null;
  nome: string;
  email: string;
  perfis: string[];
  authorities: string[];
};

export type LoginResponse = {
  accessToken: string;
  refreshToken?: string | null;
  tipoToken?: string | null;
  expiraEm?: string | null;
  usuario: UsuarioLoginApi;
};

export type TipoStatusAgenda =
  | "AGENDADO"
  | "CONFIRMADO"
  | "REALIZADO"
  | "CANCELADO"
  | "FALTOU"
  | "REMARCADO";

export type TipoCompromissoAgenda =
  | "ATENDIMENTO"
  | "RETORNO"
  | "AVALIACAO"
  | "BLOQUEIO"
  | "OUTRO";

export type CompromissoAgendaApi = {
  id: string;
  empresaId: string;
  clientePacienteId: string | null;
  profissionalId: string | null;
  profissionalNome: string;
  sala: string | null;
  tipo: TipoCompromissoAgenda;
  status: TipoStatusAgenda;
  inicio: string;
  fim: string;
  observacoes: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type TipoCliente = "CLIENTE" | "PACIENTE" | "CLIENTE_PACIENTE";

export type AreaCliente = "GERAL" | "NUTRI" | "BEAUTY" | "BIOMED" | "FISIO" | "SPACES" | "PSICO" | "FONO" | "FARMACIA_CLINICA" | "ODONTO" | "TERAPIAS_INTEGRATIVAS";

export type ClientePacienteApi = {
  id: string;
  empresaId: string;
  nome: string;
  tipo: TipoCliente;
  area: AreaCliente;
  documento: string | null;
  email: string | null;
  telefone: string | null;
  dataNascimento: string | null;
  observacoes: string | null;
  ativo: boolean;
  criadoEm: string;
  atualizadoEm: string;
};

export type TipoDocumentoPortal =
  | "DECLARACAO"
  | "RELATORIO"
  | "TERMO"
  | "ORIENTACAO"
  | "RECIBO"
  | "SOLICITACAO_EXAMES"
  | "PRESCRICAO"
  | "PLANO_ALIMENTAR"
  | "OUTRO";

export type StatusDocumentoPortal = "RASCUNHO" | "EMITIDO" | "CANCELADO";

export type DocumentoProfissionalApi = {
  id: string;
  empresaId: string;
  clientePacienteId: string | null;
  profissionalId: string | null;
  profissionalNome: string;
  titulo: string;
  tipo: TipoDocumentoPortal;
  conteudo: string;
  status: StatusDocumentoPortal;
  versao: number;
  ativo: boolean;
  criadoEm: string;
  atualizadoEm: string;
};

export type StatusPlanoAlimentarNutri = "RASCUNHO" | "ATIVO" | "SUBSTITUIDO" | "ARQUIVADO";

export type ItemPlanoAlimentarNutriApi = {
  id: string;
  tipoItem: "ALIMENTO" | "SUPLEMENTO";
  tipoItemRotulo: string;
  nome: string;
  grupo: string | null;
  unidadeMedida: string;
  quantidade: number;
  quantidadeBase: number;
  energiaKcal: number;
  proteinas: number;
  carboidratos: number;
  lipidios: number;
  observacoes: string | null;
  ordenacao: number;
};

export type RefeicaoPlanoAlimentarNutriApi = {
  id: string;
  nome: string;
  horario: string | null;
  observacoes: string | null;
  ordenacao: number;
  itens: ItemPlanoAlimentarNutriApi[];
  energiaTotalKcal: number;
  proteinasTotal: number;
  carboidratosTotal: number;
  lipidiosTotal: number;
};

export type PlanoAlimentarNutriApi = {
  id: string;
  empresaId: string;
  pacienteId: string;
  objetivo: string;
  descricao: string | null;
  status: StatusPlanoAlimentarNutri;
  statusRotulo: string;
  refeicoes: RefeicaoPlanoAlimentarNutriApi[];
  energiaTotalKcal: number;
  proteinasTotal: number;
  carboidratosTotal: number;
  lipidiosTotal: number;
  criadoEm: string;
  atualizadoEm: string;
};

export type GrupoListaComprasNutriApi = {
  categoria: string;
  itens: {
    nome: string;
    categoria: string;
    quantidade: number;
    unidadeMedida: string;
    refeicoes: string | null;
    observacoes: string | null;
  }[];
};

export type ListaComprasNutriApi = {
  empresaId: string;
  pacienteId: string;
  planoId: string;
  objetivoPlano: string;
  grupos: GrupoListaComprasNutriApi[];
  geradoEm: string;
};

export type RegistroDiarioNutriApi = {
  id: string;
  empresaId: string;
  pacienteId: string;
  planoId: string | null;
  refeicaoNome: string | null;
  texto: string;
  evidenciaUrl: string | null;
  statusRevisao: "PENDENTE" | "REVISADO";
  parecerProfissional: string | null;
  criadoPor: "PACIENTE" | "PROFISSIONAL" | "SISTEMA";
  registradoEm: string;
  atualizadoEm: string;
};

export type MetaNutriApi = {
  id: string;
  empresaId: string;
  pacienteId: string;
  tipo: string;
  descricao: string;
  valorMeta: number;
  unidade: string | null;
  dataInicio: string;
  dataAlvo: string | null;
  status: string;
  criadoEm: string;
  atualizadoEm: string;
};

export type LembreteNutriApi = {
  id: string;
  empresaId: string;
  pacienteId: string;
  titulo: string;
  descricao: string | null;
  horario: string | null;
  frequencia: string;
  status: string;
  criadoEm: string;
  atualizadoEm: string;
};

export type MensagemNutriApi = {
  id: string;
  empresaId: string;
  pacienteId: string;
  remetenteTipo: "PACIENTE" | "PROFISSIONAL" | "SISTEMA";
  remetenteNome: string;
  texto: string;
  contexto: string | null;
  lidaPeloPaciente: boolean;
  lidaPeloProfissional: boolean;
  enviadaEm: string;
};

export type ClienteBeautyResumoApi = {
  id: string;
  nome: string;
  telefone: string | null;
  observacoes: string | null;
  ativo: boolean;
  atualizadoEm: string;
};

export type ProtocoloBeautyApi = {
  id: string;
  empresaId: string;
  clienteId: string;
  nome: string;
  tipo: string;
  tipoRotulo: string;
  objetivo: string;
  quantidadeSessoesPrevistas: number;
  sessoesRealizadas: number;
  sessoesRestantes: number;
  status: string;
  statusRotulo: string;
  observacoes: string | null;
  atualizadoEm: string;
};

export type SegurancaBeautyApi = {
  termos: unknown[];
  evidencias: unknown[];
  produtosUtilizados: Array<{
    id: string;
    nomeProduto: string;
    lote: string | null;
    validade: string | null;
    alertaValidade: boolean;
    alertaEstoqueBaixo: boolean;
    statusRotulo: string;
  }>;
  produtosEstoque: Array<{
    id: string;
    nome: string;
    validade: string | null;
    quantidadeAtual: number;
    estoqueBaixo: boolean;
    validadeEmAlerta: boolean;
  }>;
};

export type AreaPosVendaApi = "NUTRI" | "BEAUTY";

export type ClientePosVendaApi = {
  id: string;
  nome: string;
  area: AreaPosVendaApi;
  telefone: string | null;
  proximaConsultaEm: string | null;
  retornoRecomendadoEm: string | null;
  motivoRetorno: string;
  riscoAbandono: "ALTO" | "MEDIO" | "BAIXO";
  oportunidadeRecorrencia: boolean;
  statusRotulo: string;
};

export type TarefaPosVendaApi = {
  id: string | null;
  clienteId: string;
  clienteNome: string;
  area: AreaPosVendaApi;
  tipo: string;
  titulo: string;
  descricao: string | null;
  dataRecomendada: string | null;
  status: string;
  origem: string | null;
};

export type PainelPosVendaApi = {
  empresaId: string;
  area: AreaPosVendaApi | null;
  metricas: {
    clientesMonitorados: number;
    retornosPendentes: number;
    clientesInativos: number;
    faltasRecentes: number;
    clientesSemContato: number;
    oportunidadesRecorrencia: number;
    npsMedio: number;
    detratores: number;
  };
  clientes: ClientePosVendaApi[];
  tarefas: TarefaPosVendaApi[];
  atualizadoEm: string;
};

export type SugestaoPosVendaGrowthApi = {
  clienteId: string;
  clienteNome: string;
  vertical: AreaPosVendaApi;
  tipo: string;
  prioridade: "1_ALTA" | "2_MEDIA" | "3_BAIXA";
  motivo: string;
  retornoRecomendadoEm: string;
  mensagemSugerida: string;
  oportunidadePacote: string;
};

const API_HOST_PADRAO_WEB = "http://localhost:8080";
const API_HOST_PADRAO_ANDROID = "http://10.0.2.2:8080";
const API_HOST_PADRAO_IOS = "http://localhost:8080";
const TEMPO_LIMITE_REQUISICAO_MS = 15000;

declare const process: { env?: Record<string, string | undefined> } | undefined;

const API_BASE_CONFIGURADA = typeof process !== "undefined" ? process.env?.EXPO_PUBLIC_API_URL : undefined;

export const API_BASE_PADRAO = API_BASE_CONFIGURADA ?? resolverApiBasePadrao();

function montarBaseApi(url: string) {
  const base = url.replace(/\/+$/, "");
  if (base.endsWith("/api")) {
    return base;
  }

  return `${base}/api`;
}

function resolverApiBasePadrao() {
  if (Platform.OS === "android") {
    return API_HOST_PADRAO_ANDROID;
  }

  if (Platform.OS === "ios") {
    return API_HOST_PADRAO_IOS;
  }

  return API_HOST_PADRAO_WEB;
}

export function criarApiClient(config: ConfigApi = {}) {
  const baseUrl = config.apiBaseUrl ?? montarBaseApi(API_BASE_PADRAO);

  async function request<T>(path: string, options: OpcoesRequisicao = {}, method: MetodoHttp = "GET"): Promise<T> {
    const headers = new Headers(options.headers);

    const token = await config.getAccessToken?.();
    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }

    if (!headers.has("Accept")) {
      headers.set("Accept", "application/json");
    }

    if (options.body !== undefined && !headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }

    const caminho = path.startsWith("/") ? path.slice(1) : path;
    const url = new URL(caminho, baseUrl.endsWith("/") ? baseUrl : `${baseUrl}/`);
    Object.entries(options.query ?? {}).forEach(([chave, valor]) => {
      if (valor !== undefined && valor !== null) {
        url.searchParams.set(chave, String(valor));
      }
    });

    const bodyPreparado = prepararBody(options.body);
    const resposta = await executarFetchComRetry(url, {
      ...options,
      method,
      body: bodyPreparado,
      headers
    }, method);

    if (resposta.status === 204) {
      return undefined as T;
    }

    const corpoTexto = await resposta.text();
    if (!resposta.ok) {
      throw parseErroRequisicao(resposta.status, corpoTexto);
    }

    if (!corpoTexto) {
      return undefined as T;
    }

    return JSON.parse(corpoTexto) as T;
  }

  return {
    get: <T>(path: string, options?: OpcoesRequisicao) => request<T>(path, options, "GET"),
    post: <T>(path: string, body?: unknown, options?: OpcoesRequisicao) =>
      request<T>(path, { ...options, body }, "POST"),
    put: <T>(path: string, body?: unknown, options?: OpcoesRequisicao) =>
      request<T>(path, { ...options, body }, "PUT"),
    patch: <T>(path: string, body?: unknown, options?: OpcoesRequisicao) =>
      request<T>(path, { ...options, body }, "PATCH"),
    delete: <T>(path: string, options?: OpcoesRequisicao) => request<T>(path, options, "DELETE"),
    request
  };
}

async function executarFetchComRetry(url: URL, init: RequestInit, method: MetodoHttp) {
  try {
    return await executarFetchComTimeout(url, init);
  } catch (falha) {
    if (method === "GET") {
      return executarFetchComTimeout(url, init);
    }

    throw normalizarErroRede(falha);
  }
}

async function executarFetchComTimeout(url: URL, init: RequestInit) {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), TEMPO_LIMITE_REQUISICAO_MS);

  try {
    return await fetch(url, {
      ...init,
      signal: init.signal ?? controller.signal
    });
  } catch (falha) {
    throw normalizarErroRede(falha);
  } finally {
    clearTimeout(timeoutId);
  }
}

function normalizarErroRede(falha: unknown) {
  if (falha instanceof Error && falha.name === "AbortError") {
    return new Error("Tempo de resposta excedido. Verifique a conexão e tente novamente.");
  }

  if (falha instanceof Error) {
    return new Error(`Falha de conexão com o AtendePro: ${falha.message}`);
  }

  return new Error("Falha de conexão com o AtendePro.");
}

function prepararBody(body: unknown) {
  if (body === undefined) {
    return undefined;
  }

  if (body instanceof FormData) {
    return body;
  }

  return JSON.stringify(body);
}

function parseErroRequisicao(status: number, corpoTexto: string) {
  if (!corpoTexto) {
    return new Error(`Erro HTTP ${status}`);
  }

  try {
    const payload = JSON.parse(corpoTexto) as { mensagem?: string; detalhe?: string; detalhes?: string[] };
    const detalhes = payload.mensagem ?? payload.detalhe ?? "Erro ao processar a requisição.";
    if (payload.detalhes?.length) {
      return new Error([detalhes, ...payload.detalhes].join(" | "));
    }

    return new Error(detalhes);
  } catch {
    return new Error(`Erro HTTP ${status}: ${corpoTexto.slice(0, 220)}`);
  }
}

export const apiClient = criarApiClient();

export async function autenticarUsuario(request: LoginRequest) {
  return apiClient.post<LoginResponse>("/auth/login", request);
}

function getTokenDaSessao() {
  return obterSessaoAutenticada().then((sessao) => sessao?.accessToken ?? null);
}

export const apiClientAutenticado = criarApiClient({
  getAccessToken: getTokenDaSessao
});

export async function listarClientesPortal(params: {
  empresaId: string | null;
  busca?: string;
  area?: AreaCliente;
  ativo?: boolean;
  pagina?: number;
  tamanho?: number;
}) {
  return apiClientAutenticado.get<ResultadoPaginado<ClientePacienteApi>>("/clientes-pacientes", {
    query: {
      empresaId: params.empresaId,
      busca: params.busca ?? undefined,
      area: params.area,
      ativo: params.ativo,
      pagina: params.pagina ?? 0,
      tamanho: params.tamanho ?? 30
    }
  });
}

export async function listarAgendaPortal(params: {
  empresaId: string | null;
  inicio?: string;
  fim?: string;
  status?: TipoStatusAgenda;
  clientePacienteId?: string;
  pagina?: number;
  tamanho?: number;
}) {
  return apiClientAutenticado.get<ResultadoPaginado<CompromissoAgendaApi>>("/agenda/compromissos", {
    query: {
      empresaId: params.empresaId,
      inicio: params.inicio,
      fim: params.fim,
      status: params.status,
      clientePacienteId: params.clientePacienteId,
      pagina: params.pagina ?? 0,
      tamanho: params.tamanho ?? 80
    }
  });
}

export async function listarDocumentosPortal(params: {
  empresaId: string | null;
  clientePacienteId?: string;
  ativo?: boolean;
  pagina?: number;
  tamanho?: number;
}) {
  return apiClientAutenticado.get<ResultadoPaginado<DocumentoProfissionalApi>>("/documentos-profissionais", {
    query: {
      empresaId: params.empresaId,
      clientePacienteId: params.clientePacienteId,
      ativo: params.ativo ?? true,
      pagina: params.pagina ?? 0,
      tamanho: params.tamanho ?? 40
    }
  });
}

export async function resolverPrimeiroPacienteNutri(empresaId: string | null) {
  const resposta = await listarClientesPortal({
    empresaId,
    area: "NUTRI",
    ativo: true,
    pagina: 0,
    tamanho: 1
  });
  return resposta.itens[0] ?? null;
}

export async function resolverPrimeiroClienteBeauty(empresaId: string | null) {
  const resposta = await listarClientesPortal({
    empresaId,
    area: "BEAUTY",
    ativo: true,
    pagina: 0,
    tamanho: 1
  });
  return resposta.itens[0] ?? null;
}

export async function consultarPlanoPublicadoNutri(params: { empresaId: string | null; pacienteId: string }) {
  return apiClientAutenticado.get<PlanoAlimentarNutriApi>(`/nutri-pro/pacientes/${params.pacienteId}/plano-publicado`, {
    query: { empresaId: params.empresaId }
  });
}

export async function consultarListaComprasNutri(params: { empresaId: string | null; pacienteId: string }) {
  return apiClientAutenticado.get<ListaComprasNutriApi>(`/nutri-pro/pacientes/${params.pacienteId}/lista-compras`, {
    query: { empresaId: params.empresaId }
  });
}

export async function listarDiarioAlimentarNutri(params: { empresaId: string | null; pacienteId: string }) {
  return apiClientAutenticado.get<{ itens: RegistroDiarioNutriApi[] }>(`/nutri-pro/pacientes/${params.pacienteId}/diario-alimentar`, {
    query: { empresaId: params.empresaId }
  });
}

export async function criarRegistroDiarioNutri(params: {
  empresaId: string | null;
  pacienteId: string;
  texto: string;
  refeicaoNome?: string | null;
}) {
  return apiClientAutenticado.post<RegistroDiarioNutriApi>(
    `/nutri-pro/pacientes/${params.pacienteId}/diario-alimentar`,
    {
      texto: params.texto,
      refeicaoNome: params.refeicaoNome ?? "Registro do paciente"
    },
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export async function listarMetasNutri(params: { empresaId: string | null; pacienteId: string }) {
  return apiClientAutenticado.get<{ itens: MetaNutriApi[] }>(`/nutri-pro/pacientes/${params.pacienteId}/metas`, {
    query: { empresaId: params.empresaId }
  });
}

export async function listarLembretesNutri(params: { empresaId: string | null; pacienteId: string }) {
  return apiClientAutenticado.get<{ itens: LembreteNutriApi[] }>(`/nutri-pro/pacientes/${params.pacienteId}/lembretes`, {
    query: { empresaId: params.empresaId }
  });
}

export async function listarMensagensNutri(params: { empresaId: string | null; pacienteId: string }) {
  return apiClientAutenticado.get<{ itens: MensagemNutriApi[] }>(`/nutri-pro/pacientes/${params.pacienteId}/mensagens`, {
    query: { empresaId: params.empresaId }
  });
}

export async function enviarMensagemNutri(params: {
  empresaId: string | null;
  pacienteId: string;
  remetenteTipo: "PACIENTE" | "PROFISSIONAL" | "SISTEMA";
  remetenteNome: string;
  texto: string;
  contexto?: string | null;
}) {
  return apiClientAutenticado.post<MensagemNutriApi>(
    `/nutri-pro/pacientes/${params.pacienteId}/mensagens`,
    {
      remetenteTipo: params.remetenteTipo,
      remetenteNome: params.remetenteNome,
      texto: params.texto,
      contexto: params.contexto ?? "Mobile Nutri"
    },
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export async function listarClientesBeauty(params: { empresaId: string | null; busca?: string }) {
  return apiClientAutenticado.get<{ itens: ClienteBeautyResumoApi[] }>("/beauty-pro/clientes", {
    query: {
      empresaId: params.empresaId,
      busca: params.busca || undefined
    }
  });
}

export async function listarProtocolosBeauty(params: { empresaId: string | null; clienteId: string }) {
  return apiClientAutenticado.get<{ itens: ProtocoloBeautyApi[] }>(`/beauty-pro/clientes/${params.clienteId}/protocolos`, {
    query: { empresaId: params.empresaId }
  });
}

export async function consultarSegurancaBeauty(params: { empresaId: string | null; clienteId: string }) {
  return apiClientAutenticado.get<SegurancaBeautyApi>(`/beauty-pro/clientes/${params.clienteId}/seguranca-operacional`, {
    query: { empresaId: params.empresaId }
  });
}

export async function consultarPainelPosVenda(params: { empresaId: string | null; area: AreaPosVendaApi; busca?: string }) {
  return apiClientAutenticado.get<PainelPosVendaApi>("/relacionamento/pos-venda", {
    query: {
      empresaId: params.empresaId,
      area: params.area,
      busca: params.busca || undefined
    }
  });
}

export async function listarSugestoesPosVendaGrowth(params: { empresaId: string | null; vertical?: AreaPosVendaApi }) {
  return apiClientAutenticado.get<SugestaoPosVendaGrowthApi[]>("/growth/pos-venda/ia-sugestoes", {
    query: {
      empresaId: params.empresaId,
      vertical: params.vertical
    }
  });
}

export async function validarSessaoAtual(): Promise<UsuarioLoginApi> {
  return apiClientAutenticado.get<UsuarioLoginApi>("/auth/me");
}
