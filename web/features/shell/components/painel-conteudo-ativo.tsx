"use client";

import { Search } from "lucide-react";

import type { SessaoAutenticada } from "@/features/auth/lib/auth-storage";
import { SecaoAdminSaas } from "@/features/admin-planos/components/secao-admin-saas";
import { PortalClienteView } from "@/features/portal-cliente/components/portal-cliente-view";
import { BeautyProOperacionalView } from "@/features/beauty-pro/components/beauty-pro-operacional-view";
import { NutriProOperacionalView } from "@/features/nutri-pro/components/nutri-pro-operacional-view";
import { SecaoBuscaGlobal } from "@/features/operacional/components/secao-busca-global";
import { SecaoOperacao } from "@/features/operacional/components/secao-operacao";
import { SecaoPrecificacao } from "@/features/precificacao/components/secao-precificacao";
import { PosVendaOperacionalView } from "@/features/relacionamento/components/pos-venda-operacional-view";
import type { SecaoPrincipal } from "@/features/shell/types";
import { VerticaisProfissionaisView } from "@/features/verticais/components/verticais-profissionais-view";

type PainelConteudoAtivoProps = {
  secaoAtiva: SecaoPrincipal;
  empresaId: string;
  sessao: SessaoAutenticada;
  definirSecaoAtiva: (secao: SecaoPrincipal) => void;
};

export function PainelConteudoAtivo({ secaoAtiva, empresaId, sessao, definirSecaoAtiva }: PainelConteudoAtivoProps) {
  return (
    <section className="px-4 py-5 sm:px-6 lg:px-8">
      {renderizarSecaoAtiva(secaoAtiva, empresaId, sessao, definirSecaoAtiva)}
    </section>
  );
}

function renderizarSecaoAtiva(
  secaoAtiva: SecaoPrincipal,
  empresaId: string,
  sessao: SessaoAutenticada,
  definirSecaoAtiva: (secao: SecaoPrincipal) => void
) {
  switch (secaoAtiva) {
    case "nutri-inicio":
    case "nutri-agenda":
    case "nutri-pacientes":
    case "nutri-prontuario":
    case "nutri-plano":
    case "nutri-avaliacoes":
    case "nutri-documentos":
      return <NutriProOperacionalView empresaId={empresaId} focoWorkspace={secaoAtiva} onNavegar={(secao) => definirSecaoAtiva(secao)} />;
    case "nutri-pos-venda":
      return <PosVendaOperacionalView empresaId={empresaId} area="NUTRI" />;
    case "beauty-inicio":
    case "beauty-agenda":
    case "beauty-clientes":
    case "beauty-ficha":
    case "beauty-protocolos":
    case "beauty-estoque":
    case "beauty-termos":
      return <BeautyProOperacionalView empresaId={empresaId} focoWorkspace={secaoAtiva} />;
    case "beauty-pos-venda":
      return <PosVendaOperacionalView empresaId={empresaId} area="BEAUTY" />;
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
    case "portal-cliente":
      return <PortalClienteView empresaId={empresaId} />;
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
