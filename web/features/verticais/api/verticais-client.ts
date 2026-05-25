import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type CodigoVerticalProfissional = "NUTRI_PRO" | "BEAUTY_PRO" | "BIOMED_PRO" | "FISIO_PRO" | "PSICO_PRO";

export type StatusVerticalProfissional = "OPERACIONAL_BASE" | "PREPARADO_FUTURO";

export type VerticalProfissional = {
  codigo: CodigoVerticalProfissional;
  nome: string;
  release: string;
  status: StatusVerticalProfissional;
  conselhoProfissional: string | null;
  resumo: string;
  publicosAtendidos: string[];
  capacidades: string[];
  entidades: string[];
  documentos: string[];
  integracoesNucleo: string[];
  proximasEvolucoes: string[];
};

export type VerticaisProfissionais = {
  itens: VerticalProfissional[];
};

const verticaisApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function listarVerticaisProfissionais() {
  return verticaisApi.get<VerticaisProfissionais>("/api/verticais-profissionais");
}
