"use client";

import { Building2, CreditCard, LifeBuoy, PackageCheck, TrendingUp } from "lucide-react";

import { AdminPlanosView } from "@/features/admin-planos/components/admin-planos-view";

const secoesAdmin = [
  { label: "Planos", icon: PackageCheck, ativo: true },
  { label: "Empresas", icon: Building2, ativo: false },
  { label: "Assinaturas", icon: CreditCard, ativo: false },
  { label: "Suporte", icon: LifeBuoy, ativo: false },
  { label: "Relatorios", icon: TrendingUp, ativo: false }
];

export function SecaoAdminSaas() {
  return (
    <section className="grid gap-4">
      <div className="rounded-lg border bg-card p-4 shadow-sm">
        <div className="flex gap-2 overflow-x-auto pb-1" aria-label="Subsecoes Admin SaaS">
          {secoesAdmin.map((secao) => {
            const Icon = secao.icon;

            return (
              <button
                key={secao.label}
                type="button"
                disabled={!secao.ativo}
                className="inline-flex h-10 shrink-0 items-center gap-2 rounded-md border bg-background px-3 text-sm font-semibold text-card-foreground transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring disabled:opacity-55"
              >
                <Icon className="h-4 w-4 text-primary" />
                {secao.label}
              </button>
            );
          })}
        </div>
      </div>

      <AdminPlanosView />
    </section>
  );
}
