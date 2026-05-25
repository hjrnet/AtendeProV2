import type { Metadata } from "next";
import { notFound } from "next/navigation";
import { PaginaVerticalPublica } from "@/features/marketing/components/pagina-vertical-publica";
import { obterVerticalPublica, verticaisPublicas } from "@/features/marketing/data/verticais-publicas";

type PaginaVerticalParams = {
  params: {
    slug: string;
  };
};

export function generateStaticParams() {
  return verticaisPublicas.map((vertical) => ({
    slug: vertical.slug
  }));
}

export function generateMetadata({ params }: PaginaVerticalParams): Metadata {
  const vertical = obterVerticalPublica(params.slug);

  if (!vertical) {
    return {
      title: "Vertical não encontrada | AtendePro"
    };
  }

  return {
    title: `${vertical.nome} | AtendePro`,
    description: vertical.resumo
  };
}

export default function PaginaVertical({ params }: PaginaVerticalParams) {
  const vertical = obterVerticalPublica(params.slug);

  if (!vertical) {
    notFound();
  }

  return <PaginaVerticalPublica vertical={vertical} />;
}
