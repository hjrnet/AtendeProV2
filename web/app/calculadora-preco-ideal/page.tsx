import type { Metadata } from "next";
import { CalculadoraPrecoIdealPublica } from "@/features/marketing/components/calculadora-preco-ideal-publica";

export const metadata: Metadata = {
  title: "Calculadora de Preço Ideal | AtendePro",
  description:
    "Calcule gratuitamente custo real, preço mínimo, preço recomendado e alerta de margem para procedimentos e serviços."
};

export default function CalculadoraPrecoIdealPage() {
  return <CalculadoraPrecoIdealPublica />;
}
