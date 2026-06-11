import { Platform } from "react-native";
import * as SecureStore from "expo-secure-store";

import { autenticarUsuario, type LoginRequest, type LoginResponse } from "@/lib/api/client";

const CHAVE_SESSAO = "atendepro.mobile.session.v1";

type SessaoPersistida = {
  accessToken: string;
  refreshToken: string;
  tipoToken: string;
  expiraEm: string | null;
  usuario: LoginResponse["usuario"];
};

let cacheSessao: SessaoPersistida | null = null;

export type SessaoAutenticada = SessaoPersistida;

export function acessarAplicacao(email: string) {
  if (!email || !email.includes("@")) {
    return "usuário";
  }

  const trecho = email.split("@")[0];
  const nome = trecho.replace(/\./g, " ").replace(/^\w/, (valor) => valor.toUpperCase());

  return nome;
}

export async function autenticarSessao(dadosLogin: LoginRequest) {
  return autenticarUsuario(dadosLogin);
}

export async function salvarSessaoAutenticada(response: LoginResponse) {
  const sessao: SessaoPersistida = {
    accessToken: response.accessToken,
    refreshToken: response.refreshToken ?? "",
    tipoToken: response.tipoToken ?? "Bearer",
    expiraEm: response.expiraEm ?? null,
    usuario: response.usuario
  };

  cacheSessao = sessao;

  await salvarSessaoSegura(sessao);
  return sessao;
}

export async function carregarSessaoAutenticada() {
  if (cacheSessao && sessaoAindaValida(cacheSessao)) {
    return cacheSessao;
  }

  const sessaoArmazenada = await carregarSessaoSegura();
  if (sessaoArmazenada && sessaoAindaValida(sessaoArmazenada)) {
    cacheSessao = sessaoArmazenada;
  } else if (sessaoArmazenada) {
    await limparSessaoAutenticada();
  }

  return cacheSessao;
}

export async function obterSessaoAutenticada() {
  return carregarSessaoAutenticada();
}

export async function exigirSessaoAutenticada() {
  const sessao = await carregarSessaoAutenticada();
  if (!sessao) {
    throw new Error("Faça login novamente para carregar dados reais do AtendePro.");
  }

  return sessao;
}

export async function limparSessaoAutenticada() {
  cacheSessao = null;
  if (Platform.OS === "web") {
    if (typeof window !== "undefined" && "localStorage" in window) {
      window.localStorage.removeItem(CHAVE_SESSAO);
      return;
    }

    return;
  }

  await SecureStore.deleteItemAsync(CHAVE_SESSAO);
}

export function nomeiaUsuario(email: string) {
  return acessarAplicacao(email);
}

async function salvarSessaoSegura(sessao: SessaoPersistida) {
  const payload = JSON.stringify(sessao);

  if (Platform.OS === "web") {
    if (typeof window === "undefined" || !("localStorage" in window)) {
      return;
    }

    window.localStorage.setItem(CHAVE_SESSAO, payload);
    return;
  }

  await SecureStore.setItemAsync(CHAVE_SESSAO, payload);
}

async function carregarSessaoSegura(): Promise<SessaoPersistida | null> {
  try {
    if (Platform.OS === "web") {
      if (typeof window === "undefined" || !("localStorage" in window)) {
        return null;
      }

      const valor = window.localStorage.getItem(CHAVE_SESSAO);
      return parseSessao(valor);
    }

    const valor = await SecureStore.getItemAsync(CHAVE_SESSAO);
    return parseSessao(valor);
  } catch {
    return null;
  }
}

function parseSessao(valor: string | null) {
  if (!valor) {
    return null;
  }

  try {
    return JSON.parse(valor) as SessaoPersistida;
  } catch {
    return null;
  }
}

function sessaoAindaValida(sessao: SessaoPersistida) {
  if (!sessao.accessToken || !sessao.usuario?.id) {
    return false;
  }

  if (!sessao.expiraEm) {
    return true;
  }

  const expiraEm = new Date(sessao.expiraEm).getTime();
  if (Number.isNaN(expiraEm)) {
    return true;
  }

  return expiraEm > Date.now() + 30_000;
}
