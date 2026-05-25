"use client";

import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { ClipboardList, LoaderCircle, Search, Stethoscope } from "lucide-react";

import {
  listarVerticaisProfissionais,
  type VerticalProfissional
} from "@/features/verticais/api/verticais-client";

export function VerticaisProfissionaisView() {
  const [busca, setBusca] = useState("");
  const verticaisQuery = useQuery({
    queryKey: ["verticais-profissionais"],
    queryFn: listarVerticaisProfissionais
  });

  const verticais = useMemo(() => {
    const termo = busca.trim().toLowerCase();
    const itens = verticaisQuery.data?.itens ?? [];

    if (!termo) {
      return itens;
    }

    return itens.filter((vertical) =>
      [vertical.nome, vertical.resumo, vertical.conselhoProfissional ?? "", ...vertical.capacidades]
        .join(" ")
        .toLowerCase()
        .includes(termo)
    );
  }, [busca, verticaisQuery.data]);

  return (
    <section className="grid gap-4">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
        <div className="min-w-0">
          <p className="text-sm font-medium text-primary">Verticais profissionais</p>
          <h2 className="text-xl font-semibold tracking-normal text-foreground">Modulos por area</h2>
        </div>

        <label className="grid gap-1 text-sm font-medium text-card-foreground lg:w-80">
          Busca
          <span className="relative">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <input
              value={busca}
              onChange={(event) => setBusca(event.target.value)}
              className="h-10 w-full rounded-md border bg-card pl-10 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Area, conselho, capacidade"
            />
          </span>
        </label>
      </div>

      {verticaisQuery.isLoading ? (
        <div className="flex h-36 items-center justify-center rounded-lg border bg-card text-sm text-muted-foreground">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando verticais
        </div>
      ) : verticais.length === 0 ? (
        <div className="flex h-36 items-center justify-center rounded-lg border bg-card text-sm font-medium text-muted-foreground">
          Nenhuma vertical encontrada
        </div>
      ) : (
        <div className="grid gap-3 xl:grid-cols-2">
          {verticais.map((vertical) => (
            <VerticalCard key={vertical.codigo} vertical={vertical} />
          ))}
        </div>
      )}
    </section>
  );
}

function VerticalCard({ vertical }: { vertical: VerticalProfissional }) {
  return (
    <article className="rounded-lg border bg-card p-4 shadow-sm">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <span className="flex h-10 w-10 items-center justify-center rounded-md bg-primary/10 text-primary">
              <Stethoscope className="h-5 w-5" />
            </span>
            <div>
              <h3 className="text-lg font-semibold text-card-foreground">{vertical.nome}</h3>
              <p className="text-xs font-semibold uppercase text-muted-foreground">{vertical.release}</p>
            </div>
          </div>
          <p className="mt-3 text-sm leading-6 text-muted-foreground">{vertical.resumo}</p>
        </div>

        <span className="inline-flex w-fit items-center gap-2 rounded-md border px-3 py-1.5 text-xs font-semibold text-primary">
          <ClipboardList className="h-4 w-4" />
          {vertical.conselhoProfissional ?? "Sem conselho"}
        </span>
      </div>

      <div className="mt-4 grid gap-3 sm:grid-cols-2">
        <ListaCompacta titulo="Capacidades" itens={vertical.capacidades} limite={5} />
        <ListaCompacta titulo="Documentos" itens={vertical.documentos} limite={4} />
      </div>
    </article>
  );
}

function ListaCompacta({ titulo, itens, limite }: { titulo: string; itens: string[]; limite: number }) {
  return (
    <div className="rounded-md border bg-background p-3">
      <p className="text-sm font-semibold text-foreground">{titulo}</p>
      <ul className="mt-2 grid gap-1 text-sm text-muted-foreground">
        {itens.slice(0, limite).map((item) => (
          <li key={item} className="line-clamp-1">
            {item}
          </li>
        ))}
      </ul>
    </div>
  );
}
