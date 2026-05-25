export type ApiErrorPayload = {
  codigo?: string;
  mensagem?: string;
  path?: string;
  timestamp?: string;
  campos?: Array<{
    campo: string;
    mensagem: string;
  }>;
};

export class ApiError extends Error {
  readonly status: number;
  readonly payload?: ApiErrorPayload;

  constructor(status: number, mensagem: string, payload?: ApiErrorPayload) {
    super(mensagem);
    this.name = "ApiError";
    this.status = status;
    this.payload = payload;
  }

  get codigo() {
    return this.payload?.codigo;
  }

  get campos() {
    return this.payload?.campos ?? [];
  }
}
