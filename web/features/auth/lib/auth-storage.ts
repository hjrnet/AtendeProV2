import type { LoginResponse, UsuarioLogin } from "@/features/auth/api/auth-client";

const CHAVE_SESSAO = "atendepro.auth.session.v1";
let sessaoEmMemoria: SessaoAutenticada | null = null;

export type SessaoAutenticada = {
  accessToken: string;
  refreshToken: string;
  tipoToken: string;
  expiraEm: string;
  usuario: UsuarioLogin;
};

export function salvarSessaoAutenticada(response: LoginResponse) {
  const sessao: SessaoAutenticada = {
    accessToken: response.accessToken,
    refreshToken: response.refreshToken,
    tipoToken: response.tipoToken,
    expiraEm: response.expiraEm,
    usuario: response.usuario
  };

  const storage = obterSessionStorage();
  if (storage) {
    storage.setItem(CHAVE_SESSAO, JSON.stringify(sessao));
  }
  sessaoEmMemoria = sessao;

  return sessao;
}

export function carregarSessaoAutenticada() {
  const storage = obterSessionStorage();
  if (!storage) {
    return sessaoEmMemoria;
  }

  const valor = storage.getItem(CHAVE_SESSAO);
  if (!valor) {
    return sessaoEmMemoria;
  }

  try {
    sessaoEmMemoria = JSON.parse(valor) as SessaoAutenticada;
    return sessaoEmMemoria;
  } catch {
    storage.removeItem(CHAVE_SESSAO);
    sessaoEmMemoria = null;
    return null;
  }
}

export function limparSessaoAutenticada() {
  sessaoEmMemoria = null;
  const storage = obterSessionStorage();
  if (storage) {
    storage.removeItem(CHAVE_SESSAO);
  }
}

function obterSessionStorage() {
  if (typeof window === "undefined" || !("sessionStorage" in window)) {
    return null;
  }

  try {
    return window.sessionStorage;
  } catch {
    return null;
  }
}
