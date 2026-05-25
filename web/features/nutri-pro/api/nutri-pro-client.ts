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
