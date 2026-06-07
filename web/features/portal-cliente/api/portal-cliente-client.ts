import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type TipoAreaCliente =
  | "GERAL"
  | "NUTRI"
  | "BEAUTY"
  | "BIOMED"
  | "FISIO"
  | "SPACES"
  | "PSICO"
  | "FONO"
  | "FARMACIA_CLINICA"
  | "ODONTO"
  | "TERAPIAS_INTEGRATIVAS";

export type TipoCliente = "CLIENTE" | "PACIENTE" | "CLIENTE_PACIENTE";

export type TipoStatusAgenda = "AGENDADO" | "CONFIRMADO" | "REALIZADO" | "CANCELADO" | "FALTOU" | "REMARCADO";

export type TipoDocumentoPortal =
  | "DECLARACAO"
  | "RELATORIO"
  | "TERMO"
  | "ORIENTACAO"
  | "RECIBO"
  | "SOLICITACAO_EXAMES"
  | "PRESCRICAO"
  | "PLANO_ALIMENTAR"
  | "OUTRO";

export type StatusDocumentoPortal = "RASCUNHO" | "EMITIDO" | "CANCELADO";

export type ClientePortal = {
  id: string;
  empresaId: string;
  nome: string;
  tipo: TipoCliente;
  area: TipoAreaCliente;
  documento: string | null;
  email: string | null;
  telefone: string | null;
  dataNascimento: string | null;
  observacoes: string | null;
  ativo: boolean;
  criadoEm: string;
  atualizadoEm: string;
};

export type ClientesPortal = {
  itens: ClientePortal[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type CompromissoPortal = {
  id: string;
  empresaId: string;
  clientePacienteId: string | null;
  profissionalId: string | null;
  profissionalNome: string;
  sala: string | null;
  tipo: "ATENDIMENTO" | "RETORNO" | "AVALIACAO" | "BLOQUEIO" | "OUTRO";
  status: TipoStatusAgenda;
  inicio: string;
  fim: string;
  observacoes: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type AgendaPortal = {
  itens: CompromissoPortal[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type DocumentoPortal = {
  id: string;
  empresaId: string;
  clientePacienteId: string | null;
  profissionalId: string | null;
  profissionalNome: string;
  titulo: string;
  tipo: TipoDocumentoPortal;
  conteudo: string;
  status: StatusDocumentoPortal;
  versao: number;
  ativo: boolean;
  criadoEm: string;
  atualizadoEm: string;
};

export type DocumentosPortal = {
  itens: DocumentoPortal[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

const portalClienteApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function listarClientesPortal(params: { empresaId: string; busca?: string; area?: TipoAreaCliente; ativo?: boolean; tamanho?: number }) {
  return portalClienteApi.get<ClientesPortal>("/api/clientes-pacientes", {
    query: {
      empresaId: params.empresaId,
      busca: params.busca || undefined,
      area: params.area,
      ativo: params.ativo,
      tamanho: params.tamanho ?? 80
    }
  });
}

export function listarAgendaPortal(params: { empresaId: string; clientePacienteId?: string; inicio?: string; fim?: string; status?: TipoStatusAgenda }) {
  return portalClienteApi.get<AgendaPortal>("/api/agenda/compromissos", {
    query: {
      empresaId: params.empresaId,
      clientePacienteId: params.clientePacienteId,
      inicio: params.inicio,
      fim: params.fim,
      status: params.status,
      tamanho: 80
    }
  });
}

export function listarDocumentosPortal(params: { empresaId: string; clientePacienteId: string }) {
  return portalClienteApi.get<DocumentosPortal>("/api/documentos-profissionais", {
    query: {
      empresaId: params.empresaId,
      clientePacienteId: params.clientePacienteId,
      ativo: true,
      tamanho: 40
    }
  });
}
