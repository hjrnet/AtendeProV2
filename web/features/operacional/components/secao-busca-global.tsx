"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { LoaderCircle, Search } from "lucide-react";

import { buscarGlobal, type ResultadoBuscaGlobal } from "@/features/operacional/api/operacional-client";

type SecaoBuscaGlobalProps = {
  empresaId: string;
};

const categoriasBusca = ["", "NUTRI", "GERAL", "Insumos", "Esterilizacao"];
const statusBusca = ["", "ATIVO", "INATIVO", "AGENDADO", "CONFIRMADO", "REALIZADO"];

export function SecaoBuscaGlobal({ empresaId }: SecaoBuscaGlobalProps) {
  const [busca, setBusca] = useState("");
  const [categoria, setCategoria] = useState("");
  const [status, setStatus] = useState("ATIVO");

  const buscaQuery = useQuery({
    queryKey: ["busca-global", empresaId, busca, categoria, status],
    queryFn: () =>
      buscarGlobal({
        empresaId,
        busca,
        categoria,
        status,
        limitePorTipo: 4
      }),
    enabled: Boolean(empresaId) && (busca.trim().length >= 2 || Boolean(categoria) || Boolean(status))
  });

  const resultados = buscaQuery.data?.itens ?? [];

  return (
    <section className="rounded-lg border bg-card p-4 shadow-sm">
      <div className="flex flex-col gap-3 border-b pb-4 xl:flex-row xl:items-end">
        <label className="grid min-w-0 flex-1 gap-1 text-sm font-medium text-card-foreground">
          Busca
          <span className="relative">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <input
              value={busca}
              onChange={(event) => setBusca(event.target.value)}
              className="h-10 w-full rounded-md border bg-background pl-10 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Cliente, procedimento, lote, equipamento"
            />
          </span>
        </label>

        <div className="grid grid-cols-2 gap-3 xl:w-[430px]">
          <FiltroSelect label="Categoria" value={categoria} onChange={setCategoria} options={categoriasBusca} />
          <FiltroSelect label="Status" value={status} onChange={setStatus} options={statusBusca} />
        </div>
      </div>

      <div className="mt-4 max-h-[640px] overflow-y-auto pr-1">
        {buscaQuery.isFetching ? (
          <div className="flex h-36 items-center justify-center rounded-lg border bg-background text-sm text-muted-foreground">
            <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
            Buscando
          </div>
        ) : resultados.length === 0 ? (
          <div className="flex h-36 items-center justify-center rounded-lg border bg-background px-4 text-center text-sm font-medium text-muted-foreground">
            Nenhum resultado operacional
          </div>
        ) : (
          <div className="grid gap-3">
            {resultados.map((resultado) => (
              <ResultadoBuscaCard key={`${resultado.tipo}-${resultado.id}`} resultado={resultado} />
            ))}
          </div>
        )}
      </div>
    </section>
  );
}

function FiltroSelect({
  label,
  value,
  onChange,
  options
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
  options: string[];
}) {
  return (
    <label className="grid gap-1 text-sm font-medium text-card-foreground">
      {label}
      <select
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="h-10 min-w-0 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
      >
        {options.map((option) => (
          <option key={option || "todos"} value={option}>
            {option || "Todos"}
          </option>
        ))}
      </select>
    </label>
  );
}

function ResultadoBuscaCard({ resultado }: { resultado: ResultadoBuscaGlobal }) {
  return (
    <article className="rounded-lg border bg-background p-4">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="text-base font-semibold text-card-foreground">{resultado.titulo}</h3>
            <span className="rounded-md bg-primary/10 px-2 py-1 text-xs font-semibold text-primary">
              {rotuloTipo(resultado.tipo)}
            </span>
          </div>
          <p className="mt-2 line-clamp-2 text-sm text-muted-foreground">{resultado.descricao || resultado.categoria}</p>
        </div>
        <div className="flex shrink-0 flex-wrap gap-2 text-xs font-semibold">
          <span className="rounded-md border bg-card px-2 py-1 text-muted-foreground">{resultado.categoria}</span>
          <span className="rounded-md border bg-card px-2 py-1 text-muted-foreground">{resultado.status}</span>
        </div>
      </div>
    </article>
  );
}

function rotuloTipo(tipo: ResultadoBuscaGlobal["tipo"]) {
  return {
    CLIENTE_PACIENTE: "Cliente",
    COMPROMISSO_AGENDA: "Agenda",
    SERVICO_PROCEDIMENTO: "Servico",
    CUSTO: "Custo",
    PRODUTO_ESTOQUE: "Estoque",
    EQUIPAMENTO: "Equipamento"
  }[tipo];
}
