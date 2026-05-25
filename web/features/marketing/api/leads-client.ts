import { apiClient } from "@/lib/api";

export type AreaInteresseLead =
  | "NUTRI_PRO"
  | "BEAUTY_PRO"
  | "BIOMED_PRO"
  | "FISIO_PRO"
  | "SPACES"
  | "OUTRA";

export type TamanhoOperacaoLead =
  | "PROFISSIONAL_SOLO"
  | "EQUIPE_PEQUENA"
  | "CLINICA"
  | "MULTIUNIDADE"
  | "ESTUDANTE";

export type RegistrarLeadMarketingRequest = {
  nome: string;
  email: string;
  telefone?: string;
  areaInteresse: AreaInteresseLead;
  tamanhoOperacao: TamanhoOperacaoLead;
  origem: string;
  mensagem?: string;
};

export type LeadMarketingResponse = {
  id: string;
  nome: string;
  email: string;
  telefone?: string;
  areaInteresse: AreaInteresseLead;
  tamanhoOperacao: TamanhoOperacaoLead;
  origem: string;
  mensagem?: string;
  status: "NOVO" | "EM_CONTATO" | "CONVERTIDO" | "DESCARTADO";
  criadoEm: string;
};

export function registrarLeadMarketing(dados: RegistrarLeadMarketingRequest) {
  return apiClient.post<LeadMarketingResponse>("/api/marketing/leads", dados);
}
