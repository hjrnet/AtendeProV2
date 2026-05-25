"use client";

import { useMemo, useState } from "react";
import {
  BookOpenText,
  CheckCircle2,
  ChevronRight,
  HelpCircle,
  LifeBuoy,
  PlayCircle,
  Search,
  Sparkles,
  Stethoscope
} from "lucide-react";

const categorias = [
  { id: "todos", label: "Todos" },
  { id: "operacao", label: "Operação" },
  { id: "precificacao", label: "Precificação" },
  { id: "verticais", label: "Verticais" },
  { id: "suporte", label: "Suporte" }
] as const;

type CategoriaAjuda = (typeof categorias)[number]["id"];

type ArtigoAjuda = {
  id: string;
  titulo: string;
  categoria: Exclude<CategoriaAjuda, "todos">;
  resumo: string;
  conteudo: string[];
  tags: string[];
  tempoLeitura: string;
};

type TutorialAjuda = {
  titulo: string;
  categoria: Exclude<CategoriaAjuda, "todos">;
  passos: string[];
};

const artigos: ArtigoAjuda[] = [
  {
    id: "primeiros-passos",
    titulo: "Primeiros passos no AtendePro",
    categoria: "operacao",
    resumo: "Organize empresa, equipe, serviços e rotina operacional antes de aprofundar as verticais.",
    conteudo: [
      "Comece selecionando a empresa ativa no topo do app e confirme se os dados do tenant estão corretos.",
      "Cadastre serviços, custos, profissionais e clientes para que dashboard, agenda e precificação trabalhem com dados consistentes.",
      "Use a busca global para localizar informações operacionais sem trocar de área."
    ],
    tags: ["empresa", "equipe", "operação"],
    tempoLeitura: "4 min"
  },
  {
    id: "precificacao-alertas",
    titulo: "Entendendo alertas de precificação",
    categoria: "precificacao",
    resumo: "Saiba interpretar margem saudável, margem baixa e prejuízo sem alterar a fórmula financeira.",
    conteudo: [
      "Simulações saudáveis indicam preço praticado compatível com custo real e margem desejada.",
      "Margem baixa exige revisão comercial, porque o preço cobre custo, mas fica abaixo da recomendação.",
      "Prejuízo indica venda abaixo do custo total estimado e deve ser tratado com prioridade."
    ],
    tags: ["preço", "margem", "custo real"],
    tempoLeitura: "5 min"
  },
  {
    id: "nutri-pro-visao",
    titulo: "Visão geral do Nutri Pro",
    categoria: "verticais",
    resumo: "Centralize pacientes, plano alimentar, avaliações, exames e documentos nutricionais.",
    conteudo: [
      "O Nutri Pro reaproveita clientes, agenda, documentos, custos e relatórios do núcleo comum.",
      "O módulo evolui para plano alimentar, refeições, gasto energético, exames, prescrições e app do paciente.",
      "Documentos oficiais devem usar dados profissionais, CRN e carimbo configurado."
    ],
    tags: ["nutrição", "paciente", "documentos"],
    tempoLeitura: "6 min"
  },
  {
    id: "abrir-chamado",
    titulo: "Quando abrir um chamado",
    categoria: "suporte",
    resumo: "Use chamados para dúvidas, incidentes, solicitações operacionais e acompanhamento com histórico.",
    conteudo: [
      "Abra um chamado quando a equipe precisar de histórico, prioridade e acompanhamento formal.",
      "Inclua contexto, empresa, tela afetada e impacto no atendimento para acelerar a triagem.",
      "Acompanhe status e mensagens pelo painel Admin SaaS > Suporte."
    ],
    tags: ["suporte", "prioridade", "histórico"],
    tempoLeitura: "3 min"
  }
];

const perguntasFrequentes = [
  {
    pergunta: "A Central de Ajuda substitui o suporte?",
    resposta: "Não. Ela resolve dúvidas recorrentes e reduz atrito, mas chamados continuam disponíveis para casos que precisam de acompanhamento."
  },
  {
    pergunta: "Os artigos são iguais para todas as empresas?",
    resposta: "Nesta fase, sim. Futuramente a base poderá variar por plano, vertical profissional e perfil de usuário."
  },
  {
    pergunta: "Posso usar os tutoriais durante uma apresentação?",
    resposta: "Sim. Os tutoriais foram escritos para orientar fluxos principais de forma curta e profissional."
  }
];

const tutoriais: TutorialAjuda[] = [
  {
    titulo: "Preparar uma empresa demo",
    categoria: "operacao",
    passos: ["Selecionar a empresa ativa", "Conferir serviços e custos", "Validar dashboard e busca global"]
  },
  {
    titulo: "Revisar uma simulação de preço",
    categoria: "precificacao",
    passos: ["Abrir Precificação", "Conferir custo total", "Comparar preço praticado e recomendado"]
  },
  {
    titulo: "Triar um chamado",
    categoria: "suporte",
    passos: ["Abrir Admin SaaS > Suporte", "Filtrar por prioridade", "Responder e ajustar status"]
  }
];

export function CentralAjudaView() {
  const [busca, setBusca] = useState("");
  const [categoriaAtiva, setCategoriaAtiva] = useState<CategoriaAjuda>("todos");
  const [artigoSelecionadoId, setArtigoSelecionadoId] = useState(artigos[0].id);

  const artigosFiltrados = useMemo(() => {
    const termo = busca.trim().toLowerCase();
    return artigos.filter((artigo) => {
      const combinaCategoria = categoriaAtiva === "todos" || artigo.categoria === categoriaAtiva;
      const combinaBusca =
        !termo ||
        artigo.titulo.toLowerCase().includes(termo) ||
        artigo.resumo.toLowerCase().includes(termo) ||
        artigo.tags.some((tag) => tag.toLowerCase().includes(termo));
      return combinaCategoria && combinaBusca;
    });
  }, [busca, categoriaAtiva]);

  const artigoSelecionado =
    artigosFiltrados.find((artigo) => artigo.id === artigoSelecionadoId) ?? artigosFiltrados[0] ?? artigos[0];
  const tutoriaisFiltrados = tutoriais.filter((tutorial) => categoriaAtiva === "todos" || tutorial.categoria === categoriaAtiva);

  function selecionarCategoria(categoria: CategoriaAjuda) {
    setCategoriaAtiva(categoria);
    const primeiroArtigo = artigos.find((artigo) => categoria === "todos" || artigo.categoria === categoria);
    if (primeiroArtigo) {
      setArtigoSelecionadoId(primeiroArtigo.id);
    }
  }

  return (
    <section className="grid gap-4 xl:grid-cols-[minmax(0,430px)_minmax(0,1fr)]">
      <div className="min-w-0 rounded-lg border bg-card p-4 shadow-sm">
        <div className="border-b pb-4">
          <p className="text-sm font-medium text-primary">Central de ajuda</p>
          <h2 className="mt-1 text-xl font-semibold text-card-foreground">Artigos, FAQ e tutoriais</h2>
          <p className="mt-1 text-sm leading-6 text-muted-foreground">
            Conteúdo curto para orientar operação, precificação, verticais e suporte.
          </p>
        </div>

        <label className="relative mt-4 block">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <input
            value={busca}
            onChange={(event) => setBusca(event.target.value)}
            className="h-10 w-full rounded-md border bg-background pl-10 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
            placeholder="Buscar artigo, tema ou tag"
          />
        </label>

        <div className="mt-3 flex gap-2 overflow-x-auto pb-1" aria-label="Categorias da central de ajuda">
          {categorias.map((categoria) => (
            <button
              key={categoria.id}
              type="button"
              onClick={() => selecionarCategoria(categoria.id)}
              className={`h-9 shrink-0 rounded-md border px-3 text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring ${
                categoriaAtiva === categoria.id ? "border-primary bg-primary text-primary-foreground" : "bg-background text-card-foreground"
              }`}
            >
              {categoria.label}
            </button>
          ))}
        </div>

        <div className="mt-4 max-h-[560px] overflow-y-auto pr-1">
          {artigosFiltrados.length === 0 ? (
            <EstadoAjuda icon={HelpCircle} titulo="Nenhum artigo encontrado" />
          ) : (
            <div className="grid gap-3">
              {artigosFiltrados.map((artigo) => (
                <button
                  key={artigo.id}
                  type="button"
                  onClick={() => setArtigoSelecionadoId(artigo.id)}
                  className={`rounded-lg border bg-background p-4 text-left transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring ${
                    artigo.id === artigoSelecionado.id ? "border-primary shadow-sm" : "hover:border-primary/50"
                  }`}
                >
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0">
                      <p className="text-sm font-semibold text-card-foreground">{artigo.titulo}</p>
                      <p className="mt-1 line-clamp-2 text-xs leading-5 text-muted-foreground">{artigo.resumo}</p>
                    </div>
                    <ChevronRight className="h-4 w-4 shrink-0 text-muted-foreground" />
                  </div>
                  <div className="mt-3 flex flex-wrap gap-2">
                    <BadgeAjuda>{rotuloCategoria(artigo.categoria)}</BadgeAjuda>
                    <BadgeAjuda>{artigo.tempoLeitura}</BadgeAjuda>
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>
      </div>

      <div className="grid min-w-0 gap-4">
        <article className="rounded-lg border bg-card p-4 shadow-sm">
          <div className="flex flex-col gap-3 border-b pb-4 lg:flex-row lg:items-start lg:justify-between">
            <div>
              <p className="inline-flex items-center gap-2 text-sm font-medium text-primary">
                <BookOpenText className="h-4 w-4" />
                Artigo selecionado
              </p>
              <h2 className="mt-1 text-xl font-semibold text-card-foreground">{artigoSelecionado.titulo}</h2>
              <p className="mt-2 text-sm leading-6 text-muted-foreground">{artigoSelecionado.resumo}</p>
            </div>
            <div className="flex shrink-0 flex-wrap gap-2">
              <BadgeAjuda>{rotuloCategoria(artigoSelecionado.categoria)}</BadgeAjuda>
              <BadgeAjuda>{artigoSelecionado.tempoLeitura}</BadgeAjuda>
            </div>
          </div>

          <div className="mt-4 grid gap-3">
            {artigoSelecionado.conteudo.map((paragrafo) => (
              <p key={paragrafo} className="rounded-md border bg-background px-3 py-2 text-sm leading-6 text-muted-foreground">
                {paragrafo}
              </p>
            ))}
          </div>

          <div className="mt-4 flex flex-wrap gap-2">
            {artigoSelecionado.tags.map((tag) => (
              <span key={tag} className="rounded-md bg-primary/10 px-2 py-1 text-xs font-semibold text-primary">
                {tag}
              </span>
            ))}
          </div>
        </article>

        <div className="grid gap-4 lg:grid-cols-[minmax(0,1fr)_minmax(0,1fr)]">
          <section className="rounded-lg border bg-card p-4 shadow-sm">
            <p className="inline-flex items-center gap-2 text-sm font-medium text-primary">
              <HelpCircle className="h-4 w-4" />
              FAQ
            </p>
            <div className="mt-3 grid gap-2">
              {perguntasFrequentes.map((item) => (
                <details key={item.pergunta} className="rounded-md border bg-background px-3 py-2">
                  <summary className="cursor-pointer text-sm font-semibold text-card-foreground">{item.pergunta}</summary>
                  <p className="mt-2 text-sm leading-6 text-muted-foreground">{item.resposta}</p>
                </details>
              ))}
            </div>
          </section>

          <section className="rounded-lg border bg-card p-4 shadow-sm">
            <p className="inline-flex items-center gap-2 text-sm font-medium text-primary">
              <PlayCircle className="h-4 w-4" />
              Tutoriais rápidos
            </p>
            <div className="mt-3 grid gap-3">
              {tutoriaisFiltrados.map((tutorial) => (
                <article key={tutorial.titulo} className="rounded-md border bg-background p-3">
                  <div className="flex items-center justify-between gap-3">
                    <h3 className="text-sm font-semibold text-card-foreground">{tutorial.titulo}</h3>
                    <BadgeAjuda>{rotuloCategoria(tutorial.categoria)}</BadgeAjuda>
                  </div>
                  <ol className="mt-3 grid gap-2">
                    {tutorial.passos.map((passo, index) => (
                      <li key={passo} className="flex gap-2 text-sm leading-6 text-muted-foreground">
                        <span className="flex h-6 w-6 shrink-0 items-center justify-center rounded-md bg-primary/10 text-xs font-bold text-primary">
                          {index + 1}
                        </span>
                        {passo}
                      </li>
                    ))}
                  </ol>
                </article>
              ))}
            </div>
          </section>
        </div>

        <section className="grid gap-3 rounded-lg border bg-card p-4 shadow-sm sm:grid-cols-3">
          <IndicadorAjuda icon={Sparkles} rotulo="Artigos ativos" valor={String(artigos.length)} />
          <IndicadorAjuda icon={Stethoscope} rotulo="Categorias" valor={String(categorias.length - 1)} />
          <IndicadorAjuda icon={LifeBuoy} rotulo="Tutoriais" valor={String(tutoriais.length)} />
        </section>
      </div>
    </section>
  );
}

function BadgeAjuda({ children }: { children: string }) {
  return <span className="rounded-md border bg-card px-2 py-1 text-xs font-semibold text-muted-foreground">{children}</span>;
}

function IndicadorAjuda({ icon: Icon, rotulo, valor }: { icon: typeof Sparkles; rotulo: string; valor: string }) {
  return (
    <div className="rounded-md border bg-background p-3">
      <Icon className="h-5 w-5 text-primary" />
      <p className="mt-2 text-xs font-medium uppercase text-muted-foreground">{rotulo}</p>
      <p className="mt-1 text-lg font-semibold text-card-foreground">{valor}</p>
    </div>
  );
}

function EstadoAjuda({ icon: Icon, titulo }: { icon: typeof HelpCircle; titulo: string }) {
  return (
    <div className="flex min-h-44 flex-col items-center justify-center rounded-lg border bg-background p-6 text-center">
      <Icon className="h-8 w-8 text-primary" />
      <p className="mt-3 text-sm font-semibold text-card-foreground">{titulo}</p>
    </div>
  );
}

function rotuloCategoria(categoria: Exclude<CategoriaAjuda, "todos">) {
  return categorias.find((item) => item.id === categoria)?.label ?? categoria;
}
