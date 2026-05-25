import type { Metadata } from "next";
import { FormularioLeadPublico } from "@/features/marketing/components/formulario-lead-publico";

export const metadata: Metadata = {
  title: "Solicitar contato | AtendePro",
  description:
    "Registre interesse no AtendePro para testar a demonstração local e conversar sobre módulos para sua operação."
};

export default function ContatoPage() {
  return <FormularioLeadPublico />;
}
