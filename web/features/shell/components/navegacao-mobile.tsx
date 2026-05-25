"use client";

import { cn } from "@/lib/utils";
import type { SecaoPrincipal, SecaoPrincipalConfig } from "@/features/shell/types";

type NavegacaoMobileProps = {
  secoes: SecaoPrincipalConfig[];
  secaoAtiva: SecaoPrincipal;
  definirSecaoAtiva: (secao: SecaoPrincipal) => void;
};

export function NavegacaoMobile({ secoes, secaoAtiva, definirSecaoAtiva }: NavegacaoMobileProps) {
  return (
    <nav
      className="fixed inset-x-0 bottom-0 z-30 border-t bg-card/95 px-2 pb-[max(0.5rem,env(safe-area-inset-bottom))] pt-2 shadow-[0_-12px_30px_rgba(15,23,42,0.12)] backdrop-blur md:hidden"
      aria-label="Navegacao mobile"
    >
      <div className="mx-auto grid max-w-lg grid-cols-5 gap-1">
        {secoes.map((secao) => {
          const ativo = secao.id === secaoAtiva;
          const Icon = secao.icon;

          return (
            <button
              key={secao.id}
              type="button"
              aria-current={ativo ? "page" : undefined}
              aria-label={secao.label}
              onClick={() => definirSecaoAtiva(secao.id)}
              className={cn(
                "flex min-h-12 flex-col items-center justify-center gap-1 rounded-md px-1 text-[10px] font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
                ativo ? "bg-primary text-primary-foreground" : "text-muted-foreground hover:bg-muted hover:text-foreground"
              )}
            >
              <Icon className="h-4 w-4" />
              <span className="max-w-full truncate">{secao.labelCurto}</span>
            </button>
          );
        })}
      </div>
    </nav>
  );
}
