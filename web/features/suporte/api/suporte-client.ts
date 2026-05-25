import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type StatusChamadoSuporte =
  | "ABERTO"
  | "EM_ATENDIMENTO"
  | "AGUARDANDO_CLIENTE"
  | "RESOLVIDO"
  | "CANCELADO";

export type PrioridadeChamadoSuporte = "BAIXA" | "MEDIA" | "ALTA" | "CRITICA";

export type OrigemMensagemChamadoSuporte = "CLIENTE" | "SUPORTE" | "SISTEMA";

export type ChamadoSuporte = {
  id: string;
  empresaId: string;
  solicitanteUsuarioId: string | null;
  solicitanteNome: string | null;
  solicitanteEmail: string | null;
  titulo: string;
  descricao: string;
  prioridade: PrioridadeChamadoSuporte;
  status: StatusChamadoSuporte;
  categoria: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type MensagemChamadoSuporte = {
  id: string;
  chamadoId: string;
  autorUsuarioId: string | null;
  autorNome: string | null;
  origem: OrigemMensagemChamadoSuporte;
  mensagem: string;
  criadoEm: string;
};

export type DetalheChamadoSuporte = {
  chamado: ChamadoSuporte;
  mensagens: MensagemChamadoSuporte[];
};

export type ChamadosSuportePaginados = {
  itens: ChamadoSuporte[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type ListarChamadosSuporteParams = {
  empresaId: string;
  pagina: number;
  tamanho: number;
  busca?: string;
  status?: StatusChamadoSuporte | "";
  prioridade?: PrioridadeChamadoSuporte | "";
};

export type AtualizarTriagemChamadoRequest = {
  status?: StatusChamadoSuporte;
  prioridade?: PrioridadeChamadoSuporte;
};

export type RegistrarMensagemChamadoRequest = {
  autorUsuarioId?: string | null;
  autorNome?: string | null;
  origem: OrigemMensagemChamadoSuporte;
  mensagem: string;
};

const suporteApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function listarChamadosSuporte(params: ListarChamadosSuporteParams) {
  return suporteApi.get<ChamadosSuportePaginados>("/api/suporte/chamados", {
    query: {
      empresaId: params.empresaId,
      pagina: params.pagina,
      tamanho: params.tamanho,
      busca: params.busca || undefined,
      status: params.status || undefined,
      prioridade: params.prioridade || undefined
    }
  });
}

export function detalharChamadoSuporte(chamadoId: string) {
  return suporteApi.get<DetalheChamadoSuporte>(`/api/suporte/chamados/${chamadoId}`);
}

export function atualizarTriagemChamadoSuporte(chamadoId: string, request: AtualizarTriagemChamadoRequest) {
  return suporteApi.patch<DetalheChamadoSuporte>(`/api/suporte/chamados/${chamadoId}/triagem`, request);
}

export function registrarMensagemChamadoSuporte(chamadoId: string, request: RegistrarMensagemChamadoRequest) {
  return suporteApi.post<DetalheChamadoSuporte>(`/api/suporte/chamados/${chamadoId}/mensagens`, request);
}
