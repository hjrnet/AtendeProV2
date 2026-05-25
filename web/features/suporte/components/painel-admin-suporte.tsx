"use client";

import { useEffect, useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  AlertTriangle,
  CheckCircle2,
  ChevronLeft,
  ChevronRight,
  Clock3,
  Inbox,
  LoaderCircle,
  MessageSquareText,
  Search,
  Send,
  ShieldCheck
} from "lucide-react";

import { Button } from "@/components/ui/button";
import { ApiError } from "@/lib/api";
import type { SessaoAutenticada } from "@/features/auth/lib/auth-storage";
import {
  atualizarTriagemChamadoSuporte,
  detalharChamadoSuporte,
  listarChamadosSuporte,
  registrarMensagemChamadoSuporte,
  type ChamadoSuporte,
  type PrioridadeChamadoSuporte,
  type StatusChamadoSuporte
} from "@/features/suporte/api/suporte-client";

const TAMANHO_PAGINA = 12;
const STATUS: Array<{ valor: "" | StatusChamadoSuporte; label: string }> = [
  { valor: "", label: "Todos" },
  { valor: "ABERTO", label: "Aberto" },
  { valor: "EM_ATENDIMENTO", label: "Em atendimento" },
  { valor: "AGUARDANDO_CLIENTE", label: "Aguardando cliente" },
  { valor: "RESOLVIDO", label: "Resolvido" },
  { valor: "CANCELADO", label: "Cancelado" }
];
const PRIORIDADES: Array<{ valor: "" | PrioridadeChamadoSuporte; label: string }> = [
  { valor: "", label: "Todas" },
  { valor: "BAIXA", label: "Baixa" },
  { valor: "MEDIA", label: "Média" },
  { valor: "ALTA", label: "Alta" },
  { valor: "CRITICA", label: "Crítica" }
];

type PainelAdminSuporteProps = {
  empresaId: string;
  sessao: SessaoAutenticada;
};

export function PainelAdminSuporte({ empresaId, sessao }: PainelAdminSuporteProps) {
  const queryClient = useQueryClient();
  const [busca, setBusca] = useState("");
  const [status, setStatus] = useState<"" | StatusChamadoSuporte>("");
  const [prioridade, setPrioridade] = useState<"" | PrioridadeChamadoSuporte>("");
  const [pagina, setPagina] = useState(0);
  const [chamadoSelecionadoId, setChamadoSelecionadoId] = useState<string | null>(null);
  const [resposta, setResposta] = useState("");
  const [erro, setErro] = useState<string | null>(null);

  const chamadosQuery = useQuery({
    queryKey: ["suporte-chamados", empresaId, pagina, busca, status, prioridade],
    queryFn: () => listarChamadosSuporte({ empresaId, pagina, tamanho: TAMANHO_PAGINA, busca, status, prioridade }),
    enabled: Boolean(empresaId)
  });

  const chamados = chamadosQuery.data?.itens ?? [];
  const chamadoSelecionado = useMemo(
    () => chamados.find((chamado) => chamado.id === chamadoSelecionadoId) ?? null,
    [chamados, chamadoSelecionadoId]
  );

  useEffect(() => {
    if (!chamadoSelecionadoId && chamados.length > 0) {
      setChamadoSelecionadoId(chamados[0].id);
    }
  }, [chamados, chamadoSelecionadoId]);

  useEffect(() => {
    if (chamadoSelecionadoId && chamados.length > 0 && !chamados.some((chamado) => chamado.id === chamadoSelecionadoId)) {
      setChamadoSelecionadoId(chamados[0].id);
    }
  }, [chamados, chamadoSelecionadoId]);

  const detalheQuery = useQuery({
    queryKey: ["suporte-chamado-detalhe", chamadoSelecionadoId],
    queryFn: () => detalharChamadoSuporte(chamadoSelecionadoId ?? ""),
    enabled: Boolean(chamadoSelecionadoId)
  });

  const atualizarTriagemMutation = useMutation({
    mutationFn: (valores: { status?: StatusChamadoSuporte; prioridade?: PrioridadeChamadoSuporte }) =>
      atualizarTriagemChamadoSuporte(chamadoSelecionadoId ?? "", valores),
    onSuccess: async () => {
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["suporte-chamados"] }),
        queryClient.invalidateQueries({ queryKey: ["suporte-chamado-detalhe", chamadoSelecionadoId] })
      ]);
      setErro(null);
    },
    onError: (error) => setErro(error instanceof ApiError ? error.message : "Não foi possível atualizar o chamado.")
  });

  const registrarMensagemMutation = useMutation({
    mutationFn: () =>
      registrarMensagemChamadoSuporte(chamadoSelecionadoId ?? "", {
        autorUsuarioId: sessao.usuario.id,
        autorNome: sessao.usuario.nome,
        origem: "SUPORTE",
        mensagem: resposta
      }),
    onSuccess: async () => {
      setResposta("");
      setErro(null);
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["suporte-chamados"] }),
        queryClient.invalidateQueries({ queryKey: ["suporte-chamado-detalhe", chamadoSelecionadoId] })
      ]);
    },
    onError: (error) => setErro(error instanceof ApiError ? error.message : "Não foi possível enviar a resposta.")
  });

  const totalPaginas = chamadosQuery.data?.totalPaginas ?? 0;
  const podeVoltar = pagina > 0;
  const podeAvancar = totalPaginas > 0 && pagina + 1 < totalPaginas;
  const detalhe = detalheQuery.data;
  const chamadoAtual = detalhe?.chamado ?? chamadoSelecionado;

  function atualizarFiltroStatus(valor: "" | StatusChamadoSuporte) {
    setStatus(valor);
    setPagina(0);
  }

  function atualizarFiltroPrioridade(valor: "" | PrioridadeChamadoSuporte) {
    setPrioridade(valor);
    setPagina(0);
  }

  function enviarResposta() {
    if (!resposta.trim()) {
      setErro("Escreva uma resposta antes de enviar.");
      return;
    }
    registrarMensagemMutation.mutate();
  }

  if (!empresaId) {
    return (
      <div className="rounded-lg border bg-card p-6 text-center shadow-sm">
        <ShieldCheck className="mx-auto h-9 w-9 text-primary" />
        <h2 className="mt-3 text-lg font-semibold text-card-foreground">Selecione uma empresa</h2>
        <p className="mt-1 text-sm leading-6 text-muted-foreground">
          O painel de suporte usa o contexto da empresa ativa para listar chamados com segurança.
        </p>
      </div>
    );
  }

  return (
    <section className="grid gap-4 xl:grid-cols-[minmax(0,440px)_minmax(0,1fr)]">
      <div className="min-w-0 rounded-lg border bg-card p-4 shadow-sm">
        <div className="flex flex-col gap-3 border-b pb-4">
          <div className="flex items-start justify-between gap-3">
            <div>
              <p className="text-sm font-medium text-primary">Admin SaaS</p>
              <h2 className="mt-1 text-xl font-semibold text-card-foreground">Caixa de entrada</h2>
              <p className="mt-1 text-sm leading-6 text-muted-foreground">Priorize chamados por status, gravidade e atualização.</p>
            </div>
            <span className="rounded-md border bg-background px-3 py-2 text-sm font-semibold text-card-foreground">
              {chamadosQuery.data?.totalItens ?? 0} chamados
            </span>
          </div>

          <label className="relative">
            <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <input
              value={busca}
              onChange={(event) => {
                setBusca(event.target.value);
                setPagina(0);
              }}
              className="h-10 w-full rounded-md border bg-background pl-10 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Buscar por título, descrição ou categoria"
            />
          </label>

          <div className="grid grid-cols-2 gap-2">
            <select
              value={status}
              onChange={(event) => atualizarFiltroStatus(event.target.value as "" | StatusChamadoSuporte)}
              className="h-10 rounded-md border bg-background px-3 text-sm font-medium outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              aria-label="Filtrar por status"
            >
              {STATUS.map((item) => (
                <option key={item.valor || "todos"} value={item.valor}>
                  {item.label}
                </option>
              ))}
            </select>
            <select
              value={prioridade}
              onChange={(event) => atualizarFiltroPrioridade(event.target.value as "" | PrioridadeChamadoSuporte)}
              className="h-10 rounded-md border bg-background px-3 text-sm font-medium outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              aria-label="Filtrar por prioridade"
            >
              {PRIORIDADES.map((item) => (
                <option key={item.valor || "todas"} value={item.valor}>
                  {item.label}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="mt-4 max-h-[620px] overflow-y-auto pr-1">
          {chamadosQuery.isLoading ? (
            <EstadoLista icon={LoaderCircle} titulo="Carregando chamados" girar />
          ) : chamados.length === 0 ? (
            <EstadoLista icon={Inbox} titulo="Nenhum chamado encontrado" />
          ) : (
            <div className="grid gap-3">
              {chamados.map((chamado) => (
                <button
                  key={chamado.id}
                  type="button"
                  onClick={() => setChamadoSelecionadoId(chamado.id)}
                  className={`rounded-lg border bg-background p-4 text-left transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring ${
                    chamado.id === chamadoSelecionadoId ? "border-primary shadow-sm" : "hover:border-primary/50"
                  }`}
                >
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0">
                      <p className="line-clamp-1 text-sm font-semibold text-card-foreground">{chamado.titulo}</p>
                      <p className="mt-1 line-clamp-2 text-xs leading-5 text-muted-foreground">{chamado.descricao}</p>
                    </div>
                    <BadgePrioridade prioridade={chamado.prioridade} />
                  </div>
                  <div className="mt-3 flex flex-wrap items-center gap-2 text-xs font-medium text-muted-foreground">
                    <BadgeStatus status={chamado.status} />
                    <span className="rounded-md border px-2 py-1">{chamado.categoria ?? "Geral"}</span>
                    <span className="inline-flex items-center gap-1">
                      <Clock3 className="h-3.5 w-3.5" />
                      {formatarData(chamado.atualizadoEm)}
                    </span>
                  </div>
                </button>
              ))}
            </div>
          )}
        </div>

        <div className="mt-4 flex items-center justify-between border-t pt-4">
          <p className="text-xs font-medium text-muted-foreground">
            Página {totalPaginas === 0 ? 0 : pagina + 1} de {totalPaginas}
          </p>
          <div className="flex gap-2">
            <Button type="button" variant="outline" size="icon" onClick={() => setPagina((valor) => valor - 1)} disabled={!podeVoltar} title="Página anterior">
              <ChevronLeft className="h-4 w-4" />
            </Button>
            <Button type="button" variant="outline" size="icon" onClick={() => setPagina((valor) => valor + 1)} disabled={!podeAvancar} title="Próxima página">
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </div>

      <aside className="min-w-0 rounded-lg border bg-card p-4 shadow-sm">
        {detalheQuery.isLoading ? (
          <EstadoLista icon={LoaderCircle} titulo="Carregando detalhe" girar />
        ) : !chamadoAtual ? (
          <EstadoLista icon={MessageSquareText} titulo="Selecione um chamado" />
        ) : (
          <div className="grid gap-4">
            <div className="border-b pb-4">
              <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
                <div className="min-w-0">
                  <p className="text-sm font-medium text-primary">Chamado de suporte</p>
                  <h2 className="mt-1 text-xl font-semibold text-card-foreground">{chamadoAtual.titulo}</h2>
                  <p className="mt-2 text-sm leading-6 text-muted-foreground">{chamadoAtual.descricao}</p>
                </div>
                <div className="flex shrink-0 flex-wrap gap-2">
                  <BadgeStatus status={chamadoAtual.status} />
                  <BadgePrioridade prioridade={chamadoAtual.prioridade} />
                </div>
              </div>
              <div className="mt-4 grid gap-2 sm:grid-cols-3">
                <MetricaSuporte rotulo="Solicitante" valor={chamadoAtual.solicitanteNome ?? "Não informado"} />
                <MetricaSuporte rotulo="E-mail" valor={chamadoAtual.solicitanteEmail ?? "Não informado"} />
                <MetricaSuporte rotulo="Atualizado" valor={formatarData(chamadoAtual.atualizadoEm)} />
              </div>
            </div>

            <div className="grid gap-3 md:grid-cols-[minmax(0,1fr)_minmax(0,1fr)]">
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Status
                <select
                  value={chamadoAtual.status}
                  onChange={(event) => atualizarTriagemMutation.mutate({ status: event.target.value as StatusChamadoSuporte })}
                  className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                >
                  {STATUS.filter((item) => item.valor).map((item) => (
                    <option key={item.valor} value={item.valor}>
                      {item.label}
                    </option>
                  ))}
                </select>
              </label>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Prioridade
                <select
                  value={chamadoAtual.prioridade}
                  onChange={(event) => atualizarTriagemMutation.mutate({ prioridade: event.target.value as PrioridadeChamadoSuporte })}
                  className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                >
                  {PRIORIDADES.filter((item) => item.valor).map((item) => (
                    <option key={item.valor} value={item.valor}>
                      {item.label}
                    </option>
                  ))}
                </select>
              </label>
            </div>

            <div className="grid gap-3">
              <h3 className="text-base font-semibold text-card-foreground">Histórico</h3>
              <div className="max-h-[320px] overflow-y-auto rounded-lg border bg-background p-3">
                {(detalhe?.mensagens ?? []).length === 0 ? (
                  <p className="text-sm text-muted-foreground">Nenhuma mensagem registrada.</p>
                ) : (
                  <div className="grid gap-3">
                    {detalhe?.mensagens.map((mensagem) => (
                      <article key={mensagem.id} className="rounded-md border bg-card p-3">
                        <div className="flex flex-wrap items-center justify-between gap-2">
                          <p className="text-sm font-semibold text-card-foreground">{mensagem.autorNome ?? rotuloOrigem(mensagem.origem)}</p>
                          <span className="text-xs font-medium text-muted-foreground">{formatarData(mensagem.criadoEm)}</span>
                        </div>
                        <p className="mt-2 text-sm leading-6 text-muted-foreground">{mensagem.mensagem}</p>
                      </article>
                    ))}
                  </div>
                )}
              </div>
            </div>

            <div className="grid gap-2">
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Resposta do suporte
                <textarea
                  value={resposta}
                  onChange={(event) => setResposta(event.target.value)}
                  className="min-h-28 rounded-md border bg-background px-3 py-2 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                  placeholder="Escreva uma resposta objetiva para o cliente."
                />
              </label>
              {erro ? <div className="rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">{erro}</div> : null}
              <Button type="button" onClick={enviarResposta} disabled={registrarMensagemMutation.isPending || !chamadoSelecionadoId}>
                {registrarMensagemMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <Send className="h-4 w-4" />}
                Enviar resposta
              </Button>
            </div>
          </div>
        )}
      </aside>
    </section>
  );
}

function BadgeStatus({ status }: { status: StatusChamadoSuporte }) {
  const config = {
    ABERTO: { label: "Aberto", className: "border-amber-200 bg-amber-50 text-amber-800", icon: AlertTriangle },
    EM_ATENDIMENTO: { label: "Em atendimento", className: "border-blue-200 bg-blue-50 text-blue-800", icon: MessageSquareText },
    AGUARDANDO_CLIENTE: { label: "Aguardando cliente", className: "border-sky-200 bg-sky-50 text-sky-800", icon: Clock3 },
    RESOLVIDO: { label: "Resolvido", className: "border-emerald-200 bg-emerald-50 text-emerald-800", icon: CheckCircle2 },
    CANCELADO: { label: "Cancelado", className: "border-slate-200 bg-slate-50 text-slate-700", icon: Inbox }
  }[status];
  const Icon = config.icon;

  return (
    <span className={`inline-flex items-center gap-1 rounded-md border px-2 py-1 text-xs font-semibold ${config.className}`}>
      <Icon className="h-3.5 w-3.5" />
      {config.label}
    </span>
  );
}

function BadgePrioridade({ prioridade }: { prioridade: PrioridadeChamadoSuporte }) {
  const config = {
    BAIXA: "border-emerald-200 bg-emerald-50 text-emerald-800",
    MEDIA: "border-sky-200 bg-sky-50 text-sky-800",
    ALTA: "border-amber-200 bg-amber-50 text-amber-800",
    CRITICA: "border-red-200 bg-red-50 text-red-700"
  }[prioridade];

  return (
    <span className={`inline-flex items-center rounded-md border px-2 py-1 text-xs font-semibold ${config}`}>
      {rotuloPrioridade(prioridade)}
    </span>
  );
}

function MetricaSuporte({ rotulo, valor }: { rotulo: string; valor: string }) {
  return (
    <div className="min-w-0 rounded-md border bg-background px-3 py-2">
      <p className="text-xs font-medium uppercase text-muted-foreground">{rotulo}</p>
      <p className="mt-1 truncate text-sm font-semibold text-card-foreground">{valor}</p>
    </div>
  );
}

function EstadoLista({ icon: Icon, titulo, girar = false }: { icon: typeof Inbox; titulo: string; girar?: boolean }) {
  return (
    <div className="flex min-h-44 flex-col items-center justify-center rounded-lg border bg-background p-6 text-center">
      <Icon className={`h-8 w-8 text-primary ${girar ? "animate-spin" : ""}`} />
      <p className="mt-3 text-sm font-semibold text-card-foreground">{titulo}</p>
    </div>
  );
}

function rotuloPrioridade(prioridade: PrioridadeChamadoSuporte) {
  return PRIORIDADES.find((item) => item.valor === prioridade)?.label ?? prioridade;
}

function rotuloOrigem(origem: string) {
  if (origem === "SUPORTE") {
    return "Suporte";
  }
  if (origem === "SISTEMA") {
    return "Sistema";
  }
  return "Cliente";
}

function formatarData(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(valor));
}
