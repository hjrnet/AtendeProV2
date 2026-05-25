import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type StatusOperacionalBeautyPro = "OPERACIONAL" | "CONFIGURACAO_PENDENTE";

export type IndicadorBeautyPro = {
  codigo: string;
  titulo: string;
  valor: number;
  descricao: string;
  status: string;
};

export type AtalhoBeautyPro = {
  codigo: string;
  titulo: string;
  descricao: string;
  status: string;
  destino: string;
};

export type ClienteBeautyResumo = {
  id: string;
  nome: string;
  telefone: string | null;
  observacoes: string | null;
  ativo: boolean;
  atualizadoEm: string;
};

export type VisaoBeautyPro = {
  empresaId: string;
  empresaNome: string;
  statusOperacional: StatusOperacionalBeautyPro;
  statusOperacionalRotulo: string;
  mensagemStatus: string;
  indicadores: IndicadorBeautyPro[];
  atalhosPrioritarios: AtalhoBeautyPro[];
  proximasEvolucoes: AtalhoBeautyPro[];
  clientesRecentes: ClienteBeautyResumo[];
  atualizadoEm: string;
};

const beautyProApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function consultarVisaoBeautyPro(empresaId: string) {
  return beautyProApi.get<VisaoBeautyPro>("/api/beauty-pro/visao", {
    query: { empresaId }
  });
}
