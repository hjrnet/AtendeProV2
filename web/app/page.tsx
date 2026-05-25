import type { Metadata } from "next";
import { LandingPagePublica } from "@/features/marketing/components/landing-page-publica";

export const metadata: Metadata = {
  title: "AtendePro | SaaS profissional para clínicas, saúde e beleza",
  description:
    "AtendePro organiza agenda, pacientes, custos, precificação, documentos e verticais profissionais em um SaaS premium multiempresa."
};

export default function Home() {
  return <LandingPagePublica />;
}
