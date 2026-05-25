"use client";

import { useState } from "react";
import { BarChart3, Calculator } from "lucide-react";

import { DashboardPrecificacaoView } from "@/features/precificacao/components/dashboard-precificacao-view";
import { SimuladorPrecificacaoView } from "@/features/precificacao/components/simulador-precificacao-view";
import { cn } from "@/lib/utils";

type SecaoPrecificacaoProps = {
  empresaId: string;
};

type AbaPrecificacao = "dashboard" | "simulador";

const abas: Array<{ id: AbaPrecificacao; label: string; icon: typeof Calculator }> = [
  { id: "dashboard", label: "Dashboard", icon: BarChart3 },
  { id: "simulador", label: "Simulador", icon: Calculator }
];

export function SecaoPrecificacao({ empresaId }: SecaoPrecificacaoProps) {
  const [abaAtiva, setAbaAtiva] = useState<AbaPrecificacao>("dashboard");

  return (
    <section className="grid gap-4">
      <div className="flex flex-col gap-3 rounded-lg border bg-card p-4 shadow-sm lg:flex-row lg:items-center lg:justify-between">
        <div>
          <p className="text-sm font-medium text-primary">Precificacao</p>
          <h2 className="mt-1 text-lg font-semibold text-card-foreground">Custo real, margem e simulacoes</h2>
        </div>

        <div className="grid grid-cols-2 gap-2 lg:w-auto" role="tablist" aria-label="Areas de precificacao">
          {abas.map((aba) => {
            const Icon = aba.icon;
            const ativa = aba.id === abaAtiva;

            return (
              <button
                key={aba.id}
                type="button"
                role="tab"
                aria-selected={ativa}
                onClick={() => setAbaAtiva(aba.id)}
                className={cn(
                  "flex h-10 items-center justify-center gap-2 rounded-md border px-3 text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
                  ativa ? "border-primary bg-primary text-primary-foreground" : "bg-background text-muted-foreground hover:bg-muted hover:text-foreground"
                )}
              >
                <Icon className="h-4 w-4" />
                {aba.label}
              </button>
            );
          })}
        </div>
      </div>

      <div className="grid gap-4 xl:hidden">
        {abaAtiva === "dashboard" ? <DashboardPrecificacaoView empresaId={empresaId} /> : <SimuladorPrecificacaoView empresaId={empresaId} />}
      </div>

      <div className="hidden gap-4 xl:grid">
        <DashboardPrecificacaoView empresaId={empresaId} />
        <SimuladorPrecificacaoView empresaId={empresaId} />
      </div>
    </section>
  );
}
