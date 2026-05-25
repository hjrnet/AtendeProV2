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

export type SexoBiologicoNutriPro = "FEMININO" | "MASCULINO" | "NAO_INFORMADO";

export type ObjetivoNutricionalNutriPro = "PERDA_DE_PESO" | "GANHO_DE_MASSA" | "MANUTENCAO" | "PERFORMANCE" | "SAUDE";

export type AvaliacaoAntropometricaNutriPro = {
  id: string;
  empresaId: string;
  pacienteId: string;
  pesoKg: number;
  alturaCm: number;
  idade: number;
  sexo: SexoBiologicoNutriPro;
  sexoRotulo: string;
  imc: number;
  objetivo: ObjetivoNutricionalNutriPro;
  objetivoRotulo: string;
  fatorAtividade: number;
  gebKcal: number;
  tmbKcal: number;
  getKcal: number;
  metaEnergeticaKcal: number;
  formula: string;
  aviso: string;
  observacoes: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type AvaliacoesAntropometricasNutriPro = {
  itens: AvaliacaoAntropometricaNutriPro[];
};

export type CriarAvaliacaoAntropometricaNutriProInput = {
  pesoKg: number;
  alturaCm: number;
  idade: number;
  sexo: SexoBiologicoNutriPro;
  objetivo: ObjetivoNutricionalNutriPro;
  fatorAtividade: number;
  observacoes?: string | null;
};

export type StatusPlanoAlimentarNutriPro = "RASCUNHO" | "ATIVO" | "SUBSTITUIDO" | "ARQUIVADO";

export type TipoItemPlanoAlimentarNutriPro = "ALIMENTO" | "SUPLEMENTO";

export type ItemPlanoAlimentarNutriPro = {
  id: string;
  tipoItem: TipoItemPlanoAlimentarNutriPro;
  tipoItemRotulo: string;
  nome: string;
  grupo: string | null;
  unidadeMedida: string;
  quantidade: number;
  quantidadeBase: number;
  energiaKcal: number;
  proteinas: number;
  carboidratos: number;
  lipidios: number;
  observacoes: string | null;
  ordenacao: number;
};

export type RefeicaoPlanoAlimentarNutriPro = {
  id: string;
  nome: string;
  horario: string | null;
  observacoes: string | null;
  ordenacao: number;
  itens: ItemPlanoAlimentarNutriPro[];
  energiaTotalKcal: number;
  proteinasTotal: number;
  carboidratosTotal: number;
  lipidiosTotal: number;
};

export type PlanoAlimentarNutriPro = {
  id: string;
  empresaId: string;
  pacienteId: string;
  objetivo: string;
  descricao: string | null;
  status: StatusPlanoAlimentarNutriPro;
  statusRotulo: string;
  refeicoes: RefeicaoPlanoAlimentarNutriPro[];
  energiaTotalKcal: number;
  proteinasTotal: number;
  carboidratosTotal: number;
  lipidiosTotal: number;
  criadoEm: string;
  atualizadoEm: string;
};

export type PlanosAlimentaresNutriPro = {
  itens: PlanoAlimentarNutriPro[];
};

export type CriarItemPlanoAlimentarNutriProInput = {
  tipoItem: TipoItemPlanoAlimentarNutriPro;
  nome: string;
  grupo?: string | null;
  unidadeMedida: string;
  quantidadeBase: number;
  quantidade: number;
  energiaKcalBase: number;
  proteinasBase: number;
  carboidratosBase: number;
  lipidiosBase: number;
  observacoes?: string | null;
  ordenacao: number;
};

export type CriarRefeicaoPlanoAlimentarNutriProInput = {
  nome: string;
  horario?: string | null;
  observacoes?: string | null;
  ordenacao: number;
  itens: CriarItemPlanoAlimentarNutriProInput[];
};

export type CriarPlanoAlimentarNutriProInput = {
  objetivo: string;
  descricao?: string | null;
  status?: StatusPlanoAlimentarNutriPro;
  refeicoes: CriarRefeicaoPlanoAlimentarNutriProInput[];
};

export type TipoDocumentoProfissionalNutriPro =
  | "DECLARACAO"
  | "RELATORIO"
  | "TERMO"
  | "ORIENTACAO"
  | "RECIBO"
  | "SOLICITACAO_EXAMES"
  | "PRESCRICAO"
  | "PLANO_ALIMENTAR"
  | "OUTRO";

export type StatusDocumentoProfissionalNutriPro = "RASCUNHO" | "EMITIDO" | "CANCELADO";

export type DocumentoProfissionalNutriPro = {
  id: string;
  empresaId: string;
  clientePacienteId: string | null;
  profissionalId: string | null;
  profissionalNome: string;
  titulo: string;
  tipo: TipoDocumentoProfissionalNutriPro;
  conteudo: string;
  status: StatusDocumentoProfissionalNutriPro;
  versao: number;
  codigoValidacao: string;
  caminhoValidacao: string;
  validacaoPublicaAtiva: boolean;
  ativo: boolean;
  criadoEm: string;
  atualizadoEm: string;
};

export type DocumentosProfissionaisNutriPro = {
  itens: DocumentoProfissionalNutriPro[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type CriarDocumentoProfissionalNutriProInput = {
  empresaId: string;
  clientePacienteId: string;
  profissionalId?: string | null;
  profissionalNome: string;
  titulo: string;
  tipo: TipoDocumentoProfissionalNutriPro;
  conteudo: string;
  status?: StatusDocumentoProfissionalNutriPro;
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

export function listarAvaliacoesAntropometricasNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<AvaliacoesAntropometricasNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/avaliacoes-antropometricas`, {
    query: { empresaId: params.empresaId }
  });
}

export function criarAvaliacaoAntropometricaNutriPro(params: {
  empresaId: string;
  pacienteId: string;
  dados: CriarAvaliacaoAntropometricaNutriProInput;
}) {
  return nutriProApi.post<AvaliacaoAntropometricaNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/avaliacoes-antropometricas`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function listarPlanosAlimentaresNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<PlanosAlimentaresNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares`, {
    query: { empresaId: params.empresaId }
  });
}

export function detalharPlanoAlimentarNutriPro(params: { empresaId: string; pacienteId: string; planoId: string }) {
  return nutriProApi.get<PlanoAlimentarNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}`, {
    query: { empresaId: params.empresaId }
  });
}

export function criarPlanoAlimentarNutriPro(params: {
  empresaId: string;
  pacienteId: string;
  dados: CriarPlanoAlimentarNutriProInput;
}) {
  return nutriProApi.post<PlanoAlimentarNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function listarDocumentosProfissionaisNutriPro(params: {
  empresaId: string;
  pacienteId: string;
  tipo?: TipoDocumentoProfissionalNutriPro;
}) {
  return nutriProApi.get<DocumentosProfissionaisNutriPro>("/api/documentos-profissionais", {
    query: {
      empresaId: params.empresaId,
      clientePacienteId: params.pacienteId,
      tipo: params.tipo,
      ativo: true,
      tamanho: 20
    }
  });
}

export function criarDocumentoProfissionalNutriPro(dados: CriarDocumentoProfissionalNutriProInput) {
  return nutriProApi.post<DocumentoProfissionalNutriPro>("/api/documentos-profissionais", dados);
}

export function caminhoPdfDocumentoNutriPro(documentoId: string) {
  const baseUrl = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";
  return new URL(`/api/documentos-profissionais/${documentoId}/pdf`, baseUrl).toString();
}
