import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type StatusOperacionalNutriPro = "OPERACIONAL" | "CONFIGURACAO_PENDENTE";

export type IndicadorNutriPro = {
  codigo: string;
  titulo: string;
  valor: number;
  descricao: string;
  status: string;
};

export type AtalhoNutriPro = {
  codigo: string;
  titulo: string;
  descricao: string;
  status: string;
  destino: string;
};

export type PacienteNutriResumo = {
  id: string;
  nome: string;
  telefone: string | null;
  observacoes: string | null;
  ativo: boolean;
  atualizadoEm: string;
};

export type PacientesNutriPro = {
  itens: PacienteNutriResumo[];
};

export type PacienteProntuarioNutriPro = {
  id: string;
  empresaId: string;
  nome: string;
  email: string | null;
  telefone: string | null;
  dataNascimento: string | null;
  idade: number | null;
  observacoes: string | null;
  ativo: boolean;
  atualizadoEm: string;
};

export type ResumoProntuarioNutriPro = {
  documentos: number;
  consultasFuturas: number;
  simulacoesPrecificacao: number;
  planosAlimentaresAtivos: number;
  statusPlanoAlimentar: string;
  statusAnamnese: string;
  statusAvaliacaoAntropometrica: string;
  statusGastoEnergetico: string;
  statusExamesLaboratoriais: string;
  ultimaConsultaEm: string | null;
};

export type StatusAcaoProntuarioNutriPro = "DISPONIVEL" | "PREPARADO" | "PROXIMA_TASK";

export type AcaoProntuarioNutriPro = {
  codigo: string;
  titulo: string;
  descricao: string;
  status: StatusAcaoProntuarioNutriPro;
  statusRotulo: string;
  destaque: boolean;
};

export type ProntuarioNutriPro = {
  empresaId: string;
  paciente: PacienteProntuarioNutriPro;
  resumo: ResumoProntuarioNutriPro;
  acoesRapidas: AcaoProntuarioNutriPro[];
  atualizadoEm: string;
};

export type VisaoNutriPro = {
  empresaId: string;
  empresaNome: string;
  statusOperacional: StatusOperacionalNutriPro;
  statusOperacionalRotulo: string;
  mensagemStatus: string;
  indicadores: IndicadorNutriPro[];
  atalhosPrioritarios: AtalhoNutriPro[];
  proximasEvolucoes: AtalhoNutriPro[];
  pacientesRecentes: PacienteNutriResumo[];
  atualizadoEm: string;
};

const nutriProApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function consultarVisaoNutriPro(empresaId: string) {
  return nutriProApi.get<VisaoNutriPro>("/api/nutri-pro/visao", {
    query: { empresaId }
  });
}

export function listarPacientesNutriPro(params: { empresaId: string; busca?: string }) {
  return nutriProApi.get<PacientesNutriPro>("/api/nutri-pro/pacientes", {
    query: {
      empresaId: params.empresaId,
      busca: params.busca || undefined
    }
  });
}

export function consultarProntuarioNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<ProntuarioNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/prontuario`, {
    query: { empresaId: params.empresaId }
  });
}
