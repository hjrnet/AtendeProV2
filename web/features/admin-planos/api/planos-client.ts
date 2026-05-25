import { criarApiClient } from "@/lib/api";
import { carregarSessaoAutenticada, limparSessaoAutenticada } from "@/features/auth/lib/auth-storage";

export type Plano = {
  id: string;
  codigo: string;
  nome: string;
  descricao: string | null;
  valorMensal: number;
  limiteUsuarios: number;
  limiteClientes: number;
  limiteProfissionais: number;
  ativo: boolean;
  estudante: boolean;
  marcaDaguaAcademica: string | null;
  modulos: string[];
  criadoEm: string;
  atualizadoEm: string;
};

export type PlanosPaginados = {
  itens: Plano[];
  totalItens: number;
  pagina: number;
  tamanho: number;
  totalPaginas: number;
};

export type SalvarPlanoRequest = {
  codigo: string;
  nome: string;
  descricao?: string | null;
  valorMensal: number;
  limiteUsuarios: number;
  limiteClientes: number;
  limiteProfissionais: number;
  ativo: boolean;
  estudante: boolean;
  marcaDaguaAcademica?: string | null;
  modulos: string[];
};

const adminApi = criarApiClient({
  getAccessToken: () => carregarSessaoAutenticada()?.accessToken ?? null,
  onUnauthorized: () => limparSessaoAutenticada()
});

export function listarPlanos(params: { pagina: number; tamanho: number; busca?: string }) {
  return adminApi.get<PlanosPaginados>("/api/admin-saas/planos", {
    query: {
      pagina: params.pagina,
      tamanho: params.tamanho,
      busca: params.busca || undefined
    }
  });
}

export function criarPlano(request: SalvarPlanoRequest) {
  return adminApi.post<Plano>("/api/admin-saas/planos", request);
}

export function atualizarPlano(planoId: string, request: SalvarPlanoRequest) {
  return adminApi.put<Plano>(`/api/admin-saas/planos/${planoId}`, request);
}
