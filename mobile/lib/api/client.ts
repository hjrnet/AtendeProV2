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
  refreshToken: string;
  tipoToken: string;
  expiraEm: string;
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

export const API_BASE_PADRAO = process.env.EXPO_PUBLIC_API_URL ?? "http://localhost:8080";

function montarBaseApi(url: string) {
  const base = url.replace(/\/+$/, "");
  if (base.endsWith("/api")) {
    return base;
  }

  return `${base}/api`;
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

    const url = new URL(path, baseUrl.endsWith("/") ? baseUrl : `${baseUrl}/`);
    Object.entries(options.query ?? {}).forEach(([chave, valor]) => {
      if (valor !== undefined && valor !== null) {
        url.searchParams.set(chave, String(valor));
      }
    });

    const resposta = await fetch(url, {
      ...options,
      method,
      body: prepararBody(options.body),
      headers
    });

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
  pagina?: number;
  tamanho?: number;
}) {
  return apiClientAutenticado.get<ResultadoPaginado<CompromissoAgendaApi>>("/agenda/compromissos", {
    query: {
      empresaId: params.empresaId,
      inicio: params.inicio,
      fim: params.fim,
      status: params.status,
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
