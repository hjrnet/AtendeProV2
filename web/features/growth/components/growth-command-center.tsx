"use client";

import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  ArrowRight,
  BadgeDollarSign,
  CalendarDays,
  CheckCircle2,
  ClipboardList,
  HeartHandshake,
  LoaderCircle,
  Presentation,
  Search,
  Sparkles,
  Target,
  TrendingUp,
  Users
} from "lucide-react";

import {
  atualizarEtapaLeadGrowth,
  atualizarVinculosLeadGrowth,
  consultarPainelGrowth,
  listarLeadsGrowth,
  registrarLeadGrowth,
  type ApresentacaoDemoGrowth,
  type EtapaLeadGrowth,
  type IndicadorVerticalGrowth,
  type LeadGrowth,
  type PerfilDemoGrowth,
  type RegistrarLeadGrowthInput,
  type SugestaoPosVendaIA,
  type VerticalGrowth
} from "@/features/growth/api/growth-client";

type GrowthCommandCenterProps = {
  empresaId: string;
};

type FiltroVertical = "TODAS" | VerticalGrowth;
type FiltroEtapa = "TODAS" | EtapaLeadGrowth;

const etapas: Array<{ id: EtapaLeadGrowth; label: string; descricao: string }> = [
  { id: "NOVO", label: "Novo", descricao: "Entrada ou indicacao recebida" },
  { id: "QUALIFICADO", label: "Qualificado", descricao: "Perfil validado para abordagem" },
  { id: "DEMO_AGENDADA", label: "Demo agendada", descricao: "Apresentacao ou conversa marcada" },
  { id: "CONVERTIDO", label: "Convertido", descricao: "Virou cliente/paciente ou pacote" },
  { id: "PERDIDO", label: "Perdido", descricao: "Sem fit ou sem resposta" }
];

const perfisDemo: Array<{ id: PerfilDemoGrowth; label: string }> = [
  { id: "NUTRI", label: "Nutri" },
  { id: "BEAUTY", label: "Beauty" },
  { id: "GESTOR", label: "Gestor" },
  { id: "INVESTIDOR", label: "Investidor" }
];

const formularioInicial = {
  nome: "",
  email: "",
  telefone: "",
  vertical: "NUTRI" as VerticalGrowth,
  origem: "Indicação",
  etapa: "NOVO" as EtapaLeadGrowth,
  potencialMensal: "",
  observacoes: ""
};

export function GrowthCommandCenter({ empresaId }: GrowthCommandCenterProps) {
  const queryClient = useQueryClient();
  const [vertical, setVertical] = useState<FiltroVertical>("TODAS");
  const [etapa, setEtapa] = useState<FiltroEtapa>("TODAS");
  const [busca, setBusca] = useState("");
  const [perfilDemo, setPerfilDemo] = useState<PerfilDemoGrowth>("NUTRI");
  const [formulario, setFormulario] = useState(formularioInicial);
  const [vinculos, setVinculos] = useState<Record<string, { clientePacienteId: string; compromissoAgendaId: string }>>({});

  const painelQuery = useQuery({
    queryKey: ["growth-painel", empresaId],
    queryFn: () => consultarPainelGrowth(empresaId),
    enabled: Boolean(empresaId)
  });

  const leadsQuery = useQuery({
    queryKey: ["growth-leads", empresaId, vertical, etapa, busca],
    queryFn: () =>
      listarLeadsGrowth({
        empresaId,
        vertical: vertical === "TODAS" ? undefined : vertical,
        etapa: etapa === "TODAS" ? undefined : etapa,
        busca
      }),
    enabled: Boolean(empresaId)
  });

  const registrarLeadMutation = useMutation({
    mutationFn: registrarLeadGrowth,
    onSuccess: () => {
      setFormulario(formularioInicial);
      invalidarGrowth(queryClient, empresaId);
    }
  });

  const atualizarEtapaMutation = useMutation({
    mutationFn: atualizarEtapaLeadGrowth,
    onSuccess: () => invalidarGrowth(queryClient, empresaId)
  });

  const atualizarVinculosMutation = useMutation({
    mutationFn: atualizarVinculosLeadGrowth,
    onSuccess: () => invalidarGrowth(queryClient, empresaId)
  });

  const leads = leadsQuery.data ?? painelQuery.data?.leads ?? [];
  const indicadores = painelQuery.data?.indicadores ?? [];
  const sugestoes = useMemo(() => filtrarSugestoes(painelQuery.data?.sugestoesPosVenda ?? [], vertical), [painelQuery.data?.sugestoesPosVenda, vertical]);
  const apresentacoesDemo = useMemo(
    () => (painelQuery.data?.apresentacoesDemo ?? []).filter((apresentacao) => apresentacao.perfil === perfilDemo),
    [painelQuery.data?.apresentacoesDemo, perfilDemo]
  );
  const totalPipeline = leads.reduce((total, lead) => total + (lead.etapa === "PERDIDO" ? 0 : lead.potencialMensal), 0);
  const leadsPorEtapa = useMemo(() => etapas.map((item) => ({ ...item, leads: leads.filter((lead) => lead.etapa === item.id) })), [leads]);

  if (!empresaId) {
    return (
      <EstadoGrowth
        titulo="Selecione uma empresa"
        descricao="Escolha uma empresa para carregar funil, indicadores, pos-venda e apresentacao comercial."
      />
    );
  }

  function registrarNovoLead() {
    if (!formulario.nome.trim() || !formulario.email.trim()) {
      return;
    }

    const payload: RegistrarLeadGrowthInput = {
      empresaId,
      nome: formulario.nome.trim(),
      email: formulario.email.trim(),
      telefone: formulario.telefone.trim() || null,
      vertical: formulario.vertical,
      origem: formulario.origem.trim() || "Manual",
      etapa: formulario.etapa,
      potencialMensal: Number(formulario.potencialMensal || 0),
      observacoes: formulario.observacoes.trim() || null
    };

    registrarLeadMutation.mutate(payload);
  }

  function atualizarEtapa(lead: LeadGrowth, direcao: "avancar" | "voltar") {
    const indice = etapas.findIndex((item) => item.id === lead.etapa);
    const proximaEtapa = etapas[direcao === "avancar" ? Math.min(indice + 1, etapas.length - 1) : Math.max(indice - 1, 0)]?.id;
    if (!proximaEtapa || proximaEtapa === lead.etapa) {
      return;
    }
    atualizarEtapaMutation.mutate({ empresaId, leadId: lead.id, etapa: proximaEtapa });
  }

  function salvarVinculos(lead: LeadGrowth) {
    const vinculo = vinculos[lead.id] ?? {
      clientePacienteId: lead.clientePacienteId ?? "",
      compromissoAgendaId: lead.compromissoAgendaId ?? ""
    };

    atualizarVinculosMutation.mutate({
      empresaId,
      leadId: lead.id,
      clientePacienteId: vinculo.clientePacienteId.trim() || null,
      compromissoAgendaId: vinculo.compromissoAgendaId.trim() || null
    });
  }

  return (
    <div className="space-y-6">
      <section className="overflow-hidden rounded-3xl border border-cyan-900/20 bg-[radial-gradient(circle_at_top_left,_rgba(34,211,238,0.22),_transparent_34%),linear-gradient(135deg,_#082f49,_#111827_55%,_#312e81)] p-6 text-white shadow-sm">
        <div className="flex flex-col gap-6 xl:flex-row xl:items-end xl:justify-between">
          <div className="max-w-3xl">
            <span className="inline-flex items-center gap-2 rounded-full border border-white/20 bg-white/10 px-3 py-1 text-xs font-semibold uppercase tracking-[0.22em] text-cyan-100">
              <TrendingUp className="h-4 w-4" /> R20 Growth UI
            </span>
            <h1 className="mt-4 text-3xl font-semibold tracking-tight sm:text-4xl">Comando comercial Nutri + Beauty</h1>
            <p className="mt-3 max-w-2xl text-sm leading-6 text-slate-200">
              Funil, indicadores executivos, pos-venda assistida e roteiro de demo navegavel usando os dados reais do modulo Growth.
            </p>
          </div>
          <div className="grid gap-3 sm:grid-cols-3">
            <MetricaHero icon={Users} label="Leads ativos" value={String(leads.filter((lead) => lead.etapa !== "PERDIDO").length)} />
            <MetricaHero icon={BadgeDollarSign} label="Pipeline" value={formatarMoeda(totalPipeline)} />
            <MetricaHero icon={Presentation} label="Demos" value={String(painelQuery.data?.apresentacoesDemo.length ?? 0)} />
          </div>
        </div>
      </section>

      <section className="grid gap-3 rounded-3xl border bg-card p-4 shadow-sm lg:grid-cols-[minmax(0,1fr)_170px_190px]">
        <label className="relative block">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <input
            value={busca}
            onChange={(event) => setBusca(event.target.value)}
            placeholder="Buscar lead, email ou origem"
            className="h-11 w-full rounded-2xl border bg-background pl-9 pr-3 text-sm outline-none ring-primary/20 transition focus:ring-4"
          />
        </label>
        <select value={vertical} onChange={(event) => setVertical(event.target.value as FiltroVertical)} className="h-11 rounded-2xl border bg-background px-3 text-sm">
          <option value="TODAS">Todas verticais</option>
          <option value="NUTRI">Nutri</option>
          <option value="BEAUTY">Beauty</option>
        </select>
        <select value={etapa} onChange={(event) => setEtapa(event.target.value as FiltroEtapa)} className="h-11 rounded-2xl border bg-background px-3 text-sm">
          <option value="TODAS">Todas etapas</option>
          {etapas.map((item) => <option key={item.id} value={item.id}>{item.label}</option>)}
        </select>
      </section>

      <section className="grid gap-5 xl:grid-cols-[minmax(0,1fr)_360px]">
        <div className="rounded-3xl border bg-card p-5 shadow-sm">
          <CabecalhoSecao
            icone={Target}
            tag="TASK-R20-001"
            titulo="Funil visual de leads"
            descricao="Arraste conceitualmente a venda com avancar/voltar, filtre por vertical e vincule cliente ou agenda quando houver match."
            carregando={leadsQuery.isFetching}
          />
          <div className="mt-5 grid gap-4 xl:grid-cols-5">
            {leadsPorEtapa.map((coluna) => (
              <article key={coluna.id} className="min-h-72 rounded-2xl border bg-background p-3">
                <div className="flex items-start justify-between gap-3">
                  <div>
                    <h3 className="font-semibold text-card-foreground">{coluna.label}</h3>
                    <p className="mt-1 text-xs leading-5 text-muted-foreground">{coluna.descricao}</p>
                  </div>
                  <span className="rounded-full bg-primary/10 px-2 py-1 text-xs font-semibold text-primary">{coluna.leads.length}</span>
                </div>
                <div className="mt-3 space-y-3">
                  {coluna.leads.length === 0 ? <p className="rounded-xl border border-dashed p-3 text-xs leading-5 text-muted-foreground">Sem leads nesta etapa.</p> : null}
                  {coluna.leads.map((lead) => (
                    <LeadCard
                      key={lead.id}
                      lead={lead}
                      vinculo={vinculos[lead.id]}
                      onVinculoChange={(valor) => setVinculos((atual) => ({ ...atual, [lead.id]: valor }))}
                      onSalvarVinculos={() => salvarVinculos(lead)}
                      onAvancar={() => atualizarEtapa(lead, "avancar")}
                      onVoltar={() => atualizarEtapa(lead, "voltar")}
                      carregando={atualizarEtapaMutation.isPending || atualizarVinculosMutation.isPending}
                    />
                  ))}
                </div>
              </article>
            ))}
          </div>
        </div>

        <aside className="rounded-3xl border bg-card p-5 shadow-sm">
          <CabecalhoSecao
            icone={Sparkles}
            tag="Cadastro rapido"
            titulo="Novo lead"
            descricao="Entrada manual para indicacao, campanha, WhatsApp ou evento."
          />
          <div className="mt-5 grid gap-3">
            <Campo value={formulario.nome} onChange={(valor) => setFormulario((atual) => ({ ...atual, nome: valor }))} placeholder="Nome" />
            <Campo value={formulario.email} onChange={(valor) => setFormulario((atual) => ({ ...atual, email: valor }))} placeholder="Email" type="email" />
            <Campo value={formulario.telefone} onChange={(valor) => setFormulario((atual) => ({ ...atual, telefone: valor }))} placeholder="Telefone" />
            <div className="grid grid-cols-2 gap-2">
              <select value={formulario.vertical} onChange={(event) => setFormulario((atual) => ({ ...atual, vertical: event.target.value as VerticalGrowth }))} className="h-10 rounded-xl border bg-background px-3 text-sm">
                <option value="NUTRI">Nutri</option>
                <option value="BEAUTY">Beauty</option>
              </select>
              <select value={formulario.etapa} onChange={(event) => setFormulario((atual) => ({ ...atual, etapa: event.target.value as EtapaLeadGrowth }))} className="h-10 rounded-xl border bg-background px-3 text-sm">
                {etapas.map((item) => <option key={item.id} value={item.id}>{item.label}</option>)}
              </select>
            </div>
            <Campo value={formulario.origem} onChange={(valor) => setFormulario((atual) => ({ ...atual, origem: valor }))} placeholder="Origem" />
            <Campo value={formulario.potencialMensal} onChange={(valor) => setFormulario((atual) => ({ ...atual, potencialMensal: valor }))} placeholder="Potencial mensal" type="number" />
            <textarea
              value={formulario.observacoes}
              onChange={(event) => setFormulario((atual) => ({ ...atual, observacoes: event.target.value }))}
              placeholder="Observacoes comerciais"
              className="min-h-24 rounded-xl border bg-background px-3 py-2 text-sm outline-none ring-primary/20 transition focus:ring-4"
            />
            <button
              type="button"
              onClick={registrarNovoLead}
              disabled={registrarLeadMutation.isPending}
              className="inline-flex h-11 items-center justify-center gap-2 rounded-2xl bg-primary px-4 text-sm font-semibold text-primary-foreground transition hover:bg-primary/90 disabled:opacity-60"
            >
              {registrarLeadMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <CheckCircle2 className="h-4 w-4" />}
              Registrar lead
            </button>
          </div>
        </aside>
      </section>

      <section className="rounded-3xl border bg-card p-5 shadow-sm">
        <CabecalhoSecao
          icone={BadgeDollarSign}
          tag="TASK-R20-002"
          titulo="Dashboard executivo Nutri/Beauty"
          descricao="Indicadores para decidir onde vender mais, onde reter e onde melhorar margem."
          carregando={painelQuery.isFetching}
        />
        <div className="mt-5 grid gap-4 lg:grid-cols-2">
          {indicadores.map((indicador) => <IndicadorExecutivo key={indicador.vertical} indicador={indicador} />)}
          {!painelQuery.isLoading && indicadores.length === 0 ? <EstadoInline texto="Nenhum indicador Growth encontrado para esta empresa." /> : null}
        </div>
      </section>

      <section className="grid gap-5 xl:grid-cols-[minmax(0,1fr)_420px]">
        <div className="rounded-3xl border bg-card p-5 shadow-sm">
          <CabecalhoSecao
            icone={HeartHandshake}
            tag="TASK-R20-003"
            titulo="Central de pos-venda assistida"
            descricao="Clientes em risco, motivo, mensagem sugerida e oportunidade de pacote para acao rapida."
            carregando={painelQuery.isFetching}
          />
          <div className="mt-5 grid gap-3">
            {sugestoes.map((sugestao) => <SugestaoAssistida key={`${sugestao.clienteId}-${sugestao.tipo}`} sugestao={sugestao} />)}
            {!painelQuery.isLoading && sugestoes.length === 0 ? <EstadoInline texto="Nenhuma sugestao de pos-venda para o filtro atual." /> : null}
          </div>
        </div>

        <div className="rounded-3xl border bg-card p-5 shadow-sm">
          <CabecalhoSecao
            icone={Presentation}
            tag="TASK-R20-004"
            titulo="Modo apresentacao/demo"
            descricao="Roteiro navegavel por perfil para mostrar valor rapidamente."
          />
          <div className="mt-4 flex flex-wrap gap-2">
            {perfisDemo.map((perfil) => (
              <button
                key={perfil.id}
                type="button"
                onClick={() => setPerfilDemo(perfil.id)}
                className={`rounded-full border px-3 py-1.5 text-xs font-semibold transition ${perfilDemo === perfil.id ? "border-primary bg-primary text-primary-foreground" : "bg-background text-muted-foreground hover:text-foreground"}`}
              >
                {perfil.label}
              </button>
            ))}
          </div>
          <div className="mt-5 space-y-3">
            {apresentacoesDemo.map((demo) => <DemoCard key={demo.id} demo={demo} />)}
            {!painelQuery.isLoading && apresentacoesDemo.length === 0 ? <EstadoInline texto="Nenhum roteiro demo para este perfil ainda." /> : null}
          </div>
        </div>
      </section>
    </div>
  );
}

function invalidarGrowth(queryClient: ReturnType<typeof useQueryClient>, empresaId: string) {
  queryClient.invalidateQueries({ queryKey: ["growth-painel", empresaId] });
  queryClient.invalidateQueries({ queryKey: ["growth-leads", empresaId] });
}

function filtrarSugestoes(sugestoes: SugestaoPosVendaIA[], vertical: FiltroVertical) {
  return sugestoes
    .filter((sugestao) => vertical === "TODAS" || sugestao.vertical === vertical)
    .sort((a, b) => a.prioridade.localeCompare(b.prioridade));
}

function LeadCard({
  lead,
  vinculo,
  onVinculoChange,
  onSalvarVinculos,
  onAvancar,
  onVoltar,
  carregando
}: {
  lead: LeadGrowth;
  vinculo?: { clientePacienteId: string; compromissoAgendaId: string };
  onVinculoChange: (valor: { clientePacienteId: string; compromissoAgendaId: string }) => void;
  onSalvarVinculos: () => void;
  onAvancar: () => void;
  onVoltar: () => void;
  carregando: boolean;
}) {
  const valores = vinculo ?? {
    clientePacienteId: lead.clientePacienteId ?? "",
    compromissoAgendaId: lead.compromissoAgendaId ?? ""
  };

  return (
    <div className="rounded-2xl border bg-card p-3 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-card-foreground">{lead.nome}</p>
          <p className="truncate text-xs text-muted-foreground">{lead.email}</p>
        </div>
        <span className={`rounded-full px-2 py-1 text-[10px] font-bold ${lead.vertical === "NUTRI" ? "bg-emerald-100 text-emerald-700" : "bg-pink-100 text-pink-700"}`}>
          {lead.vertical}
        </span>
      </div>
      <div className="mt-3 grid gap-2 text-xs text-muted-foreground">
        <span>Origem: {lead.origem}</span>
        <span>Potencial: {formatarMoeda(lead.potencialMensal)}</span>
        {lead.observacoes ? <span className="line-clamp-2">{lead.observacoes}</span> : null}
      </div>
      <div className="mt-3 grid gap-2">
        <input
          value={valores.clientePacienteId}
          onChange={(event) => onVinculoChange({ ...valores, clientePacienteId: event.target.value })}
          placeholder="clientePacienteId"
          className="h-8 rounded-lg border bg-background px-2 text-xs outline-none focus:border-primary"
        />
        <input
          value={valores.compromissoAgendaId}
          onChange={(event) => onVinculoChange({ ...valores, compromissoAgendaId: event.target.value })}
          placeholder="compromissoAgendaId"
          className="h-8 rounded-lg border bg-background px-2 text-xs outline-none focus:border-primary"
        />
      </div>
      <div className="mt-3 flex flex-wrap gap-2">
        <button type="button" onClick={onVoltar} disabled={carregando} className="rounded-lg border px-2 py-1 text-xs font-semibold text-muted-foreground hover:text-foreground disabled:opacity-50">
          Voltar
        </button>
        <button type="button" onClick={onAvancar} disabled={carregando} className="rounded-lg border px-2 py-1 text-xs font-semibold text-primary hover:bg-primary/5 disabled:opacity-50">
          Avancar
        </button>
        <button type="button" onClick={onSalvarVinculos} disabled={carregando} className="rounded-lg bg-primary px-2 py-1 text-xs font-semibold text-primary-foreground disabled:opacity-50">
          Vincular
        </button>
      </div>
    </div>
  );
}

function IndicadorExecutivo({ indicador }: { indicador: IndicadorVerticalGrowth }) {
  const margem = Math.max(0, Math.min(indicador.margemMediaPercentual, 100));
  const recorrencia = Math.max(0, Math.min(indicador.recorrenciaPercentual, 100));

  return (
    <article className="rounded-2xl border bg-background p-5">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-xs font-bold uppercase tracking-[0.2em] text-primary">{indicador.vertical}</p>
          <h3 className="mt-1 text-lg font-semibold text-card-foreground">{formatarMoeda(indicador.faturamentoPrevisto)} previstos</h3>
          <p className="mt-2 text-sm leading-6 text-muted-foreground">{indicador.leituraExecutiva}</p>
        </div>
        <span className="rounded-2xl bg-primary/10 p-3 text-primary">
          <TrendingUp className="h-5 w-5" />
        </span>
      </div>
      <div className="mt-5 grid gap-3 sm:grid-cols-3">
        <MiniMetrica label="Clientes ativos" value={formatarNumero(indicador.clientesAtivos)} />
        <MiniMetrica label="Agenda 30 dias" value={formatarNumero(indicador.agendaProximos30Dias)} />
        <MiniMetrica label="Ticket medio" value={formatarMoeda(indicador.ticketMedio)} />
      </div>
      <div className="mt-5 grid gap-3">
        <BarraPercentual label="Margem media" value={margem} />
        <BarraPercentual label="Recorrencia" value={recorrencia} />
        <MiniMetrica label="Clientes com recompra" value={formatarNumero(indicador.clientesComRecompra)} />
      </div>
    </article>
  );
}

function SugestaoAssistida({ sugestao }: { sugestao: SugestaoPosVendaIA }) {
  return (
    <article className="rounded-2xl border bg-background p-4">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="font-semibold text-card-foreground">{sugestao.clienteNome}</p>
          <p className="mt-1 text-xs font-semibold uppercase tracking-[0.18em] text-primary">{sugestao.vertical} · {sugestao.tipo}</p>
        </div>
        <span className={prioridadeClasse(sugestao.prioridade)}>{sugestao.prioridade.replace("_", " ")}</span>
      </div>
      <p className="mt-3 text-sm leading-6 text-muted-foreground">{sugestao.motivo}</p>
      <div className="mt-3 rounded-xl border border-dashed bg-card p-3 text-sm leading-6 text-card-foreground">
        {sugestao.mensagemSugerida}
      </div>
      <div className="mt-3 grid gap-2 text-xs text-muted-foreground sm:grid-cols-2">
        <span>Retorno: {formatarData(sugestao.retornoRecomendadoEm)}</span>
        <span>Pacote: {sugestao.oportunidadePacote}</span>
      </div>
    </article>
  );
}

function DemoCard({ demo }: { demo: ApresentacaoDemoGrowth }) {
  return (
    <article className="rounded-2xl border bg-background p-4">
      <div className="flex items-start gap-3">
        <span className="rounded-2xl bg-primary/10 p-3 text-primary">
          <Presentation className="h-5 w-5" />
        </span>
        <div className="min-w-0">
          <h3 className="font-semibold text-card-foreground">{demo.titulo}</h3>
          <p className="mt-2 text-sm leading-6 text-muted-foreground">{demo.roteiro}</p>
        </div>
      </div>
      <div className="mt-4 rounded-xl bg-card p-3 text-sm leading-6 text-card-foreground">
        <strong>Metricas:</strong> {demo.metricasChave}
      </div>
      <div className="mt-3 flex items-center gap-2 text-sm font-semibold text-primary">
        {demo.chamadaParaAcao}
        <ArrowRight className="h-4 w-4" />
      </div>
    </article>
  );
}

function CabecalhoSecao({
  icone: Icon,
  tag,
  titulo,
  descricao,
  carregando
}: {
  icone: typeof Users;
  tag: string;
  titulo: string;
  descricao: string;
  carregando?: boolean;
}) {
  return (
    <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
      <div className="flex gap-3">
        <span className="mt-1 rounded-2xl bg-primary/10 p-3 text-primary">
          <Icon className="h-5 w-5" />
        </span>
        <div>
          <p className="text-xs font-bold uppercase tracking-[0.22em] text-primary">{tag}</p>
          <h2 className="mt-1 text-lg font-semibold text-card-foreground">{titulo}</h2>
          <p className="mt-1 max-w-3xl text-sm leading-6 text-muted-foreground">{descricao}</p>
        </div>
      </div>
      {carregando ? <LoaderCircle className="h-5 w-5 animate-spin text-muted-foreground" /> : null}
    </div>
  );
}

function MetricaHero({ icon: Icon, label, value }: { icon: typeof Users; label: string; value: string }) {
  return (
    <div className="min-w-36 rounded-2xl border border-white/15 bg-white/10 p-4 backdrop-blur">
      <Icon className="h-5 w-5 text-cyan-100" />
      <p className="mt-3 text-xs font-semibold uppercase tracking-[0.18em] text-cyan-100">{label}</p>
      <p className="mt-1 text-xl font-semibold text-white">{value}</p>
    </div>
  );
}

function Campo({ value, onChange, placeholder, type = "text" }: { value: string; onChange: (value: string) => void; placeholder: string; type?: string }) {
  return (
    <input
      value={value}
      onChange={(event) => onChange(event.target.value)}
      placeholder={placeholder}
      type={type}
      className="h-10 rounded-xl border bg-background px-3 text-sm outline-none ring-primary/20 transition focus:ring-4"
    />
  );
}

function MiniMetrica({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-xl border bg-card p-3">
      <p className="text-xs text-muted-foreground">{label}</p>
      <p className="mt-1 font-semibold text-card-foreground">{value}</p>
    </div>
  );
}

function BarraPercentual({ label, value }: { label: string; value: number }) {
  return (
    <div>
      <div className="mb-1 flex items-center justify-between text-xs font-semibold text-muted-foreground">
        <span>{label}</span>
        <span>{value.toFixed(0)}%</span>
      </div>
      <div className="h-2 overflow-hidden rounded-full bg-muted">
        <div className="h-full rounded-full bg-primary" style={{ width: `${value}%` }} />
      </div>
    </div>
  );
}

function EstadoGrowth({ titulo, descricao }: { titulo: string; descricao: string }) {
  return (
    <div className="flex min-h-72 flex-col items-center justify-center rounded-3xl border bg-card p-8 text-center shadow-sm">
      <ClipboardList className="h-9 w-9 text-primary" />
      <h2 className="mt-3 text-lg font-semibold text-card-foreground">{titulo}</h2>
      <p className="mt-1 max-w-md text-sm leading-6 text-muted-foreground">{descricao}</p>
    </div>
  );
}

function EstadoInline({ texto }: { texto: string }) {
  return <p className="rounded-2xl border border-dashed bg-background p-4 text-sm leading-6 text-muted-foreground">{texto}</p>;
}

function prioridadeClasse(prioridade: SugestaoPosVendaIA["prioridade"]) {
  if (prioridade === "1_ALTA") {
    return "rounded-full bg-red-100 px-2 py-1 text-xs font-bold text-red-700";
  }
  if (prioridade === "2_MEDIA") {
    return "rounded-full bg-amber-100 px-2 py-1 text-xs font-bold text-amber-700";
  }
  return "rounded-full bg-emerald-100 px-2 py-1 text-xs font-bold text-emerald-700";
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL", maximumFractionDigits: 0 }).format(valor);
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR").format(valor);
}

function formatarData(valor: string) {
  const data = new Date(valor);
  if (Number.isNaN(data.getTime())) {
    return valor;
  }
  return new Intl.DateTimeFormat("pt-BR", { dateStyle: "short" }).format(data);
}
