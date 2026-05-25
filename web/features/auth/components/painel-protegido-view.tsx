"use client";

import { useMemo } from "react";
import { useRouter } from "next/navigation";
import { Building2, CalendarDays, LayoutDashboard, LogOut, ShieldCheck, Users } from "lucide-react";

import { Button } from "@/components/ui/button";
import { limparSessaoAutenticada, type SessaoAutenticada } from "@/features/auth/lib/auth-storage";

const atalhos = [
  { nome: "Empresas", detalhe: "Tenant ativo", icone: Building2 },
  { nome: "Usuarios", detalhe: "Permissoes base", icone: Users },
  { nome: "Agenda", detalhe: "Proxima release", icone: CalendarDays }
];

type PainelProtegidoViewProps = {
  sessao: SessaoAutenticada;
};

export function PainelProtegidoView({ sessao }: PainelProtegidoViewProps) {
  const router = useRouter();
  const perfilPrincipal = useMemo(() => sessao.usuario.perfis.at(0)?.replace("_", " ") ?? "Acesso", [sessao]);

  function sair() {
    limparSessaoAutenticada();
    router.replace("/login");
  }

  return (
    <main className="min-h-screen px-4 py-5 sm:px-6 lg:px-10">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-6">
        <header className="flex flex-col gap-4 border-b pb-5 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-sm font-medium text-primary">AtendePro SaaS</p>
            <h1 className="mt-1 text-2xl font-semibold tracking-normal text-foreground sm:text-3xl">
              Painel protegido
            </h1>
          </div>
          <Button type="button" variant="outline" onClick={sair}>
            <LogOut className="h-4 w-4" />
            Sair
          </Button>
        </header>

        <section className="rounded-lg border bg-card p-5 shadow-sm">
          <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div className="flex items-center gap-3">
              <span className="flex h-11 w-11 items-center justify-center rounded-md bg-primary text-primary-foreground">
                <ShieldCheck className="h-5 w-5" />
              </span>
              <div>
                <p className="text-sm text-muted-foreground">Sessao autenticada</p>
                <h2 className="text-lg font-semibold text-card-foreground">{sessao.usuario.nome}</h2>
              </div>
            </div>
            <span className="inline-flex w-fit items-center gap-2 rounded-md border bg-background px-3 py-2 text-sm font-medium text-card-foreground">
              <LayoutDashboard className="h-4 w-4 text-primary" />
              {perfilPrincipal}
            </span>
          </div>

          <div className="mt-5 grid gap-3 sm:grid-cols-3">
            <div>
              <p className="text-xs font-medium uppercase text-muted-foreground">Email</p>
              <p className="mt-1 truncate text-sm font-semibold text-card-foreground">{sessao.usuario.email}</p>
            </div>
            <div>
              <p className="text-xs font-medium uppercase text-muted-foreground">Empresa</p>
              <p className="mt-1 truncate text-sm font-semibold text-card-foreground">
                {sessao.usuario.empresaId ?? "SaaS"}
              </p>
            </div>
            <div>
              <p className="text-xs font-medium uppercase text-muted-foreground">Authorities</p>
              <p className="mt-1 text-sm font-semibold text-card-foreground">{sessao.usuario.authorities.length}</p>
            </div>
          </div>
        </section>

        <section className="grid gap-3 sm:grid-cols-3">
          {atalhos.map((atalho) => {
            const Icone = atalho.icone;

            return (
              <article key={atalho.nome} className="rounded-lg border bg-card p-4 shadow-sm">
                <div className="flex items-center gap-3">
                  <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 text-primary">
                    <Icone className="h-5 w-5" />
                  </span>
                  <div>
                    <h2 className="text-sm font-semibold text-card-foreground">{atalho.nome}</h2>
                    <p className="text-sm text-muted-foreground">{atalho.detalhe}</p>
                  </div>
                </div>
              </article>
            );
          })}
        </section>
      </div>
    </main>
  );
}
