import type { LucideIcon } from "lucide-react";

export type SecaoPrincipal =
  | "operacao"
  | "verticais"
  | "precificacao"
  | "busca"
  | "admin"
  | "nutri-inicio"
  | "nutri-agenda"
  | "nutri-pacientes"
  | "nutri-prontuario"
  | "nutri-plano"
  | "nutri-avaliacoes"
  | "nutri-documentos"
  | "beauty-inicio"
  | "beauty-agenda";

export type SecaoPrincipalConfig = {
  id: SecaoPrincipal;
  label: string;
  labelCurto: string;
  titulo: string;
  descricao: string;
  icon: LucideIcon;
};
