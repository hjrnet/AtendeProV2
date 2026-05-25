import { ApiError, type ApiErrorPayload } from "@/lib/api/api-error";

export type ApiRequestInterceptor = (request: Request) => Request | Promise<Request>;
export type ApiResponseInterceptor = (response: Response) => Response | Promise<Response>;

export type ApiClientConfig = {
  baseUrl?: string;
  getAccessToken?: () => string | null | Promise<string | null>;
  onUnauthorized?: () => void | Promise<void>;
  requestInterceptors?: ApiRequestInterceptor[];
  responseInterceptors?: ApiResponseInterceptor[];
};

export type ApiRequestOptions = Omit<RequestInit, "body"> & {
  body?: unknown;
  query?: Record<string, string | number | boolean | null | undefined>;
};

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export function criarApiClient(config: ApiClientConfig = {}) {
  const baseUrl = config.baseUrl ?? API_BASE_URL;

  async function request<TResponse>(path: string, options: ApiRequestOptions = {}) {
    const headers = new Headers(options.headers);
    const token = await config.getAccessToken?.();

    if (!headers.has("Accept")) {
      headers.set("Accept", "application/json");
    }

    if (options.body !== undefined && !headers.has("Content-Type")) {
      headers.set("Content-Type", "application/json");
    }

    if (token) {
      headers.set("Authorization", `Bearer ${token}`);
    }

    let request = new Request(criarUrl(baseUrl, path, options.query), {
      ...options,
      body: prepararBody(options.body),
      headers
    });

    for (const interceptor of config.requestInterceptors ?? []) {
      request = await interceptor(request);
    }

    let response = await fetch(request);

    for (const interceptor of config.responseInterceptors ?? []) {
      response = await interceptor(response);
    }

    if (response.status === 401) {
      await config.onUnauthorized?.();
    }

    if (!response.ok) {
      throw await criarApiError(response);
    }

    if (response.status === 204) {
      return undefined as TResponse;
    }

    return (await response.json()) as TResponse;
  }

  return {
    get: <TResponse>(path: string, options?: ApiRequestOptions) =>
      request<TResponse>(path, { ...options, method: "GET" }),
    post: <TResponse>(path: string, body?: unknown, options?: ApiRequestOptions) =>
      request<TResponse>(path, { ...options, method: "POST", body }),
    put: <TResponse>(path: string, body?: unknown, options?: ApiRequestOptions) =>
      request<TResponse>(path, { ...options, method: "PUT", body }),
    patch: <TResponse>(path: string, body?: unknown, options?: ApiRequestOptions) =>
      request<TResponse>(path, { ...options, method: "PATCH", body }),
    delete: <TResponse>(path: string, options?: ApiRequestOptions) =>
      request<TResponse>(path, { ...options, method: "DELETE" }),
    request
  };
}

export const apiClient = criarApiClient();

function criarUrl(baseUrl: string, path: string, query?: ApiRequestOptions["query"]) {
  const url = new URL(path, baseUrl.endsWith("/") ? baseUrl : `${baseUrl}/`);

  Object.entries(query ?? {}).forEach(([chave, valor]) => {
    if (valor !== undefined && valor !== null) {
      url.searchParams.set(chave, String(valor));
    }
  });

  return url;
}

function prepararBody(body: unknown) {
  if (body === undefined || body instanceof FormData || body instanceof Blob) {
    return body as BodyInit | undefined;
  }

  return JSON.stringify(body);
}

async function criarApiError(response: Response) {
  const payload = await lerPayloadErro(response);
  const mensagem = payload?.mensagem ?? `Erro HTTP ${response.status}`;

  return new ApiError(response.status, mensagem, payload);
}

async function lerPayloadErro(response: Response): Promise<ApiErrorPayload | undefined> {
  const contentType = response.headers.get("Content-Type") ?? "";

  if (!contentType.includes("application/json")) {
    return undefined;
  }

  try {
    return (await response.json()) as ApiErrorPayload;
  } catch {
    return undefined;
  }
}
