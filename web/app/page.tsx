import { Activity, CalendarDays, FileText, LineChart, Users } from "lucide-react";

import { Button } from "@/components/ui/button";

const indicadores = [
  { rotulo: "Empresas ativas", valor: "0", detalhe: "Fundacao em andamento" },
  { rotulo: "Agenda de hoje", valor: "0", detalhe: "Modulo futuro" },
  { rotulo: "Documentos", valor: "0", detalhe: "Modulo futuro" }
];

const modulos = [
  { nome: "Clientes", icone: Users },
  { nome: "Agenda", icone: CalendarDays },
  { nome: "Precificacao", icone: LineChart },
  { nome: "Documentos", icone: FileText }
];

export default function Home() {
  return (
    <main className="min-h-screen px-4 py-5 sm:px-6 lg:px-10">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-6">
        <header className="flex flex-col gap-4 border-b pb-5 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-sm font-medium text-muted-foreground">AtendePro SaaS</p>
            <h1 className="mt-1 text-2xl font-semibold tracking-normal text-foreground sm:text-3xl">
              Operacao profissional em construcao
            </h1>
          </div>
          <Button>
            <Activity className="h-4 w-4" />
            Status da plataforma
          </Button>
        </header>

        <section className="grid gap-3 sm:grid-cols-3">
          {indicadores.map((indicador) => (
            <article key={indicador.rotulo} className="rounded-lg border bg-card p-4 shadow-sm">
              <p className="text-sm text-muted-foreground">{indicador.rotulo}</p>
              <strong className="mt-2 block text-2xl font-semibold">{indicador.valor}</strong>
              <span className="mt-1 block text-sm text-muted-foreground">{indicador.detalhe}</span>
            </article>
          ))}
        </section>

        <section className="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          {modulos.map((modulo) => {
            const Icone = modulo.icone;

            return (
              <article key={modulo.nome} className="flex items-center gap-3 rounded-lg border bg-card p-4 shadow-sm">
                <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary text-primary-foreground">
                  <Icone className="h-5 w-5" />
                </span>
                <div>
                  <h2 className="text-sm font-semibold">{modulo.nome}</h2>
                  <p className="text-sm text-muted-foreground">Aguardando release</p>
                </div>
              </article>
            );
          })}
        </section>
      </div>
    </main>
  );
}
