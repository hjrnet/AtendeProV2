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

export type ItemListaComprasNutriPro = {
  nome: string;
  categoria: string;
  quantidade: number;
  unidadeMedida: string;
  refeicoes: string | null;
  observacoes: string | null;
};

export type GrupoListaComprasNutriPro = {
  categoria: string;
  itens: ItemListaComprasNutriPro[];
};

export type ListaComprasNutriPro = {
  empresaId: string;
  pacienteId: string;
  planoId: string;
  objetivoPlano: string;
  grupos: GrupoListaComprasNutriPro[];
  geradoEm: string;
};

export type SubstituicaoAlimentarNutriPro = {
  id: string;
  empresaId: string;
  pacienteId: string;
  planoId: string;
  refeicaoId: string | null;
  alimentoOrigem: string;
  alimentoSubstituto: string;
  grupo: string | null;
  objetivo: string | null;
  restricaoAlimentar: string | null;
  quantidadeEquivalente: number;
  unidadeMedida: string;
  observacoes: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type SubstituicoesAlimentaresNutriPro = {
  itens: SubstituicaoAlimentarNutriPro[];
};

export type CriarSubstituicaoAlimentarNutriProInput = {
  refeicaoId?: string | null;
  alimentoOrigem: string;
  alimentoSubstituto: string;
  grupo?: string | null;
  objetivo?: string | null;
  restricaoAlimentar?: string | null;
  quantidadeEquivalente: number;
  unidadeMedida: string;
  observacoes?: string | null;
};

export type MaterialEducativoNutriPro = {
  id: string;
  empresaId: string;
  pacienteId: string;
  planoId: string;
  tipo: string;
  titulo: string;
  objetivo: string | null;
  conteudo: string;
  linkAnexo: string | null;
  observacoes: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type MateriaisEducativosNutriPro = {
  itens: MaterialEducativoNutriPro[];
};

export type CriarMaterialEducativoNutriProInput = {
  tipo: string;
  titulo: string;
  objetivo?: string | null;
  conteudo: string;
  linkAnexo?: string | null;
  observacoes?: string | null;
};

export type ExameAvancadoNutriPro = {
  id: string;
  empresaId: string;
  pacienteId: string;
  tipo: string;
  nome: string;
  valor: number;
  unidadeMedida: string;
  dataExame: string;
  status: string;
  observacoes: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type ExamesAvancadosNutriPro = {
  itens: ExameAvancadoNutriPro[];
};

export type CriarExameAvancadoNutriProInput = {
  tipo: string;
  nome: string;
  valor: number;
  unidadeMedida: string;
  dataExame?: string | null;
  status?: string | null;
  observacoes?: string | null;
};

export type IndicadorGerencialNutriPro = {
  nome: string;
  valor: number;
  unidade: string;
};

export type PerfilCarteiraNutriPro = {
  segmento: string;
  total: number;
};

export type RelatorioGerencialNutriPro = {
  empresaId: string;
  geradoEm: string;
  indicadores: IndicadorGerencialNutriPro[];
  perfilCarteira: PerfilCarteiraNutriPro[];
};

export type RegistroDiarioNutriPro = {
  id: string;
  empresaId: string;
  pacienteId: string;
  planoId: string | null;
  refeicaoNome: string | null;
  texto: string;
  evidenciaUrl: string | null;
  statusRevisao: "PENDENTE" | "REVISADO";
  parecerProfissional: string | null;
  criadoPor: "PACIENTE" | "PROFISSIONAL" | "SISTEMA";
  registradoEm: string;
  atualizadoEm: string;
};

export type RegistrosDiarioNutriPro = {
  itens: RegistroDiarioNutriPro[];
};

export type CriarRegistroDiarioNutriProInput = {
  refeicaoNome?: string | null;
  texto: string;
  evidenciaUrl?: string | null;
};

export type RevisarRegistroDiarioNutriProInput = {
  parecerProfissional: string;
};

export type MetaNutriPro = {
  id: string;
  empresaId: string;
  pacienteId: string;
  tipo: string;
  descricao: string;
  valorMeta: number;
  unidade: string | null;
  dataInicio: string;
  dataAlvo: string | null;
  status: "ATIVA" | "CONCLUIDA" | "PAUSADA";
  criadoEm: string;
  atualizadoEm: string;
};

export type MetasNutriPro = {
  itens: MetaNutriPro[];
};

export type CriarMetaNutriProInput = {
  tipo: string;
  descricao: string;
  valorMeta?: number;
  unidade?: string | null;
  dataAlvo?: string | null;
};

export type LembreteNutriPro = {
  id: string;
  empresaId: string;
  pacienteId: string;
  titulo: string;
  descricao: string | null;
  horario: string | null;
  frequencia: string;
  status: "ATIVO" | "PAUSADO";
  criadoEm: string;
  atualizadoEm: string;
};

export type LembretesNutriPro = {
  itens: LembreteNutriPro[];
};

export type CriarLembreteNutriProInput = {
  titulo: string;
  descricao?: string | null;
  horario?: string | null;
  frequencia: string;
};

export type MensagemNutriPro = {
  id: string;
  empresaId: string;
  pacienteId: string;
  remetenteTipo: "PACIENTE" | "PROFISSIONAL" | "SISTEMA";
  remetenteNome: string;
  texto: string;
  contexto: string | null;
  lidaPeloPaciente: boolean;
  lidaPeloProfissional: boolean;
  enviadaEm: string;
};

export type MensagensNutriPro = {
  itens: MensagemNutriPro[];
};

export type EnviarMensagemNutriProInput = {
  remetenteTipo: "PACIENTE" | "PROFISSIONAL" | "SISTEMA";
  remetenteNome: string;
  texto: string;
  contexto?: string | null;
};

export type EvolucaoNutriPro = {
  tipo: string;
  titulo: string;
  descricao: string;
  status: string;
  data: string;
};

export type EvolucoesNutriPro = {
  itens: EvolucaoNutriPro[];
};

export type TipoItemBancoAlimentosNutriPro = "ALIMENTO" | "SUPLEMENTO";

export type OrigemItemBancoAlimentosNutriPro = "PADRAO" | "PERSONALIZADO";

export type MetricasBancoAlimentosNutriPro = {
  totalItens: number;
  alimentos: number;
  suplementos: number;
  padrao: number;
  personalizados: number;
};

export type ItemBancoAlimentosNutriPro = {
  id: string;
  empresaId: string | null;
  tipoItem: TipoItemBancoAlimentosNutriPro;
  tipoItemRotulo: string;
  origem: OrigemItemBancoAlimentosNutriPro;
  origemRotulo: string;
  nome: string;
  grupo: string | null;
  categoriaClinica: string | null;
  unidadeMedida: string;
  quantidadeBase: number;
  energiaKcalBase: number;
  proteinasBase: number;
  carboidratosBase: number;
  lipidiosBase: number;
  fibrasBase: number;
  sodioMgBase: number;
  fonteDados: string | null;
  marcaFabricante: string | null;
  orientacaoUso: string | null;
  observacoes: string | null;
  ativo: boolean;
  criadoEm: string;
  atualizadoEm: string;
};

export type BancoAlimentosNutriPro = {
  empresaId: string;
  metricas: MetricasBancoAlimentosNutriPro;
  itens: ItemBancoAlimentosNutriPro[];
  atualizadoEm: string;
};

export type CadastrarItemBancoAlimentosNutriProInput = {
  tipoItem: TipoItemBancoAlimentosNutriPro;
  origem?: OrigemItemBancoAlimentosNutriPro;
  nome: string;
  grupo?: string | null;
  categoriaClinica?: string | null;
  unidadeMedida: string;
  quantidadeBase: number;
  energiaKcalBase: number;
  proteinasBase: number;
  carboidratosBase: number;
  lipidiosBase: number;
  fibrasBase?: number;
  sodioMgBase?: number;
  fonteDados?: string | null;
  marcaFabricante?: string | null;
  orientacaoUso?: string | null;
  observacoes?: string | null;
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

export type ReorganizarRefeicoesPlanoAlimentarNutriProInput = {
  refeicaoIds: string[];
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

export type StatusAgendaNutriPro = "AGENDADO" | "CONFIRMADO" | "REALIZADO" | "CANCELADO" | "FALTOU" | "REMARCADO";

export type TipoAgendaNutriPro = "ATENDIMENTO" | "RETORNO" | "AVALIACAO" | "BLOQUEIO" | "OUTRO";

export type CompromissoAgendaNutriPro = {
  id: string;
  empresaId: string;
  clientePacienteId: string | null;
  profissionalId: string | null;
  profissionalNome: string;
  sala: string | null;
  tipo: TipoAgendaNutriPro;
  status: StatusAgendaNutriPro;
  inicio: string;
  fim: string;
  observacoes: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type AgendaNutriPro = {
  itens: CompromissoAgendaNutriPro[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type ClientePacienteNutriPro = {
  id: string;
  empresaId: string;
  nome: string;
  tipo: "CLIENTE" | "PACIENTE" | "CLIENTE_PACIENTE";
  area: "GERAL" | "NUTRI" | "BEAUTY" | "BIOMED" | "FISIO" | "SPACES" | "PSICO" | "FONO" | "FARMACIA_CLINICA" | "ODONTO" | "TERAPIAS_INTEGRATIVAS";
  documento: string | null;
  email: string | null;
  telefone: string | null;
  dataNascimento: string | null;
  observacoes: string | null;
  ativo: boolean;
  criadoEm: string;
  atualizadoEm: string;
};

export type CadastrarPacienteNutriProInput = {
  empresaId: string;
  nome: string;
  telefone?: string | null;
  email?: string | null;
  dataNascimento?: string | null;
  observacoes?: string | null;
};

export type CriarCompromissoAgendaNutriProInput = {
  empresaId: string;
  clientePacienteId?: string | null;
  profissionalId?: string | null;
  profissionalNome?: string | null;
  sala?: string | null;
  tipo: TipoAgendaNutriPro;
  inicio: string;
  fim: string;
  observacoes?: string | null;
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

export function cadastrarPacienteNutriPro(dados: CadastrarPacienteNutriProInput) {
  return nutriProApi.post<ClientePacienteNutriPro>("/api/clientes-pacientes", {
    empresaId: dados.empresaId,
    nome: dados.nome,
    tipo: "PACIENTE",
    area: "NUTRI",
    email: dados.email || null,
    telefone: dados.telefone || null,
    dataNascimento: dados.dataNascimento || null,
    observacoes: dados.observacoes || null
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

export function publicarPlanoAlimentarNutriPro(params: { empresaId: string; pacienteId: string; planoId: string }) {
  return nutriProApi.post<PlanoAlimentarNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/publicar`,
    undefined,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function substituirPlanoAlimentarNutriPro(params: { empresaId: string; pacienteId: string; planoId: string }) {
  return nutriProApi.post<PlanoAlimentarNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/substituir`,
    undefined,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function duplicarPlanoAlimentarNutriPro(params: { empresaId: string; pacienteId: string; planoId: string }) {
  return nutriProApi.post<PlanoAlimentarNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/duplicar`,
    undefined,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function versionarPlanoAlimentarNutriPro(params: { empresaId: string; pacienteId: string; planoId: string }) {
  return nutriProApi.post<PlanoAlimentarNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/versionar`,
    undefined,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function salvarModeloPlanoAlimentarNutriPro(params: { empresaId: string; pacienteId: string; planoId: string }) {
  return nutriProApi.post<PlanoAlimentarNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/salvar-modelo`,
    undefined,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function arquivarPlanoAlimentarNutriPro(params: { empresaId: string; pacienteId: string; planoId: string }) {
  return nutriProApi.post<PlanoAlimentarNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/arquivar`,
    undefined,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function reorganizarRefeicoesPlanoAlimentarNutriPro(params: {
  empresaId: string;
  pacienteId: string;
  planoId: string;
  dados: ReorganizarRefeicoesPlanoAlimentarNutriProInput;
}) {
  return nutriProApi.patch<void>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/reordenar-refeicoes`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function consultarPlanoPublicadoNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<PlanoAlimentarNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/plano-publicado`, {
    query: { empresaId: params.empresaId }
  });
}

export function consultarListaComprasNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<ListaComprasNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/lista-compras`, {
    query: { empresaId: params.empresaId }
  });
}

export function listarSubstituicoesAlimentaresNutriPro(params: { empresaId: string; pacienteId: string; planoId: string }) {
  return nutriProApi.get<SubstituicoesAlimentaresNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/substituicoes-alimentares`,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function criarSubstituicaoAlimentarNutriPro(params: {
  empresaId: string;
  pacienteId: string;
  planoId: string;
  dados: CriarSubstituicaoAlimentarNutriProInput;
}) {
  return nutriProApi.post<SubstituicaoAlimentarNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/substituicoes-alimentares`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function listarMateriaisEducativosNutriPro(params: { empresaId: string; pacienteId: string; planoId: string }) {
  return nutriProApi.get<MateriaisEducativosNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/materiais-educativos`,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function criarMaterialEducativoNutriPro(params: {
  empresaId: string;
  pacienteId: string;
  planoId: string;
  dados: CriarMaterialEducativoNutriProInput;
}) {
  return nutriProApi.post<MaterialEducativoNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/planos-alimentares/${params.planoId}/materiais-educativos`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function listarExamesAvancadosNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<ExamesAvancadosNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/exames-avancados`, {
    query: { empresaId: params.empresaId }
  });
}

export function criarExameAvancadoNutriPro(params: {
  empresaId: string;
  pacienteId: string;
  dados: CriarExameAvancadoNutriProInput;
}) {
  return nutriProApi.post<ExameAvancadoNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/exames-avancados`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function consultarRelatorioGerencialNutriPro(params: { empresaId: string }) {
  return nutriProApi.get<RelatorioGerencialNutriPro>("/api/nutri-pro/relatorios/gerencial", {
    query: { empresaId: params.empresaId }
  });
}

export function listarDiarioAlimentarNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<RegistrosDiarioNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/diario-alimentar`, {
    query: { empresaId: params.empresaId }
  });
}

export function criarRegistroDiarioNutriPro(params: {
  empresaId: string;
  pacienteId: string;
  dados: CriarRegistroDiarioNutriProInput;
}) {
  return nutriProApi.post<RegistroDiarioNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/diario-alimentar`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function revisarRegistroDiarioNutriPro(params: {
  empresaId: string;
  pacienteId: string;
  registroId: string;
  dados: RevisarRegistroDiarioNutriProInput;
}) {
  return nutriProApi.post<RegistroDiarioNutriPro>(
    `/api/nutri-pro/pacientes/${params.pacienteId}/diario-alimentar/${params.registroId}/revisar`,
    params.dados,
    {
      query: { empresaId: params.empresaId }
    }
  );
}

export function listarMetasNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<MetasNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/metas`, {
    query: { empresaId: params.empresaId }
  });
}

export function criarMetaNutriPro(params: { empresaId: string; pacienteId: string; dados: CriarMetaNutriProInput }) {
  return nutriProApi.post<MetaNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/metas`, params.dados, {
    query: { empresaId: params.empresaId }
  });
}

export function listarLembretesNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<LembretesNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/lembretes`, {
    query: { empresaId: params.empresaId }
  });
}

export function criarLembreteNutriPro(params: { empresaId: string; pacienteId: string; dados: CriarLembreteNutriProInput }) {
  return nutriProApi.post<LembreteNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/lembretes`, params.dados, {
    query: { empresaId: params.empresaId }
  });
}

export function listarMensagensNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<MensagensNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/mensagens`, {
    query: { empresaId: params.empresaId }
  });
}

export function enviarMensagemNutriPro(params: { empresaId: string; pacienteId: string; dados: EnviarMensagemNutriProInput }) {
  return nutriProApi.post<MensagemNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/mensagens`, params.dados, {
    query: { empresaId: params.empresaId }
  });
}

export function marcarMensagensNutriProLidas(params: { empresaId: string; pacienteId: string; leitor: "PACIENTE" | "PROFISSIONAL" }) {
  return nutriProApi.patch<void>(`/api/nutri-pro/pacientes/${params.pacienteId}/mensagens/lidas`, undefined, {
    query: { empresaId: params.empresaId, leitor: params.leitor }
  });
}

export function listarEvolucaoNutriPro(params: { empresaId: string; pacienteId: string }) {
  return nutriProApi.get<EvolucoesNutriPro>(`/api/nutri-pro/pacientes/${params.pacienteId}/evolucao`, {
    query: { empresaId: params.empresaId }
  });
}

export function consultarBancoAlimentosNutriPro(params: {
  empresaId: string;
  busca?: string;
  tipoItem?: TipoItemBancoAlimentosNutriPro;
  origem?: OrigemItemBancoAlimentosNutriPro;
  ativo?: boolean;
}) {
  return nutriProApi.get<BancoAlimentosNutriPro>("/api/nutri-pro/banco-alimentos", {
    query: {
      empresaId: params.empresaId,
      busca: params.busca || undefined,
      tipoItem: params.tipoItem,
      origem: params.origem,
      ativo: params.ativo ?? true
    }
  });
}

export function cadastrarItemBancoAlimentosNutriPro(params: {
  empresaId: string;
  dados: CadastrarItemBancoAlimentosNutriProInput;
}) {
  return nutriProApi.post<ItemBancoAlimentosNutriPro>("/api/nutri-pro/banco-alimentos", params.dados, {
    query: { empresaId: params.empresaId }
  });
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

export function listarAgendaNutriPro(params: {
  empresaId: string;
  inicio?: string;
  fim?: string;
  status?: StatusAgendaNutriPro;
}) {
  return nutriProApi.get<AgendaNutriPro>("/api/agenda/compromissos", {
    query: {
      empresaId: params.empresaId,
      inicio: params.inicio,
      fim: params.fim,
      status: params.status,
      tamanho: 30
    }
  });
}

export function criarCompromissoAgendaNutriPro(dados: CriarCompromissoAgendaNutriProInput) {
  return nutriProApi.post<CompromissoAgendaNutriPro>("/api/agenda/compromissos", dados);
}

export function caminhoPdfDocumentoNutriPro(documentoId: string) {
  const baseUrl = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";
  return new URL(`/api/documentos-profissionais/${documentoId}/pdf`, baseUrl).toString();
}
