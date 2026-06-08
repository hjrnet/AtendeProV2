"use client";

import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  BellRing,
  CalendarClock,
  CheckCircle2,
  ClipboardCheck,
  HeartHandshake,
  MessageCircle,
  Search,
  Send,
  SmilePlus,
  Target,
  Users
} from "lucide-react";

import {
  concluirTarefaPosVenda,
  consultarPainelPosVenda,
  criarTarefaPosVenda,
  registrarContatoPosVenda,
  registrarNpsPosVenda,
  type AreaPosVenda,
  type CanalContatoPosVenda,
  type ClientePosVenda,
  type TarefaPosVenda,
  type TemplateMensagemPosVenda
} from "@/features/relacionamento/api/relacionamento-client";

type PosVendaOperacionalViewProps = {
  empresaId: string;
  area: AreaPosVenda;
};

const canais: CanalContatoPosVenda[] = ["WHATSAPP", "TELEFONE", "EMAIL", "PRESENCIAL", "OUTRO"];

export function PosVendaOperacionalView({ empresaId, area }: PosVendaOperacionalViewProps) {
  const queryClient = useQueryClient();
  const [busca, setBusca] = useState("");
  const [clienteSelecionadoId, setClienteSelecionadoId] = useState("");
  const [templateSelecionado, setTemplateSelecionado] = useState("");
  const [mensagemContato, setMensagemContato] = useState("");
  const [canalContato, setCanalContato] = useState<CanalContatoPosVenda>("WHATSAPP");
  const [observacoesContato, setObservacoesContato] = useState("");
  const [npsNota, setNpsNota] = useState(10);
  const [npsComentario, setNpsComentario] = useState("");
  const [tituloTarefa, setTituloTarefa] = useState("");
  const [descricaoTarefa, setDescricaoTarefa] = useState("");

  const painelQuery = useQuery({
    queryKey: ["pos-venda", empresaId, area, busca],
    queryFn: () => consultarPainelPosVenda({ empresaId, area, busca }),
    enabled: Boolean(empresaId)
  });

  const painel = painelQuery.data;
  const clienteSelecionado = useMemo(() => {
    if (!painel?.clientes.length) {
      return null;
    }
    return painel.clientes.find((cliente) => cliente.id === clienteSelecionadoId) ?? painel.clientes[0];
  }, [clienteSelecionadoId, painel?.clientes]);

  const registrarContatoMutation = useMutation({
    mutationFn: registrarContatoPosVenda,
    onSuccess: () => {
      setMensagemContato("");
      setObservacoesContato("");
      queryClient.invalidateQueries({ queryKey: ["pos-venda", empresaId, area] });
    }
  });

  const registrarNpsMutation = useMutation({
    mutationFn: registrarNpsPosVenda,
    onSuccess: () => {
      setNpsNota(10);
      setNpsComentario("");
      queryClient.invalidateQueries({ queryKey: ["pos-venda", empresaId, area] });
    }
  });

  const criarTarefaMutation = useMutation({
    mutationFn: criarTarefaPosVenda,
    onSuccess: () => {
      setTituloTarefa("");
      setDescricaoTarefa("");
      queryClient.invalidateQueries({ queryKey: ["pos-venda", empresaId, area] });
    }
  });

  const concluirTarefaMutation = useMutation({
    mutationFn: concluirTarefaPosVenda,
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["pos-venda", empresaId, area] })
  });

  const tituloArea = area === "NUTRI" ? "Pós-venda Nutri" : "Pós-venda Beauty";
  const descricaoArea = area === "NUTRI"
    ? "Retornos, adesão ao plano, exames, check-ins e reativação de pacientes."
    : "Pós-procedimento, manutenção de resultados, pacotes, NPS e recorrência em estética.";

  function selecionarTemplate(template: TemplateMensagemPosVenda) {
    setTemplateSelecionado(template.codigo);
    setCanalContato("WHATSAPP");
    setMensagemContato(personalizarMensagem(template.mensagem, clienteSelecionado));
  }

  function registrarContato() {
    if (!clienteSelecionado || !mensagemContato.trim()) {
      return;
    }
    registrarContatoMutation.mutate({
      empresaId,
      clienteId: clienteSelecionado.id,
      area,
      canal: canalContato,
      templateCodigo: templateSelecionado || null,
      mensagem: mensagemContato.trim(),
      observacoes: observacoesContato.trim() || null
    });
  }

  function registrarNps() {
    if (!clienteSelecionado) {
      return;
    }
    registrarNpsMutation.mutate({
      empresaId,
      clienteId: clienteSelecionado.id,
      area,
      nota: Number(npsNota),
      comentario: npsComentario.trim() || null,
      origem: "POS_VENDA_MANUAL"
    });
  }

  function criarTarefaManual() {
    if (!clienteSelecionado || !tituloTarefa.trim()) {
      return;
    }
    criarTarefaMutation.mutate({
      empresaId,
      clienteId: clienteSelecionado.id,
      area,
      tipo: "OUTRO",
      titulo: tituloTarefa.trim(),
      descricao: descricaoTarefa.trim() || null,
      dataRecomendada: new Date().toISOString().slice(0, 10),
      origem: "MANUAL"
    });
  }

  function salvarTarefaAutomatica(tarefa: TarefaPosVenda) {
    criarTarefaMutation.mutate({
      empresaId,
      clienteId: tarefa.clienteId,
      area,
      tipo: tarefa.tipo,
      titulo: tarefa.titulo,
      descricao: tarefa.descricao,
      dataRecomendada: tarefa.dataRecomendada,
      origem: "AUTOMATICA_CONFIRMADA"
    });
  }

  return (
    <div className="space-y-6">
      <section className="overflow-hidden rounded-3xl border border-slate-200 bg-gradient-to-br from-slate-950 via-slate-900 to-teal-950 p-6 text-white shadow-sm">
        <div className="flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
          <div className="max-w-3xl">
            <span className="inline-flex items-center gap-2 rounded-full border border-white/20 bg-white/10 px-3 py-1 text-xs font-semibold uppercase tracking-[0.22em] text-teal-100">
              <HeartHandshake className="h-4 w-4" /> R16 relacionamento
            </span>
            <h1 className="mt-4 text-3xl font-semibold tracking-tight sm:text-4xl">{tituloArea}</h1>
            <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-200">{descricaoArea}</p>
          </div>
          <div className="rounded-2xl border border-white/15 bg-white/10 p-4 text-sm text-slate-100 backdrop-blur">
            <p className="font-semibold">Motor de recorrência</p>
            <p className="mt-1 text-slate-300">Carteira, tarefas, templates, NPS e campanhas simples no mesmo lugar.</p>
          </div>
        </div>
      </section>

      <section className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <MetricaCard icone={Users} titulo="Clientes monitorados" valor={painel?.metricas.clientesMonitorados ?? 0} detalhe="Carteira ativa da vertical" />
        <MetricaCard icone={CalendarClock} titulo="Retornos pendentes" valor={painel?.metricas.retornosPendentes ?? 0} detalhe="Janela recomendada aberta" destaque />
        <MetricaCard icone={BellRing} titulo="Sem contato" valor={painel?.metricas.clientesSemContato ?? 0} detalhe="Mais de 30 dias sem toque" />
        <MetricaCard icone={SmilePlus} titulo="NPS médio" valor={(painel?.metricas.npsMedio ?? 0).toFixed(1)} detalhe={`${painel?.metricas.detratores ?? 0} detratores`} />
      </section>

      <section className="grid gap-5 xl:grid-cols-[minmax(0,1.1fr)_minmax(360px,0.9fr)]">
        <div className="rounded-3xl border bg-card p-5 shadow-sm">
          <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h2 className="text-lg font-semibold text-card-foreground">Carteira de acompanhamento</h2>
              <p className="text-sm text-muted-foreground">Status, risco, retorno recomendado, faltas e oportunidade de recorrência.</p>
            </div>
            <label className="relative block sm:w-72">
              <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <input
                value={busca}
                onChange={(event) => setBusca(event.target.value)}
                placeholder="Buscar cliente"
                className="h-10 w-full rounded-2xl border bg-background pl-9 pr-3 text-sm outline-none ring-primary/20 transition focus:ring-4"
              />
            </label>
          </div>

          <div className="mt-4 max-h-[660px] space-y-3 overflow-auto pr-1">
            {painelQuery.isLoading ? <EstadoSuave texto="Carregando carteira de pós-venda..." /> : null}
            {!painelQuery.isLoading && !painel?.clientes.length ? <EstadoSuave texto="Nenhum cliente encontrado para esta vertical." /> : null}
            {painel?.clientes.map((cliente) => (
              <button
                key={cliente.id}
                type="button"
                onClick={() => setClienteSelecionadoId(cliente.id)}
                className={`w-full rounded-2xl border p-4 text-left transition hover:border-primary/40 hover:bg-primary/5 ${clienteSelecionado?.id === cliente.id ? "border-primary bg-primary/5" : "bg-background"}`}
              >
                <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
                  <div>
                    <div className="flex flex-wrap items-center gap-2">
                      <h3 className="font-semibold text-card-foreground">{cliente.nome}</h3>
                      <span className={`rounded-full px-2.5 py-1 text-xs font-semibold ${classeRisco(cliente.riscoAbandono)}`}>Risco {rotuloRisco(cliente.riscoAbandono)}</span>
                      {cliente.aniversarioProximo ? <span className="rounded-full bg-amber-100 px-2.5 py-1 text-xs font-semibold text-amber-800">Aniversário próximo</span> : null}
                    </div>
                    <p className="mt-2 text-sm leading-6 text-muted-foreground">{cliente.motivoRetorno}</p>
                  </div>
                  <span className={`rounded-full px-3 py-1 text-xs font-semibold ${classeStatus(cliente.statusAcompanhamento)}`}>{cliente.statusRotulo}</span>
                </div>
                <div className="mt-3 grid gap-2 text-xs text-muted-foreground sm:grid-cols-4">
                  <span>Última consulta: {formatarDataHora(cliente.ultimaConsultaEm)}</span>
                  <span>Próxima: {formatarDataHora(cliente.proximaConsultaEm)}</span>
                  <span>Retorno: {formatarData(cliente.retornoRecomendadoEm)}</span>
                  <span>NPS: {cliente.ultimaNotaNps ?? "sem nota"}</span>
                </div>
              </button>
            ))}
          </div>
        </div>

        <div className="space-y-5">
          <div className="rounded-3xl border bg-card p-5 shadow-sm">
            <h2 className="text-lg font-semibold text-card-foreground">Ação rápida</h2>
            <p className="mt-1 text-sm text-muted-foreground">Selecione um cliente e use templates prontos para contato manual.</p>

            <div className="mt-4 rounded-2xl bg-muted/60 p-4">
              <p className="text-sm font-semibold text-card-foreground">{clienteSelecionado?.nome ?? "Nenhum cliente selecionado"}</p>
              <p className="text-xs text-muted-foreground">{clienteSelecionado?.telefone ?? "Telefone não informado"}</p>
            </div>

            <div className="mt-4 grid gap-2 sm:grid-cols-2">
              {painel?.templates.map((template) => (
                <button
                  key={template.codigo}
                  type="button"
                  onClick={() => selecionarTemplate(template)}
                  className="rounded-2xl border bg-background p-3 text-left transition hover:border-primary/40 hover:bg-primary/5"
                >
                  <p className="text-sm font-semibold text-card-foreground">{template.titulo}</p>
                  <p className="mt-1 text-xs text-muted-foreground">{template.objetivo}</p>
                </button>
              ))}
            </div>

            <div className="mt-4 space-y-3">
              <select
                value={canalContato}
                onChange={(event) => setCanalContato(event.target.value as CanalContatoPosVenda)}
                className="h-10 w-full rounded-2xl border bg-background px-3 text-sm outline-none ring-primary/20 transition focus:ring-4"
              >
                {canais.map((canal) => <option key={canal} value={canal}>{rotuloCanal(canal)}</option>)}
              </select>
              <textarea
                value={mensagemContato}
                onChange={(event) => setMensagemContato(event.target.value)}
                placeholder="Mensagem de contato"
                rows={5}
                className="w-full rounded-2xl border bg-background p-3 text-sm outline-none ring-primary/20 transition focus:ring-4"
              />
              <input
                value={observacoesContato}
                onChange={(event) => setObservacoesContato(event.target.value)}
                placeholder="Observação interna opcional"
                className="h-10 w-full rounded-2xl border bg-background px-3 text-sm outline-none ring-primary/20 transition focus:ring-4"
              />
              <div className="flex flex-col gap-2 sm:flex-row">
                <button
                  type="button"
                  onClick={registrarContato}
                  disabled={!clienteSelecionado || !mensagemContato.trim() || registrarContatoMutation.isPending}
                  className="inline-flex flex-1 items-center justify-center gap-2 rounded-2xl bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  <ClipboardCheck className="h-4 w-4" /> Registrar contato
                </button>
                <a
                  href={montarLinkWhatsapp(clienteSelecionado, mensagemContato)}
                  target="_blank"
                  rel="noreferrer"
                  className="inline-flex flex-1 items-center justify-center gap-2 rounded-2xl border px-4 py-2.5 text-sm font-semibold text-card-foreground transition hover:bg-muted"
                >
                  <MessageCircle className="h-4 w-4" /> Abrir WhatsApp
                </a>
              </div>
            </div>
          </div>

          <div className="rounded-3xl border bg-card p-5 shadow-sm">
            <h2 className="text-lg font-semibold text-card-foreground">NPS e satisfação</h2>
            <p className="mt-1 text-sm text-muted-foreground">Registre nota pós-consulta ou pós-procedimento.</p>
            <div className="mt-4 grid gap-3 sm:grid-cols-[100px_minmax(0,1fr)]">
              <input
                type="number"
                min={0}
                max={10}
                value={npsNota}
                onChange={(event) => setNpsNota(Number(event.target.value))}
                className="h-11 rounded-2xl border bg-background px-3 text-sm outline-none ring-primary/20 transition focus:ring-4"
              />
              <input
                value={npsComentario}
                onChange={(event) => setNpsComentario(event.target.value)}
                placeholder="Comentário opcional"
                className="h-11 rounded-2xl border bg-background px-3 text-sm outline-none ring-primary/20 transition focus:ring-4"
              />
            </div>
            <button
              type="button"
              onClick={registrarNps}
              disabled={!clienteSelecionado || registrarNpsMutation.isPending}
              className="mt-3 inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-slate-950 px-4 py-2.5 text-sm font-semibold text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
            >
              <SmilePlus className="h-4 w-4" /> Registrar NPS
            </button>
          </div>
        </div>
      </section>

      <section className="grid gap-5 xl:grid-cols-[minmax(0,1fr)_380px]">
        <div className="rounded-3xl border bg-card p-5 shadow-sm">
          <div className="flex items-center justify-between gap-3">
            <div>
              <h2 className="text-lg font-semibold text-card-foreground">Tarefas e lembretes</h2>
              <p className="text-sm text-muted-foreground">Automáticas por retorno, falta, NPS, aniversário e sem contato, com opção de salvar/concluir.</p>
            </div>
            <BellRing className="h-5 w-5 text-primary" />
          </div>
          <div className="mt-4 grid gap-3 md:grid-cols-2">
            {painel?.tarefas.map((tarefa, indice) => (
              <div key={`${tarefa.id ?? "auto"}-${tarefa.clienteId}-${tarefa.tipo}-${indice}`} className="rounded-2xl border bg-background p-4">
                <div className="flex items-start justify-between gap-2">
                  <div>
                    <p className="text-sm font-semibold text-card-foreground">{tarefa.titulo}</p>
                    <p className="mt-1 text-xs text-muted-foreground">{tarefa.clienteNome} - {formatarData(tarefa.dataRecomendada)}</p>
                  </div>
                  <span className="rounded-full bg-muted px-2.5 py-1 text-[11px] font-semibold text-muted-foreground">{tarefa.origem ?? "MANUAL"}</span>
                </div>
                <p className="mt-3 text-sm leading-6 text-muted-foreground">{tarefa.descricao ?? "Sem descrição."}</p>
                {tarefa.id ? (
                  <button
                    type="button"
                    onClick={() => concluirTarefaMutation.mutate({ empresaId, tarefaId: tarefa.id as string })}
                    disabled={tarefa.status === "CONCLUIDA" || concluirTarefaMutation.isPending}
                    className="mt-3 inline-flex items-center gap-2 rounded-2xl border px-3 py-2 text-xs font-semibold transition hover:bg-muted disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    <CheckCircle2 className="h-4 w-4" /> {tarefa.status === "CONCLUIDA" ? "Concluída" : "Concluir"}
                  </button>
                ) : (
                  <button
                    type="button"
                    onClick={() => salvarTarefaAutomatica(tarefa)}
                    disabled={criarTarefaMutation.isPending}
                    className="mt-3 inline-flex items-center gap-2 rounded-2xl bg-primary px-3 py-2 text-xs font-semibold text-primary-foreground transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
                  >
                    <BellRing className="h-4 w-4" /> Salvar tarefa
                  </button>
                )}
              </div>
            ))}
            {!painel?.tarefas.length ? <EstadoSuave texto="Nenhuma tarefa pendente no momento." /> : null}
          </div>
        </div>

        <div className="rounded-3xl border bg-card p-5 shadow-sm">
          <h2 className="text-lg font-semibold text-card-foreground">Criar tarefa manual</h2>
          <p className="mt-1 text-sm text-muted-foreground">Use para combinados específicos do acompanhamento.</p>
          <div className="mt-4 space-y-3">
            <input
              value={tituloTarefa}
              onChange={(event) => setTituloTarefa(event.target.value)}
              placeholder="Título da tarefa"
              className="h-10 w-full rounded-2xl border bg-background px-3 text-sm outline-none ring-primary/20 transition focus:ring-4"
            />
            <textarea
              value={descricaoTarefa}
              onChange={(event) => setDescricaoTarefa(event.target.value)}
              placeholder="Descrição"
              rows={4}
              className="w-full rounded-2xl border bg-background p-3 text-sm outline-none ring-primary/20 transition focus:ring-4"
            />
            <button
              type="button"
              onClick={criarTarefaManual}
              disabled={!clienteSelecionado || !tituloTarefa.trim() || criarTarefaMutation.isPending}
              className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground transition hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
            >
              <Send className="h-4 w-4" /> Criar tarefa
            </button>
          </div>
        </div>
      </section>

      <section className="grid gap-5 lg:grid-cols-3">
        <div className="rounded-3xl border bg-card p-5 shadow-sm lg:col-span-2">
          <h2 className="text-lg font-semibold text-card-foreground">Segmentação e campanhas simples</h2>
          <p className="mt-1 text-sm text-muted-foreground">Filtros operacionais para reativação, recorrência, satisfação e retorno.</p>
          <div className="mt-4 grid gap-3 sm:grid-cols-2">
            {painel?.segmentos.map((segmento) => (
              <div key={segmento.codigo} className="rounded-2xl border bg-background p-4">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <p className="text-sm font-semibold text-card-foreground">{segmento.titulo}</p>
                    <p className="mt-1 text-xs leading-5 text-muted-foreground">{segmento.descricao}</p>
                  </div>
                  <span className="flex h-10 min-w-10 items-center justify-center rounded-2xl bg-primary/10 text-sm font-bold text-primary">{segmento.quantidadeClientes}</span>
                </div>
                <p className="mt-3 text-xs font-semibold text-muted-foreground">Ação: {segmento.acaoRecomendada}</p>
              </div>
            ))}
          </div>
        </div>

        <div className="rounded-3xl border bg-card p-5 shadow-sm">
          <h2 className="text-lg font-semibold text-card-foreground">Histórico recente</h2>
          <div className="mt-4 space-y-3">
            {painel?.contatosRecentes.slice(0, 6).map((contato) => (
              <div key={contato.id} className="rounded-2xl bg-muted/60 p-3">
                <p className="text-sm font-semibold text-card-foreground">{contato.clienteNome}</p>
                <p className="mt-1 line-clamp-2 text-xs text-muted-foreground">{rotuloCanal(contato.canal)} - {contato.mensagem}</p>
              </div>
            ))}
            {painel?.npsRecentes.slice(0, 4).map((nps) => (
              <div key={nps.id} className="rounded-2xl bg-amber-50 p-3 text-amber-950">
                <p className="text-sm font-semibold">NPS {nps.nota} - {nps.clienteNome}</p>
                <p className="mt-1 line-clamp-2 text-xs">{nps.comentario ?? "Sem comentário."}</p>
              </div>
            ))}
            {!painel?.contatosRecentes.length && !painel?.npsRecentes.length ? <EstadoSuave texto="Sem histórico recente ainda." /> : null}
          </div>
        </div>
      </section>
    </div>
  );
}

function MetricaCard({ icone: Icone, titulo, valor, detalhe, destaque = false }: { icone: typeof Users; titulo: string; valor: string | number; detalhe: string; destaque?: boolean }) {
  return (
    <div className={`rounded-3xl border p-5 shadow-sm ${destaque ? "border-primary/30 bg-primary/10" : "bg-card"}`}>
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-medium text-muted-foreground">{titulo}</p>
          <p className="mt-2 text-3xl font-semibold text-card-foreground">{valor}</p>
          <p className="mt-1 text-xs text-muted-foreground">{detalhe}</p>
        </div>
        <span className="rounded-2xl bg-background p-3 text-primary shadow-sm"><Icone className="h-5 w-5" /></span>
      </div>
    </div>
  );
}

function EstadoSuave({ texto }: { texto: string }) {
  return <div className="rounded-2xl border border-dashed bg-muted/40 p-5 text-center text-sm text-muted-foreground">{texto}</div>;
}

function personalizarMensagem(mensagem: string, cliente: ClientePosVenda | null) {
  return mensagem.replaceAll("{{nome}}", cliente?.nome.split(" ")[0] ?? "cliente");
}

function montarLinkWhatsapp(cliente: ClientePosVenda | null, mensagem: string) {
  const telefone = cliente?.telefone?.replace(/\D/g, "") ?? "";
  if (!telefone) {
    return "#";
  }
  const numero = telefone.startsWith("55") ? telefone : `55${telefone}`;
  return `https://wa.me/${numero}?text=${encodeURIComponent(mensagem)}`;
}

function formatarData(valor?: string | null) {
  if (!valor) {
    return "sem data";
  }
  return new Intl.DateTimeFormat("pt-BR", { day: "2-digit", month: "short", year: "numeric" }).format(new Date(valor));
}

function formatarDataHora(valor?: string | null) {
  if (!valor) {
    return "sem registro";
  }
  return new Intl.DateTimeFormat("pt-BR", { day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit" }).format(new Date(valor));
}

function classeRisco(risco: ClientePosVenda["riscoAbandono"]) {
  if (risco === "ALTO") {
    return "bg-rose-100 text-rose-700";
  }
  if (risco === "MEDIO") {
    return "bg-amber-100 text-amber-800";
  }
  return "bg-emerald-100 text-emerald-700";
}

function rotuloRisco(risco: ClientePosVenda["riscoAbandono"]) {
  return risco === "MEDIO" ? "médio" : risco.toLowerCase();
}

function classeStatus(status: string) {
  if (status === "FALTA_RECENTE" || status === "INATIVO") {
    return "bg-rose-100 text-rose-700";
  }
  if (status === "RETORNO_PENDENTE" || status === "SEM_CONTATO") {
    return "bg-amber-100 text-amber-800";
  }
  return "bg-emerald-100 text-emerald-700";
}

function rotuloCanal(canal: CanalContatoPosVenda) {
  return canal.charAt(0) + canal.slice(1).toLowerCase().replace("_", " ");
}
