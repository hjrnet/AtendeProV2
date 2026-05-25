"use client";

import { Search } from "lucide-react";

import type { SessaoAutenticada } from "@/features/auth/lib/auth-storage";
import { SecaoAdminSaas } from "@/features/admin-planos/components/secao-admin-saas";
import { SecaoBuscaGlobal } from "@/features/operacional/components/secao-busca-global";
import { SecaoOperacao } from "@/features/operacional/components/secao-operacao";
import { SecaoPrecificacao } from "@/features/precificacao/components/secao-precificacao";
import type { SecaoPrincipal } from "@/features/shell/types";
import { VerticaisProfissionaisView } from "@/features/verticais/components/verticais-profissionais-view";

type PainelConteudoAtivoProps = {
  secaoAtiva: SecaoPrincipal;
  empresaId: string;
  sessao: SessaoAutenticada;
};

export function PainelConteudoAtivo({ secaoAtiva, empresaId, sessao }: PainelConteudoAtivoProps) {
  return (
    <section className="px-4 py-5 sm:px-6 lg:px-8">
      {renderizarSecaoAtiva(secaoAtiva, empresaId, sessao)}
    </section>
  );
}

function renderizarSecaoAtiva(secaoAtiva: SecaoPrincipal, empresaId: string, sessao: SessaoAutenticada) {
  switch (secaoAtiva) {
    case "operacao":
      return <SecaoOperacao empresaId={empresaId} sessao={sessao} />;
    case "verticais":
      return <VerticaisProfissionaisView empresaId={empresaId} />;
    case "precificacao":
      return <SecaoPrecificacao empresaId={empresaId} />;
    case "busca":
      return <SecaoBuscaGlobal empresaId={empresaId} />;
    case "admin":
      return <SecaoAdminSaas empresaId={empresaId} sessao={sessao} />;
    default:
      return (
        <div className="flex min-h-72 flex-col items-center justify-center rounded-lg border bg-card p-8 text-center shadow-sm">
          <Search className="h-9 w-9 text-primary" />
          <h2 className="mt-3 text-lg font-semibold text-card-foreground">Seção indisponível</h2>
          <p className="mt-1 max-w-md text-sm leading-6 text-muted-foreground">Escolha uma área no menu principal.</p>
        </div>
      );
  }
}
