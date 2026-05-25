"use client";

import { Building2, LogOut, ShieldCheck } from "lucide-react";

import { Button } from "@/components/ui/button";
import type { SessaoAutenticada } from "@/features/auth/lib/auth-storage";
import type { EmpresaResumo } from "@/features/operacional/api/operacional-client";
import type { SecaoPrincipalConfig } from "@/features/shell/types";

type BarraSuperiorContextualProps = {
  sessao: SessaoAutenticada;
  secao: SecaoPrincipalConfig;
  perfilPrincipal: string;
  ehUsuarioSaas: boolean;
  empresas: EmpresaResumo[];
  empresaSelecionadaId: string;
  empresaAtualNome: string;
  onEmpresaChange: (empresaId: string) => void;
  onSair: () => void;
};

export function BarraSuperiorContextual({
  sessao,
  secao,
  perfilPrincipal,
  ehUsuarioSaas,
  empresas,
  empresaSelecionadaId,
  empresaAtualNome,
  onEmpresaChange,
  onSair
}: BarraSuperiorContextualProps) {
  const Icon = secao.icon;

  return (
    <header className="sticky top-0 z-20 border-b bg-background/90 px-4 py-4 backdrop-blur sm:px-6 lg:px-8">
      <div className="flex flex-col gap-4 xl:flex-row xl:items-center xl:justify-between">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2 text-sm font-medium text-muted-foreground">
            <span className="inline-flex h-9 items-center gap-2 rounded-md border bg-card px-3">
              <ShieldCheck className="h-4 w-4 text-primary" />
              {perfilPrincipal}
            </span>
            <span className="inline-flex h-9 min-w-0 items-center gap-2 rounded-md border bg-card px-3">
              <Building2 className="h-4 w-4 shrink-0 text-primary" />
              <span className="truncate">{empresaAtualNome}</span>
            </span>
          </div>

          <div className="mt-3 flex min-w-0 items-start gap-3">
            <span className="hidden h-11 w-11 shrink-0 items-center justify-center rounded-md bg-primary/10 text-primary sm:flex">
              <Icon className="h-5 w-5" />
            </span>
            <div className="min-w-0">
              <h1 className="text-2xl font-semibold tracking-normal text-foreground sm:text-3xl">{secao.titulo}</h1>
              <p className="mt-1 max-w-3xl text-sm leading-6 text-muted-foreground">{secao.descricao}</p>
            </div>
          </div>
        </div>

        <div className="flex flex-col gap-2 sm:flex-row sm:items-end xl:items-center">
          {ehUsuarioSaas ? (
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Empresa
              <select
                value={empresaSelecionadaId}
                onChange={(event) => onEmpresaChange(event.target.value)}
                className="h-10 min-w-0 rounded-md border bg-card px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring sm:min-w-64"
              >
                {empresas.map((empresa) => (
                  <option key={empresa.id} value={empresa.id}>
                    {empresa.nomeFantasia}
                  </option>
                ))}
              </select>
            </label>
          ) : null}

          <div className="grid grid-cols-[1fr_auto] gap-2 sm:flex sm:items-center">
            <div className="min-w-0 rounded-md border bg-card px-3 py-2 text-sm">
              <p className="truncate font-semibold text-card-foreground">{sessao.usuario.nome}</p>
              <p className="truncate text-xs text-muted-foreground">{sessao.usuario.email}</p>
            </div>
            <Button type="button" variant="outline" onClick={onSair} aria-label="Sair">
              <LogOut className="h-4 w-4" />
              <span className="hidden sm:inline">Sair</span>
            </Button>
          </div>
        </div>
      </div>
    </header>
  );
}
