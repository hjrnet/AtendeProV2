import type { LucideIcon } from "lucide-react";

export type SecaoPrincipal = "operacao" | "verticais" | "precificacao" | "busca" | "admin";

export type SecaoPrincipalConfig = {
  id: SecaoPrincipal;
  label: string;
  labelCurto: string;
  titulo: string;
  descricao: string;
  icon: LucideIcon;
};
