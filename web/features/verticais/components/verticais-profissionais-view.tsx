"use client";

import { useEffect, useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import {
  ClipboardList,
  FileText,
  Layers3,
  LoaderCircle,
  Map,
  Route,
  Search,
  Sparkles,
  Stethoscope
} from "lucide-react";

import {
  listarVerticaisProfissionais,
  type VerticalProfissional
} from "@/features/verticais/api/verticais-client";
import { BeautyProOperacionalView } from "@/features/beauty-pro/components/beauty-pro-operacional-view";
import { MenuRapidoNutriProPreview } from "@/features/verticais/components/menu-rapido-nutri-pro-preview";
import { NutriProOperacionalView } from "@/features/nutri-pro/components/nutri-pro-operacional-view";
import { cn } from "@/lib/utils";

type AbaVertical = "visao" | "capacidades" | "documentos" | "roadmap";

const abasVerticais: Array<{ id: AbaVertical; label: string; icon: typeof Stethoscope }> = [
  { id: "visao", label: "Visão geral", icon: Sparkles },
  { id: "capacidades", label: "Capacidades", icon: Layers3 },
  { id: "documentos", label: "Documentos", icon: FileText },
  { id: "roadmap", label: "Roadmap", icon: Route }
];

export function VerticaisProfissionaisView({ empresaId }: { empresaId: string }) {
  const [busca, setBusca] = useState("");
  const [verticalSelecionadaCodigo, setVerticalSelecionadaCodigo] = useState<string | null>(null);
  const [abaAtiva, setAbaAtiva] = useState<AbaVertical>("visao");

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
      [
        vertical.nome,
        vertical.resumo,
        vertical.conselhoProfissional ?? "",
        vertical.release,
        ...vertical.publicosAtendidos,
        ...vertical.capacidades,
        ...vertical.documentos
      ]
        .join(" ")
        .toLowerCase()
        .includes(termo)
    );
  }, [busca, verticaisQuery.data]);

  useEffect(() => {
    if (!verticais.length) {
      setVerticalSelecionadaCodigo(null);
      return;
    }

    if (!verticalSelecionadaCodigo || !verticais.some((vertical) => vertical.codigo === verticalSelecionadaCodigo)) {
      setVerticalSelecionadaCodigo(verticais[0].codigo);
      setAbaAtiva("visao");
    }
  }, [verticalSelecionadaCodigo, verticais]);

  const verticalSelecionada = verticais.find((vertical) => vertical.codigo === verticalSelecionadaCodigo) ?? null;

  return (
    <section className="grid gap-4">
      <div className="flex flex-col gap-3 rounded-lg border bg-card p-4 shadow-sm lg:flex-row lg:items-end lg:justify-between">
        <div className="min-w-0">
          <p className="text-sm font-medium text-primary">Verticais profissionais</p>
          <h2 className="mt-1 text-lg font-semibold tracking-normal text-card-foreground">Catálogo modular por área</h2>
        </div>

        <label className="grid gap-1 text-sm font-medium text-card-foreground lg:w-80">
          Busca
          <span className="relative">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <input
              value={busca}
              onChange={(event) => setBusca(event.target.value)}
              className="h-10 w-full rounded-md border bg-background pl-10 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Área, conselho, capacidade"
            />
          </span>
        </label>
      </div>

      {verticaisQuery.isLoading ? (
        <div className="flex h-44 items-center justify-center rounded-lg border bg-card text-sm text-muted-foreground shadow-sm">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando verticais
        </div>
      ) : verticais.length === 0 ? (
        <div className="flex h-44 items-center justify-center rounded-lg border bg-card px-4 text-center text-sm font-medium text-muted-foreground shadow-sm">
          Nenhuma vertical encontrada
        </div>
      ) : (
        <div className="grid gap-4 xl:grid-cols-[410px_minmax(0,1fr)]">
          <div className="max-h-[720px] overflow-y-auto pr-1">
            <div className="grid gap-3">
              {verticais.map((vertical) => (
                <CardModulo
                  key={vertical.codigo}
                  vertical={vertical}
                  selecionada={vertical.codigo === verticalSelecionadaCodigo}
                  onSelecionar={() => {
                    setVerticalSelecionadaCodigo(vertical.codigo);
                    setAbaAtiva("visao");
                  }}
                />
              ))}
            </div>
          </div>

          {verticalSelecionada ? (
            <DetalheVertical vertical={verticalSelecionada} abaAtiva={abaAtiva} onAbaChange={setAbaAtiva} empresaId={empresaId} />
          ) : null}
        </div>
      )}
    </section>
  );
}

function CardModulo({
  vertical,
  selecionada,
  onSelecionar
}: {
  vertical: VerticalProfissional;
  selecionada: boolean;
  onSelecionar: () => void;
}) {
  return (
    <button
      type="button"
      aria-pressed={selecionada}
      onClick={onSelecionar}
      className={cn(
        "w-full rounded-lg border bg-card p-4 text-left shadow-sm transition-all focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
        selecionada ? "border-primary shadow-md" : "hover:border-primary/45 hover:bg-background"
      )}
    >
      <div className="flex items-start gap-3">
        <span className={cn("flex h-11 w-11 shrink-0 items-center justify-center rounded-md", selecionada ? "bg-primary text-primary-foreground" : "bg-primary/10 text-primary")}>
          <Stethoscope className="h-5 w-5" />
        </span>
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <h3 className="text-base font-semibold text-card-foreground">{vertical.nome}</h3>
            <span className="rounded-md border bg-background px-2 py-1 text-xs font-semibold text-muted-foreground">
              {vertical.release}
            </span>
          </div>
          <p className="mt-2 line-clamp-2 text-sm leading-6 text-muted-foreground">{vertical.resumo}</p>
        </div>
      </div>

      <div className="mt-3 flex flex-wrap gap-2 text-xs font-semibold">
        <span className="rounded-md bg-primary/10 px-2 py-1 text-primary">{rotuloStatus(vertical.status)}</span>
        <span className="rounded-md border bg-background px-2 py-1 text-muted-foreground">
          {vertical.conselhoProfissional ?? "Sem conselho"}
        </span>
      </div>
    </button>
  );
}

function DetalheVertical({
  vertical,
  abaAtiva,
  onAbaChange,
  empresaId
}: {
  vertical: VerticalProfissional;
  abaAtiva: AbaVertical;
  onAbaChange: (aba: AbaVertical) => void;
  empresaId: string;
}) {
  return (
    <article className="min-w-0 rounded-lg border bg-card p-4 shadow-sm">
      <div className="flex flex-col gap-4 border-b pb-4 lg:flex-row lg:items-start lg:justify-between">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <span className="flex h-11 w-11 items-center justify-center rounded-md bg-primary/10 text-primary">
              <Map className="h-5 w-5" />
            </span>
            <div>
              <h3 className="text-xl font-semibold text-card-foreground">{vertical.nome}</h3>
              <p className="text-sm font-medium text-muted-foreground">{vertical.resumo}</p>
            </div>
          </div>
        </div>
        <span className="inline-flex w-fit items-center gap-2 rounded-md border bg-background px-3 py-2 text-sm font-semibold text-primary">
          <ClipboardList className="h-4 w-4" />
          {vertical.conselhoProfissional ?? "Sem conselho"}
        </span>
      </div>

      <div className="mt-4 flex gap-2 overflow-x-auto pb-1" role="tablist" aria-label={`Detalhes de ${vertical.nome}`}>
        {abasVerticais.map((aba) => {
          const Icon = aba.icon;
          const ativa = aba.id === abaAtiva;

          return (
            <button
              key={aba.id}
              type="button"
              role="tab"
              aria-selected={ativa}
              onClick={() => onAbaChange(aba.id)}
              className={cn(
                "inline-flex h-10 shrink-0 items-center gap-2 rounded-md border px-3 text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
                ativa ? "border-primary bg-primary text-primary-foreground" : "bg-background text-muted-foreground hover:bg-muted hover:text-foreground"
              )}
            >
              <Icon className="h-4 w-4" />
              {aba.label}
            </button>
          );
        })}
      </div>

      <div className="mt-4">
        {abaAtiva === "visao" ? <AbaVisao vertical={vertical} empresaId={empresaId} /> : null}
        {abaAtiva === "capacidades" ? <ListaDetalhe titulo="Capacidades" itens={vertical.capacidades} /> : null}
        {abaAtiva === "documentos" ? <ListaDetalhe titulo="Documentos" itens={vertical.documentos} /> : null}
        {abaAtiva === "roadmap" ? <AbaRoadmap vertical={vertical} /> : null}
      </div>
    </article>
  );
}

function AbaVisao({ vertical, empresaId }: { vertical: VerticalProfissional; empresaId: string }) {
  return (
    <div className="grid gap-3 lg:grid-cols-2">
      {vertical.codigo === "NUTRI_PRO" ? (
        <div className="lg:col-span-2">
          {empresaId ? <NutriProOperacionalView empresaId={empresaId} /> : <MenuRapidoNutriProPreview />}
        </div>
      ) : null}
      {vertical.codigo === "BEAUTY_PRO" ? (
        <div className="lg:col-span-2">
          {empresaId ? <BeautyProOperacionalView empresaId={empresaId} /> : null}
        </div>
      ) : null}
      <ListaDetalhe titulo="Públicos atendidos" itens={vertical.publicosAtendidos} />
      <ListaDetalhe titulo="Integrações do núcleo" itens={vertical.integracoesNucleo} />
      <ListaDetalhe titulo="Entidades previstas" itens={vertical.entidades} />
      <div className="rounded-lg border bg-background p-4">
        <p className="text-sm font-semibold text-card-foreground">Status</p>
        <p className="mt-2 text-sm leading-6 text-muted-foreground">
          {rotuloStatus(vertical.status)} na {vertical.release}. A vertical reaproveita o núcleo comum e evolui por tasks próprias.
        </p>
      </div>
    </div>
  );
}

function AbaRoadmap({ vertical }: { vertical: VerticalProfissional }) {
  return (
    <div className="grid gap-3">
      <ListaDetalhe titulo="Próximas evoluções" itens={vertical.proximasEvolucoes} />
      <div className="rounded-lg border bg-background p-4">
        <p className="text-sm font-semibold text-card-foreground">Progressive disclosure</p>
        <p className="mt-2 text-sm leading-6 text-muted-foreground">
          O detalhe da vertical aparece somente quando selecionado. As demais áreas permanecem compactas para reduzir rolagem e clarear o contexto.
        </p>
      </div>
    </div>
  );
}

function ListaDetalhe({ titulo, itens }: { titulo: string; itens: string[] }) {
  return (
    <div className="rounded-lg border bg-background p-4">
      <p className="text-sm font-semibold text-card-foreground">{titulo}</p>
      <ul className="mt-3 grid gap-2 text-sm text-muted-foreground">
        {itens.map((item) => (
          <li key={item} className="rounded-md border bg-card px-3 py-2">
            {item}
          </li>
        ))}
      </ul>
    </div>
  );
}

function rotuloStatus(status: VerticalProfissional["status"]) {
  return {
    OPERACIONAL_BASE: "Disponível",
    PREPARADO_FUTURO: "Planejado"
  }[status];
}
