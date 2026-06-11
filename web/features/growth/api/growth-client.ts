import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type VerticalGrowth = "NUTRI" | "BEAUTY";
export type EtapaLeadGrowth = "NOVO" | "QUALIFICADO" | "DEMO_AGENDADA" | "CONVERTIDO" | "PERDIDO";
export type PerfilDemoGrowth = "NUTRI" | "BEAUTY" | "GESTOR" | "INVESTIDOR";

export type LeadGrowth = {
  id: string;
  empresaId: string;
  nome: string;
  email: string;
  telefone: string | null;
  vertical: VerticalGrowth;
  origem: string;
  etapa: EtapaLeadGrowth;
  potencialMensal: number;
  clientePacienteId: string | null;
  compromissoAgendaId: string | null;
  observacoes: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type SugestaoPosVendaIA = {
  clienteId: string;
  clienteNome: string;
  vertical: VerticalGrowth;
  tipo: string;
  prioridade: "1_ALTA" | "2_MEDIA" | "3_BAIXA";
  motivo: string;
  retornoRecomendadoEm: string;
  mensagemSugerida: string;
  oportunidadePacote: string;
};

export type IndicadorVerticalGrowth = {
  vertical: VerticalGrowth;
  clientesAtivos: number;
  agendaProximos30Dias: number;
  faturamentoPrevisto: number;
  ticketMedio: number;
  margemMediaPercentual: number;
  recorrenciaPercentual: number;
  clientesComRecompra: number;
  leituraExecutiva: string;
};

export type ApresentacaoDemoGrowth = {
  id: string;
  perfil: PerfilDemoGrowth;
  titulo: string;
  roteiro: string;
  metricasChave: string;
  chamadaParaAcao: string;
  atualizadoEm: string;
};

export type PainelGrowth = {
  empresaId: string;
  leads: LeadGrowth[];
  sugestoesPosVenda: SugestaoPosVendaIA[];
  indicadores: IndicadorVerticalGrowth[];
  apresentacoesDemo: ApresentacaoDemoGrowth[];
  atualizadoEm: string;
};

export type RegistrarLeadGrowthInput = {
  empresaId: string;
  nome: string;
  email: string;
  telefone?: string | null;
  vertical: VerticalGrowth;
  origem: string;
  etapa?: EtapaLeadGrowth;
  potencialMensal?: number;
  clientePacienteId?: string | null;
  compromissoAgendaId?: string | null;
  observacoes?: string | null;
};

const growthApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function consultarPainelGrowth(empresaId: string) {
  return growthApi.get<PainelGrowth>("/api/growth/painel", { query: { empresaId } });
}

export function listarLeadsGrowth(params: { empresaId: string; vertical?: VerticalGrowth; etapa?: EtapaLeadGrowth; busca?: string }) {
  return growthApi.get<LeadGrowth[]>("/api/growth/leads", {
    query: {
      empresaId: params.empresaId,
      vertical: params.vertical,
      etapa: params.etapa,
      busca: params.busca || undefined
    }
  });
}

export function registrarLeadGrowth(dados: RegistrarLeadGrowthInput) {
  return growthApi.post<LeadGrowth>("/api/growth/leads", dados);
}

export function atualizarEtapaLeadGrowth(params: { empresaId: string; leadId: string; etapa: EtapaLeadGrowth }) {
  return growthApi.patch<LeadGrowth>(`/api/growth/leads/${params.leadId}/etapa`, {
    empresaId: params.empresaId,
    etapa: params.etapa
  });
}

export function atualizarVinculosLeadGrowth(params: { empresaId: string; leadId: string; clientePacienteId?: string | null; compromissoAgendaId?: string | null }) {
  return growthApi.patch<LeadGrowth>(`/api/growth/leads/${params.leadId}/vinculos`, {
    empresaId: params.empresaId,
    clientePacienteId: params.clientePacienteId ?? null,
    compromissoAgendaId: params.compromissoAgendaId ?? null
  });
}

export function listarSugestoesPosVendaGrowth(params: { empresaId: string; vertical?: VerticalGrowth }) {
  return growthApi.get<SugestaoPosVendaIA[]>("/api/growth/pos-venda/ia-sugestoes", {
    query: { empresaId: params.empresaId, vertical: params.vertical }
  });
}

export function listarIndicadoresVerticaisGrowth(empresaId: string) {
  return growthApi.get<IndicadorVerticalGrowth[]>("/api/growth/indicadores-verticais", { query: { empresaId } });
}

export function listarApresentacoesDemoGrowth(params: { empresaId: string; perfil?: PerfilDemoGrowth }) {
  return growthApi.get<ApresentacaoDemoGrowth[]>("/api/growth/apresentacoes-demo", {
    query: { empresaId: params.empresaId, perfil: params.perfil }
  });
}
