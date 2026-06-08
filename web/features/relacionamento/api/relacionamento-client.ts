import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type AreaPosVenda = "NUTRI" | "BEAUTY";
export type CanalContatoPosVenda = "WHATSAPP" | "EMAIL" | "TELEFONE" | "SMS" | "PRESENCIAL" | "OUTRO";
export type TipoTarefaPosVenda = "RETORNO" | "CHECKIN" | "REATIVACAO" | "NPS" | "ANIVERSARIO" | "CAMPANHA" | "OUTRO";
export type StatusTarefaPosVenda = "PENDENTE" | "CONCLUIDA";

export type MetricasPosVenda = {
  clientesMonitorados: number;
  retornosPendentes: number;
  clientesInativos: number;
  faltasRecentes: number;
  clientesSemContato: number;
  oportunidadesRecorrencia: number;
  npsMedio: number;
  detratores: number;
};

export type ClientePosVenda = {
  id: string;
  nome: string;
  area: AreaPosVenda;
  email: string | null;
  telefone: string | null;
  dataNascimento: string | null;
  ultimaConsultaEm: string | null;
  proximaConsultaEm: string | null;
  ultimoContatoEm: string | null;
  faltasRecentes: number;
  ultimaNotaNps: number | null;
  statusAcompanhamento: string;
  statusRotulo: string;
  retornoRecomendadoEm: string | null;
  motivoRetorno: string;
  riscoAbandono: "ALTO" | "MEDIO" | "BAIXO";
  aniversarioProximo: boolean;
  oportunidadeRecorrencia: boolean;
};

export type TarefaPosVenda = {
  id: string | null;
  clienteId: string;
  clienteNome: string;
  area: AreaPosVenda;
  tipo: TipoTarefaPosVenda;
  titulo: string;
  descricao: string | null;
  dataRecomendada: string | null;
  status: StatusTarefaPosVenda;
  origem: string | null;
  criadoEm: string;
  atualizadoEm: string;
};

export type TemplateMensagemPosVenda = {
  codigo: string;
  area: AreaPosVenda | "GERAL";
  titulo: string;
  objetivo: string;
  mensagem: string;
  variaveis: string[];
};

export type ContatoPosVenda = {
  id: string;
  clienteId: string;
  clienteNome: string;
  area: AreaPosVenda;
  canal: CanalContatoPosVenda;
  templateCodigo: string | null;
  mensagem: string;
  observacoes: string | null;
  criadoEm: string;
};

export type PesquisaNpsPosVenda = {
  id: string;
  clienteId: string;
  clienteNome: string;
  area: AreaPosVenda;
  nota: number;
  comentario: string | null;
  origem: string | null;
  criadoEm: string;
};

export type SegmentoCampanhaPosVenda = {
  codigo: string;
  titulo: string;
  descricao: string;
  quantidadeClientes: number;
  acaoRecomendada: string;
};

export type PainelPosVenda = {
  empresaId: string;
  area: AreaPosVenda | null;
  metricas: MetricasPosVenda;
  clientes: ClientePosVenda[];
  tarefas: TarefaPosVenda[];
  templates: TemplateMensagemPosVenda[];
  contatosRecentes: ContatoPosVenda[];
  npsRecentes: PesquisaNpsPosVenda[];
  segmentos: SegmentoCampanhaPosVenda[];
  atualizadoEm: string;
};

export type RegistrarContatoPosVendaInput = {
  empresaId: string;
  clienteId: string;
  area: AreaPosVenda;
  canal: CanalContatoPosVenda;
  templateCodigo?: string | null;
  mensagem: string;
  observacoes?: string | null;
};

export type RegistrarNpsPosVendaInput = {
  empresaId: string;
  clienteId: string;
  area: AreaPosVenda;
  nota: number;
  comentario?: string | null;
  origem?: string | null;
};

export type CriarTarefaPosVendaInput = {
  empresaId: string;
  clienteId: string;
  area: AreaPosVenda;
  tipo: TipoTarefaPosVenda;
  titulo: string;
  descricao?: string | null;
  dataRecomendada?: string | null;
  origem?: string | null;
};

const relacionamentoApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function consultarPainelPosVenda(params: { empresaId: string; area: AreaPosVenda; busca?: string }) {
  return relacionamentoApi.get<PainelPosVenda>("/api/relacionamento/pos-venda", {
    query: {
      empresaId: params.empresaId,
      area: params.area,
      busca: params.busca || undefined
    }
  });
}

export function registrarContatoPosVenda(dados: RegistrarContatoPosVendaInput) {
  return relacionamentoApi.post<ContatoPosVenda>("/api/relacionamento/contatos", dados);
}

export function registrarNpsPosVenda(dados: RegistrarNpsPosVendaInput) {
  return relacionamentoApi.post<PesquisaNpsPosVenda>("/api/relacionamento/nps", dados);
}

export function criarTarefaPosVenda(dados: CriarTarefaPosVendaInput) {
  return relacionamentoApi.post<TarefaPosVenda>("/api/relacionamento/tarefas", dados);
}

export function concluirTarefaPosVenda(params: { empresaId: string; tarefaId: string }) {
  return relacionamentoApi.patch<TarefaPosVenda>(`/api/relacionamento/tarefas/${params.tarefaId}/concluir`, undefined, {
    query: { empresaId: params.empresaId }
  });
}
