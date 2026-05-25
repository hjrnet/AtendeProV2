"use client";

import { cn } from "@/lib/utils";
import type { SecaoPrincipal, SecaoPrincipalConfig } from "@/features/shell/types";

type MenuPrincipalProps = {
  secoes: SecaoPrincipalConfig[];
  secaoAtiva: SecaoPrincipal;
  definirSecaoAtiva: (secao: SecaoPrincipal) => void;
};

export function MenuPrincipal({ secoes, secaoAtiva, definirSecaoAtiva }: MenuPrincipalProps) {
  return (
    <nav className="grid gap-2" aria-label="Menu principal">
      {secoes.map((secao) => {
        const ativo = secao.id === secaoAtiva;
        const Icon = secao.icon;

        return (
          <button
            key={secao.id}
            type="button"
            aria-label={secao.label}
            aria-current={ativo ? "page" : undefined}
            title={secao.label}
            onClick={() => definirSecaoAtiva(secao.id)}
            className={cn(
              "group flex h-12 items-center justify-center gap-3 rounded-md border px-3 text-sm font-semibold transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring xl:justify-start",
              ativo
                ? "border-primary/25 bg-primary text-primary-foreground shadow-sm"
                : "border-transparent text-muted-foreground hover:border-border hover:bg-card hover:text-foreground"
            )}
          >
            <Icon className="h-5 w-5 shrink-0" />
            <span className="hidden xl:inline">{secao.label}</span>
          </button>
        );
      })}
    </nav>
  );
}
