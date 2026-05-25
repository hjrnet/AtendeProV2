import type { Metadata } from "next";
import { PaginaPlanosPublica } from "@/features/marketing/components/pagina-planos-publica";

export const metadata: Metadata = {
  title: "Planos AtendePro | Comparação e trial 30 dias",
  description:
    "Compare planos do AtendePro para profissionais solo, verticais profissionais, equipes, Spaces e operação premium."
};

export default function PlanosPage() {
  return <PaginaPlanosPublica />;
}
