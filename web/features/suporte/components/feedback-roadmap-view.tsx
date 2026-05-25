"use client";

import { useMemo, useState, type ReactNode } from "react";
import {
  CheckCircle2,
  CircleDot,
  ClipboardList,
  Flame,
  Lightbulb,
  MessageSquarePlus,
  Rocket,
  Search,
  ThumbsUp,
  TrendingUp
} from "lucide-react";

import { Button } from "@/components/ui/button";

type StatusRoadmap = "PLANEJADO" | "DESCOBERTA" | "EM_DESENVOLVIMENTO" | "ENTREGUE";
type AreaFeedback = "Operação" | "Precificação" | "Nutri Pro" | "Admin SaaS" | "Mobile";

type PedidoFeedback = {
  id: string;
  titulo: string;
  descricao: string;
  area: AreaFeedback;
  impacto: "Baixo" | "Médio" | "Alto";
  votos: number;
  status: StatusRoadmap;
};

const pedidosIniciais: PedidoFeedback[] = [
  {
    id: "feedback-alertas-precificacao",
    titulo: "Filtro rápido para simulações em prejuízo",
    descricao: "Destacar procedimentos que vendem abaixo do custo e permitir ação rápida.",
    area: "Precificação",
    impacto: "Alto",
    votos: 18,
    status: "EM_DESENVOLVIMENTO"
  },
  {
    id: "feedback-menu-nutri",
    titulo: "Menu rápido no prontuário Nutri Pro",
    descricao: "Acesso direto a gastos energéticos, exames e plano alimentar.",
    area: "Nutri Pro",
    impacto: "Alto",
    votos: 21,
    status: "PLANEJADO"
  },
  {
    id: "feedback-app-paciente",
    titulo: "App do paciente com lembretes",
    descricao: "Lembretes de refeições, hidratação e acompanhamento de metas.",
    area: "Mobile",
    impacto: "Médio",
    votos: 13,
    status: "DESCOBERTA"
  },
  {
    id: "feedback-suporte-roadmap",
    titulo: "Roadmap visível no Admin SaaS",
    descricao: "Organizar pedidos e mostrar andamento para a equipe comercial.",
    area: "Admin SaaS",
    impacto: "Médio",
    votos: 9,
    status: "ENTREGUE"
  }
];

const colunasRoadmap: Array<{ status: StatusRoadmap; label: string; icon: typeof ClipboardList }> = [
  { status: "PLANEJADO", label: "Planejado", icon: ClipboardList },
  { status: "DESCOBERTA", label: "Descoberta", icon: Lightbulb },
  { status: "EM_DESENVOLVIMENTO", label: "Em desenvolvimento", icon: Rocket },
  { status: "ENTREGUE", label: "Entregue", icon: CheckCircle2 }
];

const areas: Array<"Todas" | AreaFeedback> = ["Todas", "Operação", "Precificação", "Nutri Pro", "Admin SaaS", "Mobile"];

export function FeedbackRoadmapView() {
  const [pedidos, setPedidos] = useState(pedidosIniciais);
  const [busca, setBusca] = useState("");
  const [areaAtiva, setAreaAtiva] = useState<"Todas" | AreaFeedback>("Todas");
  const [titulo, setTitulo] = useState("");
  const [descricao, setDescricao] = useState("");
  const [area, setArea] = useState<AreaFeedback>("Operação");
  const [erro, setErro] = useState<string | null>(null);

  const pedidosFiltrados = useMemo(() => {
    const termo = busca.trim().toLowerCase();
    return pedidos.filter((pedido) => {
      const combinaArea = areaAtiva === "Todas" || pedido.area === areaAtiva;
      const combinaBusca =
        !termo ||
        pedido.titulo.toLowerCase().includes(termo) ||
        pedido.descricao.toLowerCase().includes(termo) ||
        pedido.area.toLowerCase().includes(termo);
      return combinaArea && combinaBusca;
    });
  }, [areaAtiva, busca, pedidos]);

  const pedidosOrdenados = [...pedidosFiltrados].sort((a, b) => b.votos - a.votos);
  const totalVotos = pedidos.reduce((total, pedido) => total + pedido.votos, 0);
  const totalAltoImpacto = pedidos.filter((pedido) => pedido.impacto === "Alto").length;

  function votar(pedidoId: string) {
    setPedidos((atuais) => atuais.map((pedido) => (pedido.id === pedidoId ? { ...pedido, votos: pedido.votos + 1 } : pedido)));
  }

  function enviarPedido() {
    if (!titulo.trim() || !descricao.trim()) {
      setErro("Informe título e descrição para registrar o pedido.");
      return;
    }
    const novoPedido: PedidoFeedback = {
      id: `feedback-${Date.now()}`,
      titulo: titulo.trim(),
      descricao: descricao.trim(),
      area,
      impacto: "Médio",
      votos: 1,
      status: "DESCOBERTA"
    };
    setPedidos((atuais) => [novoPedido, ...atuais]);
    setTitulo("");
    setDescricao("");
    setArea("Operação");
    setErro(null);
  }

  return (
    <section className="grid gap-4 xl:grid-cols-[minmax(0,430px)_minmax(0,1fr)]">
      <div className="min-w-0 rounded-lg border bg-card p-4 shadow-sm">
        <div className="border-b pb-4">
          <p className="text-sm font-medium text-primary">Feedback e roadmap</p>
          <h2 className="mt-1 text-xl font-semibold text-card-foreground">Pedidos de melhoria</h2>
          <p className="mt-1 text-sm leading-6 text-muted-foreground">
            Reúna sugestões, impacto e votos para orientar a priorização do produto.
          </p>
        </div>

        <div className="mt-4 grid gap-3">
          <label className="relative">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <input
              value={busca}
              onChange={(event) => setBusca(event.target.value)}
              className="h-10 w-full rounded-md border bg-background pl-10 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Buscar pedido, área ou prioridade"
            />
          </label>

          <div className="flex gap-2 overflow-x-auto pb-1" aria-label="Áreas de feedback">
            {areas.map((item) => (
              <button
                key={item}
                type="button"
                onClick={() => setAreaAtiva(item)}
                className={`h-9 shrink-0 rounded-md border px-3 text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring ${
                  areaAtiva === item ? "border-primary bg-primary text-primary-foreground" : "bg-background text-card-foreground"
                }`}
              >
                {item}
              </button>
            ))}
          </div>
        </div>

        <div className="mt-4 max-h-[520px] overflow-y-auto pr-1">
          {pedidosOrdenados.length === 0 ? (
            <EstadoRoadmap titulo="Nenhum pedido encontrado" />
          ) : (
            <div className="grid gap-3">
              {pedidosOrdenados.map((pedido) => (
                <article key={pedido.id} className="rounded-lg border bg-background p-4">
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0">
                      <div className="flex flex-wrap items-center gap-2">
                        <h3 className="text-sm font-semibold text-card-foreground">{pedido.titulo}</h3>
                        <BadgeImpacto impacto={pedido.impacto} />
                      </div>
                      <p className="mt-2 line-clamp-2 text-xs leading-5 text-muted-foreground">{pedido.descricao}</p>
                    </div>
                    <button
                      type="button"
                      onClick={() => votar(pedido.id)}
                      className="inline-flex shrink-0 items-center gap-1 rounded-md border bg-card px-2 py-1 text-xs font-semibold text-card-foreground transition-colors hover:border-primary focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
                      title="Votar neste pedido"
                    >
                      <ThumbsUp className="h-3.5 w-3.5 text-primary" />
                      {pedido.votos}
                    </button>
                  </div>
                  <div className="mt-3 flex flex-wrap gap-2">
                    <BadgeNeutro>{pedido.area}</BadgeNeutro>
                    <BadgeNeutro>{rotuloStatus(pedido.status)}</BadgeNeutro>
                  </div>
                </article>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="grid min-w-0 gap-4">
        <section className="grid gap-3 rounded-lg border bg-card p-4 shadow-sm sm:grid-cols-3">
          <IndicadorRoadmap icon={MessageSquarePlus} rotulo="Pedidos" valor={String(pedidos.length)} />
          <IndicadorRoadmap icon={ThumbsUp} rotulo="Votos" valor={String(totalVotos)} />
          <IndicadorRoadmap icon={Flame} rotulo="Alto impacto" valor={String(totalAltoImpacto)} />
        </section>

        <section className="rounded-lg border bg-card p-4 shadow-sm">
          <div className="flex flex-col gap-3 border-b pb-4 md:flex-row md:items-start md:justify-between">
            <div>
              <p className="inline-flex items-center gap-2 text-sm font-medium text-primary">
                <CircleDot className="h-4 w-4" />
                Roadmap
              </p>
              <h2 className="mt-1 text-xl font-semibold text-card-foreground">Priorização por status</h2>
              <p className="mt-1 text-sm leading-6 text-muted-foreground">Acompanhe o funil de ideias até a entrega.</p>
            </div>
            <BadgeNeutro>{pedidos.filter((pedido) => pedido.status !== "ENTREGUE").length} em andamento</BadgeNeutro>
          </div>

          <div className="mt-4 grid gap-3 lg:grid-cols-2 2xl:grid-cols-4">
            {colunasRoadmap.map((coluna) => {
              const Icon = coluna.icon;
              const itens = pedidos.filter((pedido) => pedido.status === coluna.status);

              return (
                <div key={coluna.status} className="rounded-lg border bg-background p-3">
                  <div className="flex items-center justify-between gap-2">
                    <p className="inline-flex items-center gap-2 text-sm font-semibold text-card-foreground">
                      <Icon className="h-4 w-4 text-primary" />
                      {coluna.label}
                    </p>
                    <span className="rounded-md bg-primary/10 px-2 py-1 text-xs font-semibold text-primary">{itens.length}</span>
                  </div>
                  <div className="mt-3 grid gap-2">
                    {itens.length === 0 ? (
                      <p className="rounded-md border bg-card px-3 py-2 text-xs font-medium text-muted-foreground">Sem pedidos neste status.</p>
                    ) : (
                      itens.map((pedido) => (
                        <article key={pedido.id} className="rounded-md border bg-card p-3">
                          <p className="text-sm font-semibold text-card-foreground">{pedido.titulo}</p>
                          <p className="mt-1 text-xs text-muted-foreground">{pedido.area}</p>
                        </article>
                      ))
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </section>

        <aside className="rounded-lg border bg-card p-4 shadow-sm">
          <div className="mb-4 border-b pb-4">
            <p className="inline-flex items-center gap-2 text-sm font-medium text-primary">
              <TrendingUp className="h-4 w-4" />
              Novo pedido
            </p>
            <h2 className="mt-1 text-lg font-semibold text-card-foreground">Registrar melhoria</h2>
          </div>

          <div className="grid gap-3">
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Título
              <input
                value={titulo}
                onChange={(event) => setTitulo(event.target.value)}
                className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                placeholder="Ex.: Melhorar exportação de relatórios"
              />
            </label>
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Área
              <select
                value={area}
                onChange={(event) => setArea(event.target.value as AreaFeedback)}
                className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              >
                {areas.filter((item) => item !== "Todas").map((item) => (
                  <option key={item} value={item}>
                    {item}
                  </option>
                ))}
              </select>
            </label>
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Descrição
              <textarea
                value={descricao}
                onChange={(event) => setDescricao(event.target.value)}
                className="min-h-24 rounded-md border bg-background px-3 py-2 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                placeholder="Descreva o problema, impacto e resultado esperado."
              />
            </label>
            {erro ? <div className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{erro}</div> : null}
            <Button type="button" onClick={enviarPedido}>
              <MessageSquarePlus className="h-4 w-4" />
              Registrar pedido
            </Button>
          </div>
        </aside>
      </div>
    </section>
  );
}

function BadgeImpacto({ impacto }: { impacto: PedidoFeedback["impacto"] }) {
  const className = {
    Baixo: "border-emerald-200 bg-emerald-50 text-emerald-800",
    Médio: "border-amber-200 bg-amber-50 text-amber-800",
    Alto: "border-red-200 bg-red-50 text-red-700"
  }[impacto];

  return <span className={`rounded-md border px-2 py-1 text-xs font-semibold ${className}`}>{impacto} impacto</span>;
}

function BadgeNeutro({ children }: { children: ReactNode }) {
  return <span className="rounded-md border bg-card px-2 py-1 text-xs font-semibold text-muted-foreground">{children}</span>;
}

function IndicadorRoadmap({ icon: Icon, rotulo, valor }: { icon: typeof MessageSquarePlus; rotulo: string; valor: string }) {
  return (
    <div className="rounded-md border bg-background p-3">
      <Icon className="h-5 w-5 text-primary" />
      <p className="mt-2 text-xs font-medium uppercase text-muted-foreground">{rotulo}</p>
      <p className="mt-1 text-lg font-semibold text-card-foreground">{valor}</p>
    </div>
  );
}

function EstadoRoadmap({ titulo }: { titulo: string }) {
  return (
    <div className="flex min-h-44 flex-col items-center justify-center rounded-lg border bg-background p-6 text-center">
      <Lightbulb className="h-8 w-8 text-primary" />
      <p className="mt-3 text-sm font-semibold text-card-foreground">{titulo}</p>
    </div>
  );
}

function rotuloStatus(status: StatusRoadmap) {
  return colunasRoadmap.find((coluna) => coluna.status === status)?.label ?? status;
}
