import { apiClient } from "@/lib/api";

export type LoginRequest = {
  email: string;
  senha: string;
};

export type UsuarioLogin = {
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
  usuario: UsuarioLogin;
};

export function autenticarUsuario(request: LoginRequest) {
  return apiClient.post<LoginResponse>("/api/auth/login", request);
}
