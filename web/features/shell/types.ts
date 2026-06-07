import type { LucideIcon } from "lucide-react";

export type SecaoPrincipal =
  | "operacao"
  | "verticais"
  | "precificacao"
  | "busca"
  | "admin"
  | "portal-cliente"
  | "nutri-inicio"
  | "nutri-agenda"
  | "nutri-pacientes"
  | "nutri-prontuario"
  | "nutri-plano"
  | "nutri-avaliacoes"
  | "nutri-documentos"
  | "beauty-inicio"
  | "beauty-agenda"
  | "beauty-clientes"
  | "beauty-ficha"
  | "beauty-protocolos"
  | "beauty-termos";

export type SecaoPrincipalConfig = {
  id: SecaoPrincipal;
  label: string;
  labelCurto: string;
  titulo: string;
  descricao: string;
  icon: LucideIcon;
};
