"use client";

import { useState } from "react";
import { BookOpenText, Building2, CreditCard, LifeBuoy, PackageCheck, Route, TrendingUp } from "lucide-react";

import { AdminPlanosView } from "@/features/admin-planos/components/admin-planos-view";
import type { SessaoAutenticada } from "@/features/auth/lib/auth-storage";
import { CentralAjudaView } from "@/features/suporte/components/central-ajuda-view";
import { FeedbackRoadmapView } from "@/features/suporte/components/feedback-roadmap-view";
import { PainelAdminSuporte } from "@/features/suporte/components/painel-admin-suporte";

const secoesAdmin = [
  { id: "planos", label: "Planos", icon: PackageCheck, ativo: true },
  { id: "empresas", label: "Empresas", icon: Building2, ativo: false },
  { id: "assinaturas", label: "Assinaturas", icon: CreditCard, ativo: false },
  { id: "suporte", label: "Suporte", icon: LifeBuoy, ativo: true },
  { id: "ajuda", label: "Ajuda", icon: BookOpenText, ativo: true },
  { id: "roadmap", label: "Roadmap", icon: Route, ativo: true },
  { id: "relatorios", label: "Relatórios", icon: TrendingUp, ativo: false }
];

type SubsecaoAdmin = "planos" | "suporte" | "ajuda" | "roadmap";

type SecaoAdminSaasProps = {
  empresaId: string;
  sessao: SessaoAutenticada;
};

export function SecaoAdminSaas({ empresaId, sessao }: SecaoAdminSaasProps) {
  const [subsecaoAtiva, setSubsecaoAtiva] = useState<SubsecaoAdmin>("planos");

  return (
    <section className="grid gap-4">
      <div className="rounded-lg border bg-card p-4 shadow-sm">
        <div className="flex gap-2 overflow-x-auto pb-1" aria-label="Subseções Admin SaaS">
          {secoesAdmin.map((secao) => {
            const Icon = secao.icon;
            const ativo = subsecaoAtiva === secao.id;

            return (
              <button
                key={secao.id}
                type="button"
                disabled={!secao.ativo}
                onClick={() => secao.ativo && setSubsecaoAtiva(secao.id as SubsecaoAdmin)}
                className={`inline-flex h-10 shrink-0 items-center gap-2 rounded-md border px-3 text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:opacity-55 ${
                  ativo ? "border-primary bg-primary text-primary-foreground" : "bg-background text-card-foreground"
                }`}
              >
                <Icon className={`h-4 w-4 ${ativo ? "text-primary-foreground" : "text-primary"}`} />
                {secao.label}
              </button>
            );
          })}
        </div>
      </div>

      {renderizarSubsecaoAdmin(subsecaoAtiva, empresaId, sessao)}
    </section>
  );
}

function renderizarSubsecaoAdmin(subsecao: SubsecaoAdmin, empresaId: string, sessao: SessaoAutenticada) {
  if (subsecao === "suporte") {
    return <PainelAdminSuporte empresaId={empresaId} sessao={sessao} />;
  }
  if (subsecao === "ajuda") {
    return <CentralAjudaView />;
  }
  if (subsecao === "roadmap") {
    return <FeedbackRoadmapView />;
  }
  return <AdminPlanosView />;
}
