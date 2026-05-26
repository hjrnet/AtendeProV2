"use client";

import { useEffect, useMemo, useState, type FormEvent } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  AlertTriangle,
  Apple,
  Activity,
  CalendarDays,
  ChevronRight,
  Clock,
  ClipboardList,
  FileText,
  Gauge,
  LoaderCircle,
  Sparkles,
  Stethoscope,
  Users
} from "lucide-react";

import {
  consultarProntuarioNutriPro,
  consultarVisaoNutriPro,
  caminhoPdfDocumentoNutriPro,
  criarAvaliacaoAntropometricaNutriPro,
  criarDocumentoProfissionalNutriPro,
  criarPlanoAlimentarNutriPro,
  listarAgendaNutriPro,
  listarAvaliacoesAntropometricasNutriPro,
  listarDocumentosProfissionaisNutriPro,
  listarPacientesNutriPro,
  listarPlanosAlimentaresNutriPro,
  type CompromissoAgendaNutriPro,
  type AtalhoNutriPro,
  type AvaliacaoAntropometricaNutriPro,
  type CriarAvaliacaoAntropometricaNutriProInput,
  type CriarDocumentoProfissionalNutriProInput,
  type CriarPlanoAlimentarNutriProInput,
  type DocumentoProfissionalNutriPro,
  type IndicadorNutriPro,
  type ObjetivoNutricionalNutriPro,
  type PacienteNutriResumo,
  type PlanoAlimentarNutriPro,
  type ProntuarioNutriPro,
  type SexoBiologicoNutriPro,
  type StatusAgendaNutriPro,
  type VisaoNutriPro
} from "@/features/nutri-pro/api/nutri-pro-client";
import { carregarSessaoAutenticada } from "@/features/auth/lib/auth-storage";
import { cn } from "@/lib/utils";

type NutriProOperacionalViewProps = {
  empresaId: string;
  focoWorkspace?: FocoWorkspaceNutriPro;
};

type FocoWorkspaceNutriPro =
  | "nutri-inicio"
  | "nutri-agenda"
  | "nutri-pacientes"
  | "nutri-prontuario"
  | "nutri-plano"
  | "nutri-avaliacoes"
  | "nutri-documentos";

type Icone = typeof Apple;

type FormularioAvaliacaoNutri = {
  pesoKg: string;
  alturaCm: string;
  idade: string;
  sexo: SexoBiologicoNutriPro;
  objetivo: ObjetivoNutricionalNutriPro;
  fatorAtividade: string;
  observacoes: string;
};

type AbaProntuarioNutriPro = "resumo" | "anamnese" | "avaliacoes" | "plano" | "exames" | "prescricoes" | "documentos" | "historico";

type AbaPlanoNutriPro = "visao" | "planos" | "refeicoes" | "macros" | "pdf" | "historico";

type AbaAvaliacaoNutriPro = "antropometria" | "gasto" | "historico" | "evolucao";

const abasProntuarioNutri: Array<{ id: AbaProntuarioNutriPro; label: string }> = [
  { id: "resumo", label: "Resumo" },
  { id: "anamnese", label: "Anamnese" },
  { id: "avaliacoes", label: "Avaliações" },
  { id: "plano", label: "Plano" },
  { id: "exames", label: "Exames" },
  { id: "prescricoes", label: "Prescrições" },
  { id: "documentos", label: "Documentos" },
  { id: "historico", label: "Histórico" }
];

const abasPlanoNutri: Array<{ id: AbaPlanoNutriPro; label: string }> = [
  { id: "visao", label: "Visão geral" },
  { id: "planos", label: "Planos ativos" },
  { id: "refeicoes", label: "Refeições" },
  { id: "macros", label: "Macros" },
  { id: "pdf", label: "PDF" },
  { id: "historico", label: "Histórico" }
];

const abasAvaliacoesNutri: Array<{ id: AbaAvaliacaoNutriPro; label: string }> = [
  { id: "antropometria", label: "Antropometria" },
  { id: "gasto", label: "Gasto energético" },
  { id: "historico", label: "Histórico" },
  { id: "evolucao", label: "Evolução futura" }
];

const iconesIndicadores: Record<string, Icone> = {
  pacientes: Users,
  agendaHoje: CalendarDays,
  agenda7Dias: CalendarDays,
  servicos: Stethoscope,
  documentos: FileText,
  precificacao: Gauge,
  alertas: AlertTriangle,
  planos: Apple
};

const iconesAtalhos: Record<string, Icone> = {
  "gasto-energetico": Gauge,
  "exames-laboratoriais": ClipboardList,
  "plano-alimentar": Apple,
  prescricoes: FileText,
  prontuario: Users,
  avaliacao: Stethoscope,
  documentos: FileText
};

export function NutriProOperacionalView({ empresaId, focoWorkspace = "nutri-inicio" }: NutriProOperacionalViewProps) {
  const [buscaPaciente, setBuscaPaciente] = useState("");
  const [pacienteSelecionadoId, setPacienteSelecionadoId] = useState<string | null>(null);
  const intervaloAgenda = useMemo(() => criarIntervaloAgendaNutri(), []);

  const visaoQuery = useQuery({
    queryKey: ["nutri-pro-visao", empresaId],
    queryFn: () => consultarVisaoNutriPro(empresaId),
    enabled: Boolean(empresaId)
  });

  const pacientesQuery = useQuery({
    queryKey: ["nutri-pro-pacientes", empresaId, buscaPaciente],
    queryFn: () => listarPacientesNutriPro({ empresaId, busca: buscaPaciente }),
    enabled: Boolean(empresaId)
  });

  const agendaQuery = useQuery({
    queryKey: ["nutri-pro-agenda", empresaId, intervaloAgenda.inicio, intervaloAgenda.fim],
    queryFn: () => listarAgendaNutriPro({ empresaId, inicio: intervaloAgenda.inicio, fim: intervaloAgenda.fim }),
    enabled: Boolean(empresaId && ["nutri-inicio", "nutri-agenda"].includes(focoWorkspace))
  });

  const pacientes = pacientesQuery.data?.itens ?? [];

  useEffect(() => {
    if (!pacientes.length) {
      setPacienteSelecionadoId(null);
      return;
    }

    if (!pacienteSelecionadoId || !pacientes.some((paciente) => paciente.id === pacienteSelecionadoId)) {
      setPacienteSelecionadoId(pacientes[0].id);
    }
  }, [pacienteSelecionadoId, pacientes]);

  const indicadoresPrincipais = useMemo(
    () => (visaoQuery.data?.indicadores ?? []).filter((indicador) => ["pacientes", "agendaHoje", "agenda7Dias", "precificacao"].includes(indicador.codigo)),
    [visaoQuery.data]
  );

  const indicadoresApoio = useMemo(
    () => (visaoQuery.data?.indicadores ?? []).filter((indicador) => !["pacientes", "agendaHoje", "agenda7Dias", "precificacao"].includes(indicador.codigo)),
    [visaoQuery.data]
  );

  if (!empresaId) {
    return <EstadoNutriPro titulo="Selecione uma empresa" descricao="Escolha uma empresa para carregar a área operacional do Nutri Pro." />;
  }

  if (visaoQuery.isLoading) {
    return (
      <section className="rounded-lg border border-emerald-200 bg-emerald-50/45 p-4">
        <div className="flex min-h-44 items-center justify-center text-sm font-medium text-emerald-800">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando Nutri Pro
        </div>
      </section>
    );
  }

  if (visaoQuery.isError || !visaoQuery.data) {
    return <EstadoNutriPro titulo="Não foi possível carregar o Nutri Pro" descricao="Confira a sessão atual e tente novamente." alerta />;
  }

  const visao = visaoQuery.data;

  if (focoWorkspace === "nutri-agenda") {
    return <TelaAgendaNutriPro agenda={agendaQuery.data?.itens ?? []} carregando={agendaQuery.isLoading} />;
  }

  if (focoWorkspace === "nutri-pacientes") {
    return (
      <TelaPacientesNutriPro
        buscaPaciente={buscaPaciente}
        pacientes={pacientes}
        pacienteSelecionadoId={pacienteSelecionadoId}
        carregando={pacientesQuery.isLoading}
        onBuscar={setBuscaPaciente}
        onSelecionar={setPacienteSelecionadoId}
      />
    );
  }

  if (focoWorkspace === "nutri-prontuario") {
    return <TelaPacienteSelecionadoNutriPro empresaId={empresaId} pacienteId={pacienteSelecionadoId} modo="prontuario" />;
  }

  if (focoWorkspace === "nutri-plano") {
    return <TelaPacienteSelecionadoNutriPro empresaId={empresaId} pacienteId={pacienteSelecionadoId} modo="plano" />;
  }

  if (focoWorkspace === "nutri-avaliacoes") {
    return <TelaPacienteSelecionadoNutriPro empresaId={empresaId} pacienteId={pacienteSelecionadoId} modo="avaliacoes" />;
  }

  if (focoWorkspace === "nutri-documentos") {
    return <TelaPacienteSelecionadoNutriPro empresaId={empresaId} pacienteId={pacienteSelecionadoId} modo="documentos" />;
  }

  return (
    <TelaInicioNutriPro
      visao={visao}
      indicadoresPrincipais={indicadoresPrincipais}
      indicadoresApoio={indicadoresApoio}
      agenda={agendaQuery.data?.itens ?? []}
      agendaCarregando={agendaQuery.isLoading}
      pacientes={pacientes}
      pacienteSelecionadoId={pacienteSelecionadoId}
      onSelecionarPaciente={setPacienteSelecionadoId}
    />
  );
}

function TelaInicioNutriPro({
  visao,
  indicadoresPrincipais,
  indicadoresApoio,
  agenda,
  agendaCarregando,
  pacientes,
  pacienteSelecionadoId,
  onSelecionarPaciente
}: {
  visao: VisaoNutriPro;
  indicadoresPrincipais: IndicadorNutriPro[];
  indicadoresApoio: IndicadorNutriPro[];
  agenda: CompromissoAgendaNutriPro[];
  agendaCarregando: boolean;
  pacientes: PacienteNutriResumo[];
  pacienteSelecionadoId: string | null;
  onSelecionarPaciente: (pacienteId: string) => void;
}) {
  return (
    <section className="grid min-w-0 gap-4">
      <div className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
        <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
          <div className="min-w-0">
            <p className="text-sm font-semibold text-emerald-800">Workspace Nutri Pro</p>
            <h4 className="mt-1 text-xl font-semibold text-card-foreground">{visao.empresaNome}</h4>
            <p className="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">{visao.mensagemStatus}</p>
          </div>
          <span className={cn("inline-flex w-fit items-center gap-2 rounded-md border px-3 py-2 text-xs font-semibold", visao.statusOperacional === "OPERACIONAL" ? "border-emerald-200 bg-white text-emerald-800" : "border-amber-200 bg-amber-50 text-amber-800")}>
            <Sparkles className="h-4 w-4" />
            {visao.statusOperacionalRotulo}
          </span>
        </div>
      </div>

      <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        {indicadoresPrincipais.map((indicador) => (
          <CardIndicadorNutri key={indicador.codigo} indicador={indicador} />
        ))}
      </div>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_360px]">
        <section className="grid gap-4">
          <div className="rounded-lg border bg-white p-4 shadow-sm">
            <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Ações prioritárias</p>
                <p className="text-sm leading-6 text-muted-foreground">Use atalhos compactos para iniciar os fluxos mais frequentes do atendimento.</p>
              </div>
              <span className="w-fit rounded-md border border-sky-200 bg-sky-50 px-2 py-1 text-xs font-semibold text-sky-800">Menu Nutri</span>
            </div>
            <div className="mt-4 grid gap-3 md:grid-cols-3">
              {visao.atalhosPrioritarios.map((atalho) => (
                <CardAtalhoNutri key={atalho.codigo} atalho={atalho} principal />
              ))}
            </div>
          </div>

          <div className="grid gap-3 xl:grid-cols-2">
            <GrupoNutri titulo="Indicadores de apoio" itens={indicadoresApoio} />
            <div className="rounded-lg border bg-white p-4 shadow-sm">
              <p className="text-sm font-semibold text-card-foreground">Próximas evoluções</p>
              <div className="mt-3 grid gap-2">
                {visao.proximasEvolucoes.slice(0, 3).map((atalho) => (
                  <CardAtalhoNutri key={atalho.codigo} atalho={atalho} />
                ))}
              </div>
            </div>
          </div>
        </section>

        <aside className="grid gap-4">
          <section className="rounded-lg border border-sky-100 bg-sky-50/50 p-4 shadow-sm">
            <div className="mb-3 flex items-start justify-between gap-3 border-b border-sky-100 pb-3">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Agenda resumida</p>
                <p className="text-xs text-muted-foreground">Hoje e próximos compromissos.</p>
              </div>
              <CalendarDays className="h-5 w-5 text-sky-800" />
            </div>
            <ListaAgendaNutri agenda={agenda.slice(0, 4)} carregando={agendaCarregando} vazio="Nenhum atendimento nutricional no período." compacta />
          </section>

          <section className="rounded-lg border bg-white p-4 shadow-sm">
            <div className="mb-3 flex items-start justify-between gap-3 border-b pb-3">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Pacientes recentes</p>
                <p className="text-xs text-muted-foreground">Selecione para manter o contexto do paciente.</p>
              </div>
              <Users className="h-5 w-5 text-emerald-800" />
            </div>
            <div className="grid gap-2">
              {pacientes.slice(0, 4).map((paciente) => (
                <LinhaPacienteNutri
                  key={paciente.id}
                  paciente={paciente}
                  selecionado={paciente.id === pacienteSelecionadoId}
                  onSelecionar={() => onSelecionarPaciente(paciente.id)}
                />
              ))}
            </div>
          </section>
        </aside>
      </div>
    </section>
  );
}

function TelaAgendaNutriPro({ agenda, carregando }: { agenda: CompromissoAgendaNutriPro[]; carregando: boolean }) {
  const [buscaAgenda, setBuscaAgenda] = useState("");
  const [statusAgenda, setStatusAgenda] = useState<StatusAgendaNutriPro | "TODOS">("TODOS");
  const agendaFiltrada = filtrarAgendaNutri(agenda, buscaAgenda, statusAgenda);
  const compromissosHoje = agenda.filter((compromisso) => ehMesmoDia(new Date(compromisso.inicio), new Date())).length;

  return (
    <section className="grid min-w-0 gap-4">
      <div className="grid gap-3 md:grid-cols-3">
        <ResumoAgendaNutri icon={CalendarDays} label="Hoje" value={compromissosHoje} description="Atendimentos nutricionais previstos para hoje." />
        <ResumoAgendaNutri icon={Clock} label="Próximos 7 dias" value={agenda.length} description="Compromissos carregados no período." />
        <ResumoAgendaNutri icon={Activity} label="Lista ativa" value={agendaFiltrada.length} description="Resultado após busca e filtro." />
      </div>

      <section className="rounded-lg border bg-white p-4 shadow-sm">
        <div className="flex flex-col gap-3 border-b pb-4 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <p className="text-sm font-semibold text-emerald-800">Agenda nutricional</p>
            <h4 className="mt-1 text-xl font-semibold text-card-foreground">Atendimentos e retornos</h4>
            <p className="mt-2 max-w-2xl text-sm leading-6 text-muted-foreground">Lista dedicada de compromissos do núcleo comum, sem carregar prontuário ou plano alimentar junto.</p>
          </div>
          <div className="grid gap-2 sm:grid-cols-[minmax(220px,1fr)_180px]">
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Busca
              <input
                value={buscaAgenda}
                onChange={(event) => setBuscaAgenda(event.target.value)}
                className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                placeholder="Profissional, sala ou observação"
              />
            </label>
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Status
              <select
                value={statusAgenda}
                onChange={(event) => setStatusAgenda(event.target.value as StatusAgendaNutriPro | "TODOS")}
                className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              >
                <option value="TODOS">Todos</option>
                <option value="AGENDADO">Agendado</option>
                <option value="CONFIRMADO">Confirmado</option>
                <option value="REALIZADO">Realizado</option>
                <option value="CANCELADO">Cancelado</option>
                <option value="FALTOU">Faltou</option>
                <option value="REMARCADO">Remarcado</option>
              </select>
            </label>
          </div>
        </div>
        <ListaAgendaNutri agenda={agendaFiltrada} carregando={carregando} vazio="Nenhum compromisso encontrado para os filtros atuais." />
      </section>
    </section>
  );
}

function TelaPacientesNutriPro({
  buscaPaciente,
  pacientes,
  pacienteSelecionadoId,
  carregando,
  onBuscar,
  onSelecionar
}: {
  buscaPaciente: string;
  pacientes: PacienteNutriResumo[];
  pacienteSelecionadoId: string | null;
  carregando: boolean;
  onBuscar: (valor: string) => void;
  onSelecionar: (pacienteId: string) => void;
}) {
  const pacienteSelecionado = pacientes.find((paciente) => paciente.id === pacienteSelecionadoId) ?? pacientes[0] ?? null;

  return (
    <section className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_360px]">
      <section className="rounded-lg border bg-white p-4 shadow-sm">
        <div className="flex flex-col gap-3 border-b pb-4 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <p className="text-sm font-semibold text-emerald-800">Pacientes Nutri</p>
            <h4 className="mt-1 text-xl font-semibold text-card-foreground">Lista de pacientes</h4>
            <p className="mt-2 max-w-2xl text-sm leading-6 text-muted-foreground">Busque e selecione pacientes sem abrir o prontuário completo automaticamente.</p>
          </div>
          <label className="grid gap-1 text-sm font-medium text-card-foreground lg:min-w-80">
            Busca
            <input
              value={buscaPaciente}
              onChange={(event) => onBuscar(event.target.value)}
              className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Nome, email ou telefone"
            />
          </label>
        </div>
        <div className="mt-4 grid gap-3 md:grid-cols-2 2xl:grid-cols-3">
          {carregando ? (
            <div className="flex min-h-32 items-center justify-center rounded-md border bg-background text-sm text-muted-foreground md:col-span-2 2xl:col-span-3">
              <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
              Carregando pacientes
            </div>
          ) : pacientes.length ? (
            pacientes.map((paciente) => (
              <LinhaPacienteNutri
                key={paciente.id}
                paciente={paciente}
                selecionado={paciente.id === pacienteSelecionadoId}
                onSelecionar={() => onSelecionar(paciente.id)}
              />
            ))
          ) : (
            <div className="rounded-md border bg-background p-4 text-sm text-muted-foreground md:col-span-2 2xl:col-span-3">Nenhum paciente de nutrição encontrado nesta empresa.</div>
          )}
        </div>
      </section>

      <aside className="rounded-lg border border-sky-100 bg-sky-50/55 p-4 shadow-sm">
        <p className="text-sm font-semibold text-card-foreground">Paciente selecionado</p>
        {pacienteSelecionado ? (
          <div className="mt-4 rounded-lg border bg-white p-4">
            <p className="text-lg font-semibold text-card-foreground">{pacienteSelecionado.nome}</p>
            <p className="mt-1 text-sm text-muted-foreground">{pacienteSelecionado.telefone ?? "Sem telefone cadastrado"}</p>
            {pacienteSelecionado.observacoes ? <p className="mt-3 text-sm leading-6 text-muted-foreground">{pacienteSelecionado.observacoes}</p> : null}
            <p className="mt-4 rounded-md border border-emerald-200 bg-emerald-50 p-3 text-xs leading-5 text-emerald-900">
              Para abrir dados clínicos, use o menu Prontuário. Para plano, avaliação ou documentos, use a área dedicada no menu lateral.
            </p>
          </div>
        ) : (
          <div className="mt-4 rounded-md border bg-white p-4 text-sm text-muted-foreground">Selecione um paciente na lista.</div>
        )}
      </aside>
    </section>
  );
}

function TelaPacienteSelecionadoNutriPro({
  empresaId,
  pacienteId,
  modo
}: {
  empresaId: string;
  pacienteId: string | null;
  modo: "prontuario" | "plano" | "avaliacoes" | "documentos";
}) {
  return <ProntuarioNutriProPainel empresaId={empresaId} pacienteId={pacienteId} modo={modo} />;
}

function ProntuarioNutriProPainel({
  empresaId,
  pacienteId,
  modo
}: {
  empresaId: string;
  pacienteId: string | null;
  modo: "prontuario" | "plano" | "avaliacoes" | "documentos";
}) {
  const [submenuProntuarioAtivo, definirSubmenuProntuarioAtivo] = useState<AbaProntuarioNutriPro>("resumo");
  const [submenuPlanoAtivo, definirSubmenuPlanoAtivo] = useState<AbaPlanoNutriPro>("visao");
  const [submenuAvaliacaoAtivo, definirSubmenuAvaliacaoAtivo] = useState<AbaAvaliacaoNutriPro>("antropometria");

  const prontuarioQuery = useQuery({
    queryKey: ["nutri-pro-prontuario", empresaId, pacienteId],
    queryFn: () => consultarProntuarioNutriPro({ empresaId, pacienteId: pacienteId ?? "" }),
    enabled: Boolean(empresaId && pacienteId)
  });

  useEffect(() => {
    definirSubmenuProntuarioAtivo("resumo");
    definirSubmenuPlanoAtivo("visao");
    definirSubmenuAvaliacaoAtivo("antropometria");
  }, [modo, pacienteId]);

  if (!pacienteId) {
    return <EstadoNutriPro titulo="Selecione um paciente" descricao="Escolha um paciente na tela Pacientes para abrir esta área com contexto clínico." />;
  }

  if (prontuarioQuery.isLoading) {
    return (
      <section className="rounded-lg border bg-white p-4 shadow-sm">
        <div className="flex min-h-44 items-center justify-center text-sm text-muted-foreground">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando prontuário
        </div>
      </section>
    );
  }

  if (prontuarioQuery.isError || !prontuarioQuery.data) {
    return <EstadoNutriPro titulo="Prontuário indisponível" descricao="Não foi possível abrir o prontuário deste paciente." alerta />;
  }

  const prontuario = prontuarioQuery.data;

  return (
    <section className="grid gap-4">
      <CabecalhoPacienteNutri prontuario={prontuario} modo={modo} />

      {modo === "prontuario" ? (
        <section className="min-w-0 overflow-hidden rounded-lg border bg-white p-4 shadow-sm">
          <AbasNutri<AbaProntuarioNutriPro> abas={abasProntuarioNutri} ativa={submenuProntuarioAtivo} onSelecionar={definirSubmenuProntuarioAtivo} />
          <div className="mt-4">
            <ConteudoProntuarioNutri empresaId={empresaId} prontuario={prontuario} aba={submenuProntuarioAtivo} />
          </div>
        </section>
      ) : null}

      {modo === "plano" ? (
        <AreaPlanoAlimentarNutri empresaId={empresaId} prontuario={prontuario} abaAtiva={submenuPlanoAtivo} onSelecionarAba={definirSubmenuPlanoAtivo} />
      ) : null}

      {modo === "avaliacoes" ? (
        <AreaAvaliacoesNutri empresaId={empresaId} prontuario={prontuario} abaAtiva={submenuAvaliacaoAtivo} onSelecionarAba={definirSubmenuAvaliacaoAtivo} />
      ) : null}

      {modo === "documentos" ? <AreaDocumentosNutri empresaId={empresaId} prontuario={prontuario} /> : null}
    </section>
  );
}

function CabecalhoPacienteNutri({ prontuario, modo }: { prontuario: ProntuarioNutriPro; modo: "prontuario" | "plano" | "avaliacoes" | "documentos" }) {
  const titulos: Record<typeof modo, { titulo: string; descricao: string; cor: string }> = {
    prontuario: {
      titulo: "Prontuário nutricional",
      descricao: "Central do paciente com resumo e submenus clínicos.",
      cor: "border-emerald-200 bg-emerald-50"
    },
    plano: {
      titulo: "Plano alimentar",
      descricao: "Planos, refeições, macronutrientes, PDF e histórico em área própria.",
      cor: "border-lime-200 bg-lime-50"
    },
    avaliacoes: {
      titulo: "Avaliações",
      descricao: "Antropometria, gasto energético, histórico e evolução sem misturar outros fluxos.",
      cor: "border-amber-200 bg-amber-50"
    },
    documentos: {
      titulo: "Exames e documentos",
      descricao: "Solicitações, prescrições, PDFs e documentos profissionais do paciente.",
      cor: "border-sky-200 bg-sky-50"
    }
  };
  const contexto = titulos[modo];

  return (
    <section className={cn("min-w-0 overflow-hidden rounded-lg border p-4 shadow-sm", contexto.cor)}>
      <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
        <div className="min-w-0">
          <p className="text-sm font-semibold text-emerald-800">{contexto.titulo}</p>
          <h4 className="mt-1 text-2xl font-semibold text-card-foreground">{prontuario.paciente.nome}</h4>
          <p className="mt-2 max-w-3xl break-words text-sm leading-6 text-muted-foreground">
            {textoPaciente(prontuario)} · {contexto.descricao}
          </p>
        </div>
        <span className={cn("w-fit rounded-md border bg-white px-3 py-2 text-xs font-semibold", prontuario.paciente.ativo ? "text-emerald-800" : "text-slate-700")}>
          {prontuario.paciente.ativo ? "Paciente ativo" : "Paciente inativo"}
        </span>
      </div>
    </section>
  );
}

function AbasNutri<T extends string>({
  abas,
  ativa,
  onSelecionar
}: {
  abas: Array<{ id: T; label: string }>;
  ativa: T;
  onSelecionar: (aba: T) => void;
}) {
  return (
    <div className="flex w-full min-w-0 max-w-full gap-2 overflow-x-auto pb-1" role="tablist">
      {abas.map((aba) => (
        <button
          key={aba.id}
          type="button"
          role="tab"
          aria-selected={ativa === aba.id}
          onClick={() => onSelecionar(aba.id)}
          className={cn(
            "h-10 shrink-0 rounded-md border px-3 text-sm font-semibold transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
            ativa === aba.id ? "border-primary bg-primary text-primary-foreground shadow-sm" : "bg-background text-muted-foreground hover:border-primary/50 hover:text-card-foreground"
          )}
        >
          {aba.label}
        </button>
      ))}
    </div>
  );
}

function ConteudoProntuarioNutri({ empresaId, prontuario, aba }: { empresaId: string; prontuario: ProntuarioNutriPro; aba: AbaProntuarioNutriPro }) {
  if (aba === "anamnese") {
    return <PainelSimplesNutri titulo="Anamnese" descricao="Registro preparado para queixas, rotina, preferências, sintomas e contexto do acompanhamento." icon={ClipboardList} />;
  }

  if (aba === "avaliacoes") {
    return <FluxoAvaliacaoEGastoEnergetico empresaId={empresaId} prontuario={prontuario} />;
  }

  if (aba === "plano") {
    return <AreaPlanoAlimentarNutri empresaId={empresaId} prontuario={prontuario} abaAtiva="visao" onSelecionarAba={() => undefined} semAbas />;
  }

  if (aba === "exames") {
    return <FluxoExamesLaboratoriais empresaId={empresaId} prontuario={prontuario} />;
  }

  if (aba === "prescricoes") {
    return <FluxoPrescricoes empresaId={empresaId} prontuario={prontuario} />;
  }

  if (aba === "documentos") {
    return <DocumentosGeraisNutri empresaId={empresaId} prontuario={prontuario} />;
  }

  if (aba === "historico") {
    return (
      <div className="grid gap-3 md:grid-cols-2">
        <ResumoProntuarioCard label="Consultas futuras" value={prontuario.resumo.consultasFuturas} />
        <ResumoProntuarioCard label="Documentos emitidos" value={prontuario.resumo.documentos} />
        <ResumoProntuarioCard label="Simulações Nutri" value={prontuario.resumo.simulacoesPrecificacao} />
        <ResumoProntuarioCard label="Planos ativos" value={prontuario.resumo.planosAlimentaresAtivos} />
      </div>
    );
  }

  return (
    <div className="grid gap-4 lg:grid-cols-[minmax(0,0.9fr)_minmax(300px,0.6fr)]">
      <section className="rounded-lg border bg-background p-4">
        <p className="text-sm font-semibold text-card-foreground">Resumo do paciente</p>
        <div className="mt-4 grid gap-3 sm:grid-cols-2">
          <ResumoProntuarioCard label="Consultas futuras" value={prontuario.resumo.consultasFuturas} />
          <ResumoProntuarioCard label="Documentos" value={prontuario.resumo.documentos} />
          <ResumoProntuarioCard label="Planos ativos" value={prontuario.resumo.planosAlimentaresAtivos} />
          <ResumoProntuarioCard label="Simulações Nutri" value={prontuario.resumo.simulacoesPrecificacao} />
        </div>
      </section>
      <section className="rounded-lg border border-sky-100 bg-sky-50/60 p-4">
        <p className="text-sm font-semibold text-card-foreground">Ações organizadas por menu</p>
        <p className="mt-2 text-sm leading-6 text-muted-foreground">
          Plano alimentar, avaliações, exames e documentos agora ficam nas áreas dedicadas do menu lateral. O prontuário mostra o contexto do paciente sem empilhar todos os fluxos.
        </p>
      </section>
    </div>
  );
}

function AreaPlanoAlimentarNutri({
  empresaId,
  prontuario,
  abaAtiva,
  onSelecionarAba,
  semAbas = false
}: {
  empresaId: string;
  prontuario: ProntuarioNutriPro;
  abaAtiva: AbaPlanoNutriPro;
  onSelecionarAba: (aba: AbaPlanoNutriPro) => void;
  semAbas?: boolean;
}) {
  const queryClient = useQueryClient();
  const planosQuery = useQuery({
    queryKey: ["nutri-pro-planos-alimentares", empresaId, prontuario.paciente.id],
    queryFn: () => listarPlanosAlimentaresNutriPro({ empresaId, pacienteId: prontuario.paciente.id }),
    enabled: Boolean(empresaId && prontuario.paciente.id)
  });
  const documentosPlanoQuery = useQuery({
    queryKey: ["nutri-pro-documentos", empresaId, prontuario.paciente.id, "PLANO_ALIMENTAR"],
    queryFn: () => listarDocumentosProfissionaisNutriPro({ empresaId, pacienteId: prontuario.paciente.id, tipo: "PLANO_ALIMENTAR" }),
    enabled: Boolean(empresaId && prontuario.paciente.id)
  });
  const criarPlanoMutation = useMutation({
    mutationFn: () =>
      criarPlanoAlimentarNutriPro({
        empresaId,
        pacienteId: prontuario.paciente.id,
        dados: criarPlanoAlimentarInicial()
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-planos-alimentares", empresaId, prontuario.paciente.id] });
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-prontuario", empresaId, prontuario.paciente.id] });
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-visao", empresaId] });
    }
  });

  const planos = planosQuery.data?.itens ?? [];
  const planoEmFoco = criarPlanoMutation.data ?? planos[0] ?? null;
  const documentosPlano = documentosPlanoQuery.data?.itens ?? [];

  const criarDocumentoPlanoMutation = useMutation({
    mutationFn: () => {
      if (!planoEmFoco) {
        throw new Error("Plano alimentar não selecionado.");
      }
      return criarDocumentoProfissionalNutriPro(criarDocumentoPlanoAlimentar(empresaId, prontuario, planoEmFoco));
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-documentos", empresaId, prontuario.paciente.id] });
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-prontuario", empresaId, prontuario.paciente.id] });
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-visao", empresaId] });
    }
  });

  const abaRenderizada = semAbas ? "visao" : abaAtiva;

  return (
    <section className="min-w-0 overflow-hidden rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-3 border-b pb-4 lg:flex-row lg:items-start lg:justify-between">
        <div>
          <p className="text-sm font-semibold text-lime-800">Área de plano alimentar</p>
          <p className="mt-1 text-sm leading-6 text-muted-foreground">Cada submenu mostra somente uma parte do fluxo para evitar tela poluída.</p>
        </div>
        <button
          type="button"
          onClick={() => criarPlanoMutation.mutate()}
          disabled={criarPlanoMutation.isPending}
          className="inline-flex h-10 shrink-0 items-center justify-center rounded-md bg-primary px-4 text-sm font-semibold text-primary-foreground transition-colors hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {criarPlanoMutation.isPending ? "Criando plano" : "Criar plano inicial"}
        </button>
      </div>
      {!semAbas ? <div className="mt-4"><AbasNutri<AbaPlanoNutriPro> abas={abasPlanoNutri} ativa={abaAtiva} onSelecionar={onSelecionarAba} /></div> : null}
      <div className="mt-4">
        <ConteudoPlanoNutri
          aba={abaRenderizada}
          plano={planoEmFoco}
          planos={planos}
          documentosPlano={documentosPlano}
          carregando={planosQuery.isLoading || documentosPlanoQuery.isLoading}
          criandoDocumento={criarDocumentoPlanoMutation.isPending}
          documentoCriado={criarDocumentoPlanoMutation.data ?? null}
          erroDocumento={criarDocumentoPlanoMutation.isError}
          onCriarDocumento={() => criarDocumentoPlanoMutation.mutate()}
        />
      </div>
    </section>
  );
}

function ConteudoPlanoNutri({
  aba,
  plano,
  planos,
  documentosPlano,
  carregando,
  criandoDocumento,
  documentoCriado,
  erroDocumento,
  onCriarDocumento
}: {
  aba: AbaPlanoNutriPro;
  plano: PlanoAlimentarNutriPro | null;
  planos: PlanoAlimentarNutriPro[];
  documentosPlano: DocumentoProfissionalNutriPro[];
  carregando: boolean;
  criandoDocumento: boolean;
  documentoCriado: DocumentoProfissionalNutriPro | null;
  erroDocumento: boolean;
  onCriarDocumento: () => void;
}) {
  if (carregando) {
    return <EstadoCarregandoNutri texto="Carregando planos alimentares" />;
  }

  if (!plano && aba !== "historico" && aba !== "planos") {
    return <EstadoNutriPro titulo="Nenhum plano alimentar" descricao="Crie o primeiro plano para liberar refeições, macros e PDF." />;
  }

  if (aba === "planos" || aba === "historico") {
    return <ListaPlanosNutri planos={planos} />;
  }

  if (aba === "refeicoes" && plano) {
    return <ListaRefeicoesPlano plano={plano} />;
  }

  if (aba === "macros" && plano) {
    return <ResumoMacrosPlano plano={plano} />;
  }

  if (aba === "pdf") {
    return (
      <section className="rounded-lg border bg-background p-4">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
          <div>
            <p className="text-sm font-semibold text-card-foreground">Documento e PDF</p>
            <p className="mt-1 text-sm leading-6 text-muted-foreground">Gere o documento profissional do plano com carimbo CRN pelo módulo de documentos.</p>
          </div>
          <button
            type="button"
            onClick={onCriarDocumento}
            disabled={criandoDocumento || !plano}
            className="inline-flex h-10 shrink-0 items-center justify-center rounded-md border bg-white px-4 text-sm font-semibold text-card-foreground transition-colors hover:border-primary/50 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {criandoDocumento ? "Gerando documento" : "Gerar documento do plano"}
          </button>
        </div>
        {erroDocumento ? <p className="mt-3 text-sm font-medium text-destructive">Não foi possível gerar o documento do plano.</p> : null}
        {documentoCriado ? <ResumoDocumentoNutri documento={documentoCriado} compacto /> : null}
        {!documentoCriado && documentosPlano[0] ? <ResumoDocumentoNutri documento={documentosPlano[0]} compacto /> : null}
        <ListaDocumentosNutri titulo="Histórico de PDFs do plano" documentos={documentosPlano} carregando={false} vazio="Nenhum PDF de plano alimentar registrado ainda." />
      </section>
    );
  }

  return plano ? <ResumoPlanoCompacto plano={plano} /> : <ListaPlanosNutri planos={planos} />;
}

function AreaAvaliacoesNutri({
  empresaId,
  prontuario,
  abaAtiva,
  onSelecionarAba
}: {
  empresaId: string;
  prontuario: ProntuarioNutriPro;
  abaAtiva: AbaAvaliacaoNutriPro;
  onSelecionarAba: (aba: AbaAvaliacaoNutriPro) => void;
}) {
  return (
    <section className="min-w-0 overflow-hidden rounded-lg border bg-white p-4 shadow-sm">
      <div className="border-b pb-4">
        <p className="text-sm font-semibold text-amber-800">Área de avaliações</p>
        <p className="mt-1 text-sm leading-6 text-muted-foreground">Antropometria e gasto energético ficam em tela própria, sem plano alimentar e documentos ao lado.</p>
      </div>
      <div className="mt-4">
        <AbasNutri<AbaAvaliacaoNutriPro> abas={abasAvaliacoesNutri} ativa={abaAtiva} onSelecionar={onSelecionarAba} />
      </div>
      <div className="mt-4">
        {abaAtiva === "antropometria" ? <FluxoAvaliacaoEGastoEnergetico empresaId={empresaId} prontuario={prontuario} /> : null}
        {abaAtiva === "gasto" ? <PainelSimplesNutri titulo="Gasto energético" descricao="Use a aba Antropometria para registrar peso, altura e fator de atividade. O resultado estimado exibe TMB, GEB, GET e meta energética." icon={Gauge} /> : null}
        {abaAtiva === "historico" ? <HistoricoAvaliacoesNutri empresaId={empresaId} prontuario={prontuario} /> : null}
        {abaAtiva === "evolucao" ? <PainelSimplesNutri titulo="Evolução futura" descricao="Gráficos comparativos, fotos seguras e análise longitudinal entram em evolução futura do Nutri Pro." icon={Activity} /> : null}
      </div>
    </section>
  );
}

function AreaDocumentosNutri({ empresaId, prontuario }: { empresaId: string; prontuario: ProntuarioNutriPro }) {
  const [abaDocumentoAtiva, definirAbaDocumentoAtiva] = useState<"exames" | "prescricoes" | "documentos">("exames");
  const abasDocumento: Array<{ id: "exames" | "prescricoes" | "documentos"; label: string }> = [
    { id: "exames", label: "Exames" },
    { id: "prescricoes", label: "Prescrições" },
    { id: "documentos", label: "Histórico" }
  ];

  return (
    <section className="min-w-0 overflow-hidden rounded-lg border bg-white p-4 shadow-sm">
      <div className="border-b pb-4">
        <p className="text-sm font-semibold text-sky-800">Área de documentos</p>
        <p className="mt-1 text-sm leading-6 text-muted-foreground">Solicitações, prescrições e PDFs ficam organizados por submenu.</p>
      </div>
      <div className="mt-4">
        <AbasNutri abas={abasDocumento} ativa={abaDocumentoAtiva} onSelecionar={definirAbaDocumentoAtiva} />
      </div>
      <div className="mt-4">
        {abaDocumentoAtiva === "exames" ? <FluxoExamesLaboratoriais empresaId={empresaId} prontuario={prontuario} /> : null}
        {abaDocumentoAtiva === "prescricoes" ? <FluxoPrescricoes empresaId={empresaId} prontuario={prontuario} /> : null}
        {abaDocumentoAtiva === "documentos" ? <DocumentosGeraisNutri empresaId={empresaId} prontuario={prontuario} /> : null}
      </div>
    </section>
  );
}

function DocumentosGeraisNutri({ empresaId, prontuario }: { empresaId: string; prontuario: ProntuarioNutriPro }) {
  const documentosQuery = useQuery({
    queryKey: ["nutri-pro-documentos", empresaId, prontuario.paciente.id, "TODOS"],
    queryFn: () => listarDocumentosProfissionaisNutriPro({ empresaId, pacienteId: prontuario.paciente.id }),
    enabled: Boolean(empresaId && prontuario.paciente.id)
  });
  return (
    <ListaDocumentosNutri
      titulo="Documentos do paciente"
      documentos={documentosQuery.data?.itens ?? []}
      carregando={documentosQuery.isLoading}
      vazio="Nenhum documento profissional registrado para este paciente."
    />
  );
}

function HistoricoAvaliacoesNutri({ empresaId, prontuario }: { empresaId: string; prontuario: ProntuarioNutriPro }) {
  const avaliacoesQuery = useQuery({
    queryKey: ["nutri-pro-avaliacoes-antropometricas", empresaId, prontuario.paciente.id],
    queryFn: () => listarAvaliacoesAntropometricasNutriPro({ empresaId, pacienteId: prontuario.paciente.id }),
    enabled: Boolean(empresaId && prontuario.paciente.id)
  });
  const avaliacoes = avaliacoesQuery.data?.itens ?? [];
  return (
    <section className="rounded-lg border bg-background p-4">
      <div className="flex items-center justify-between gap-3">
        <p className="text-sm font-semibold text-card-foreground">Histórico de avaliações</p>
        {avaliacoesQuery.isLoading ? <LoaderCircle className="h-4 w-4 animate-spin text-muted-foreground" /> : null}
      </div>
      <div className="mt-3 grid gap-2">
        {avaliacoes.length ? avaliacoes.map((avaliacao) => <LinhaAvaliacaoNutri key={avaliacao.id} avaliacao={avaliacao} />) : <div className="rounded-md border bg-white p-3 text-sm text-muted-foreground">Nenhuma avaliação registrada ainda.</div>}
      </div>
    </section>
  );
}

function ResumoAgendaNutri({ icon: Icon, label, value, description }: { icon: Icone; label: string; value: number; description: string }) {
  return (
    <article className="rounded-lg border border-sky-100 bg-sky-50/60 p-4 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-sky-800">
          <Icon className="h-5 w-5" />
        </span>
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-sky-800">Agenda</span>
      </div>
      <p className="mt-4 text-sm font-medium text-muted-foreground">{label}</p>
      <p className="mt-1 text-2xl font-semibold text-card-foreground">{formatarNumero(value)}</p>
      <p className="mt-2 text-xs leading-5 text-muted-foreground">{description}</p>
    </article>
  );
}

function ListaAgendaNutri({
  agenda,
  carregando,
  vazio,
  compacta = false
}: {
  agenda: CompromissoAgendaNutriPro[];
  carregando: boolean;
  vazio: string;
  compacta?: boolean;
}) {
  if (carregando) {
    return <EstadoCarregandoNutri texto="Carregando agenda" />;
  }

  if (!agenda.length) {
    return <div className="rounded-md border bg-white p-4 text-sm leading-6 text-muted-foreground">{vazio}</div>;
  }

  return (
    <div className={cn("mt-4 grid gap-2", compacta ? "" : "md:grid-cols-2")}>
      {agenda.map((compromisso) => (
        <article key={compromisso.id} className="rounded-lg border bg-white p-3 text-sm">
          <div className="flex items-start justify-between gap-3">
            <div className="min-w-0">
              <p className="font-semibold text-card-foreground">{formatarAgendaPeriodo(compromisso.inicio, compromisso.fim)}</p>
              <p className="mt-1 text-xs leading-5 text-muted-foreground">{compromisso.profissionalNome}</p>
            </div>
            <span className={cn("shrink-0 rounded-md border px-2 py-1 text-xs font-semibold", classeStatusAgenda(compromisso.status))}>
              {rotuloStatusAgenda(compromisso.status)}
            </span>
          </div>
          <div className="mt-3 flex flex-wrap gap-2 text-xs text-muted-foreground">
            <span className="rounded-md border bg-background px-2 py-1">{rotuloTipoAgenda(compromisso.tipo)}</span>
            {compromisso.sala ? <span className="rounded-md border bg-background px-2 py-1">{compromisso.sala}</span> : null}
          </div>
          {compromisso.observacoes ? <p className="mt-3 line-clamp-2 text-xs leading-5 text-muted-foreground">{compromisso.observacoes}</p> : null}
        </article>
      ))}
    </div>
  );
}

function EstadoCarregandoNutri({ texto }: { texto: string }) {
  return (
    <div className="flex min-h-28 items-center justify-center rounded-md border bg-background text-sm text-muted-foreground">
      <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
      {texto}
    </div>
  );
}

function PainelSimplesNutri({ titulo, descricao, icon: Icon }: { titulo: string; descricao: string; icon: Icone }) {
  return (
    <section className="rounded-lg border border-sky-100 bg-sky-50/60 p-4">
      <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-sky-800">
        <Icon className="h-5 w-5" />
      </span>
      <h5 className="mt-4 text-base font-semibold text-card-foreground">{titulo}</h5>
      <p className="mt-2 text-sm leading-6 text-muted-foreground">{descricao}</p>
    </section>
  );
}

function ResumoPlanoCompacto({ plano }: { plano: PlanoAlimentarNutriPro }) {
  return (
    <section className="rounded-lg border border-lime-200 bg-lime-50/70 p-4">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-lime-950">{plano.objetivo}</p>
          <p className="mt-1 max-w-2xl text-sm leading-6 text-lime-950/80">{plano.descricao ?? "Plano alimentar com refeições e macros calculados automaticamente."}</p>
        </div>
        <span className="w-fit rounded-md border border-lime-300 bg-white px-2 py-1 text-xs font-semibold text-lime-800">{plano.statusRotulo}</span>
      </div>
      <div className="mt-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <CampoPreparado label="Energia diária" value={formatarKcal(plano.energiaTotalKcal)} />
        <CampoPreparado label="Proteínas" value={formatarMacro(plano.proteinasTotal)} />
        <CampoPreparado label="Carboidratos" value={formatarMacro(plano.carboidratosTotal)} />
        <CampoPreparado label="Lipídios" value={formatarMacro(plano.lipidiosTotal)} />
      </div>
    </section>
  );
}

function ListaPlanosNutri({ planos }: { planos: PlanoAlimentarNutriPro[] }) {
  return (
    <section className="rounded-lg border bg-background p-4">
      <p className="text-sm font-semibold text-card-foreground">Planos alimentares</p>
      <div className="mt-3 grid gap-2">
        {planos.length ? planos.map((plano) => <LinhaPlanoAlimentar key={plano.id} plano={plano} />) : <div className="rounded-md border bg-white p-3 text-sm text-muted-foreground">Nenhum plano alimentar registrado ainda.</div>}
      </div>
    </section>
  );
}

function ListaRefeicoesPlano({ plano }: { plano: PlanoAlimentarNutriPro }) {
  return (
    <section className="grid gap-3">
      {plano.refeicoes.map((refeicao) => (
        <article key={refeicao.id} className="rounded-lg border bg-background p-4">
          <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
            <div>
              <p className="text-sm font-semibold text-card-foreground">
                {refeicao.nome}
                {refeicao.horario ? <span className="font-medium text-muted-foreground"> · {refeicao.horario}</span> : null}
              </p>
              {refeicao.observacoes ? <p className="mt-1 text-xs leading-5 text-muted-foreground">{refeicao.observacoes}</p> : null}
            </div>
            <span className="w-fit rounded-md border bg-white px-2 py-1 text-xs font-semibold text-emerald-800">{formatarKcal(refeicao.energiaTotalKcal)}</span>
          </div>
          <div className="mt-3 grid gap-2">
            {refeicao.itens.map((item) => (
              <div key={item.id} className="grid gap-2 rounded-md border bg-white p-3 text-sm md:grid-cols-[minmax(0,1fr)_auto]">
                <div className="min-w-0">
                  <p className="font-semibold text-card-foreground">
                    {item.nome}
                    <span className="ml-2 rounded-md border bg-background px-2 py-0.5 text-xs font-semibold text-muted-foreground">{item.tipoItemRotulo}</span>
                  </p>
                  <p className="mt-1 text-xs leading-5 text-muted-foreground">
                    {formatarDecimal(item.quantidade)} {item.unidadeMedida}
                    {item.grupo ? ` · ${item.grupo}` : ""}
                    {item.observacoes ? ` · ${item.observacoes}` : ""}
                  </p>
                </div>
                <p className="text-xs font-semibold text-muted-foreground md:text-right">
                  {formatarKcal(item.energiaKcal)} · P {formatarMacro(item.proteinas)} · C {formatarMacro(item.carboidratos)} · L {formatarMacro(item.lipidios)}
                </p>
              </div>
            ))}
          </div>
        </article>
      ))}
    </section>
  );
}

function ResumoMacrosPlano({ plano }: { plano: PlanoAlimentarNutriPro }) {
  const totalMacros = plano.proteinasTotal + plano.carboidratosTotal + plano.lipidiosTotal;
  const macros = [
    { label: "Proteínas", valor: plano.proteinasTotal, cor: "bg-emerald-500" },
    { label: "Carboidratos", valor: plano.carboidratosTotal, cor: "bg-sky-500" },
    { label: "Lipídios", valor: plano.lipidiosTotal, cor: "bg-amber-500" }
  ];

  return (
    <section className="rounded-lg border bg-background p-4">
      <p className="text-sm font-semibold text-card-foreground">Resumo de macronutrientes</p>
      <div className="mt-4 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <CampoPreparado label="Energia diária" value={formatarKcal(plano.energiaTotalKcal)} />
        {macros.map((macro) => <CampoPreparado key={macro.label} label={macro.label} value={formatarMacro(macro.valor)} />)}
      </div>
      <div className="mt-4 overflow-hidden rounded-full border bg-white">
        <div className="flex h-4">
          {macros.map((macro) => (
            <span key={macro.label} className={macro.cor} style={{ width: `${totalMacros > 0 ? (macro.valor / totalMacros) * 100 : 0}%` }} />
          ))}
        </div>
      </div>
      <div className="mt-3 flex flex-wrap gap-2">
        {macros.map((macro) => (
          <span key={macro.label} className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{macro.label}: {formatarMacro(macro.valor)}</span>
        ))}
      </div>
    </section>
  );
}

function CardIndicadorNutri({ indicador }: { indicador: IndicadorNutriPro }) {
  const Icon = iconesIndicadores[indicador.codigo] ?? Sparkles;
  const destaque = classeStatusIndicador(indicador.status);

  return (
    <article className={cn("rounded-lg border p-4 shadow-sm", destaque)}>
      <div className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-emerald-800">
          <Icon className="h-5 w-5" />
        </span>
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{rotuloStatus(indicador.status)}</span>
      </div>
      <p className="mt-4 text-sm font-medium text-muted-foreground">{indicador.titulo}</p>
      <p className="mt-1 text-2xl font-semibold text-card-foreground">{formatarNumero(indicador.valor)}</p>
      <p className="mt-2 text-xs leading-5 text-muted-foreground">{indicador.descricao}</p>
    </article>
  );
}

function GrupoNutri({ titulo, itens }: { titulo: string; itens: IndicadorNutriPro[] }) {
  return (
    <div className="rounded-lg border bg-white p-4 shadow-sm">
      <p className="text-sm font-semibold text-card-foreground">{titulo}</p>
      <div className="mt-3 grid gap-2">
        {itens.map((indicador) => {
          const Icon = iconesIndicadores[indicador.codigo] ?? Sparkles;
          return (
            <div key={indicador.codigo} className="flex items-start justify-between gap-3 rounded-md border bg-background p-3">
              <span className="flex min-w-0 items-start gap-3">
                <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-primary/10 text-primary">
                  <Icon className="h-4 w-4" />
                </span>
                <span>
                  <span className="block text-sm font-semibold text-card-foreground">{indicador.titulo}</span>
                  <span className="mt-1 block text-xs leading-5 text-muted-foreground">{indicador.descricao}</span>
                </span>
              </span>
              <span className="shrink-0 text-lg font-semibold text-card-foreground">{formatarNumero(indicador.valor)}</span>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function CardAtalhoNutri({ atalho, principal = false }: { atalho: AtalhoNutriPro; principal?: boolean }) {
  const Icon = iconesAtalhos[atalho.codigo] ?? Sparkles;

  return (
    <button
      type="button"
      className={cn(
        "group min-h-24 rounded-lg border bg-background p-4 text-left transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
        principal ? "border-emerald-300 bg-emerald-50/70 hover:border-emerald-500" : "hover:border-primary/45"
      )}
      aria-label={`${atalho.titulo}: ${rotuloStatusAtalho(atalho.status)}`}
    >
      <span className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-emerald-800">
          <Icon className="h-5 w-5" />
        </span>
        <ChevronRight className="h-4 w-4 text-muted-foreground transition-transform group-hover:translate-x-0.5" />
      </span>
      <span className="mt-3 block text-sm font-semibold text-card-foreground">{atalho.titulo}</span>
      <span className="mt-2 block text-xs leading-5 text-muted-foreground">{atalho.descricao}</span>
      <span className="mt-3 inline-flex rounded-md border bg-white px-2 py-1 text-xs font-semibold text-emerald-800">{rotuloStatusAtalho(atalho.status)}</span>
    </button>
  );
}

function FluxoAvaliacaoEGastoEnergetico({ empresaId, prontuario }: { empresaId: string; prontuario: ProntuarioNutriPro }) {
  const queryClient = useQueryClient();
  const idadeInicial = prontuario.paciente.idade ?? 30;
  const [formulario, setFormulario] = useState<FormularioAvaliacaoNutri>({
    pesoKg: "70",
    alturaCm: "168",
    idade: String(idadeInicial),
    sexo: "FEMININO",
    objetivo: "MANUTENCAO",
    fatorAtividade: "1.40",
    observacoes: ""
  });

  useEffect(() => {
    setFormulario((estadoAtual) => ({ ...estadoAtual, idade: String(idadeInicial) }));
  }, [idadeInicial, prontuario.paciente.id]);

  const avaliacoesQuery = useQuery({
    queryKey: ["nutri-pro-avaliacoes-antropometricas", empresaId, prontuario.paciente.id],
    queryFn: () => listarAvaliacoesAntropometricasNutriPro({ empresaId, pacienteId: prontuario.paciente.id }),
    enabled: Boolean(empresaId && prontuario.paciente.id)
  });

  const criarAvaliacaoMutation = useMutation({
    mutationFn: (dados: CriarAvaliacaoAntropometricaNutriProInput) =>
      criarAvaliacaoAntropometricaNutriPro({
        empresaId,
        pacienteId: prontuario.paciente.id,
        dados
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-avaliacoes-antropometricas", empresaId, prontuario.paciente.id] });
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-prontuario", empresaId, prontuario.paciente.id] });
    }
  });

  const avaliacoes = avaliacoesQuery.data?.itens ?? [];
  const avaliacaoMaisRecente = criarAvaliacaoMutation.data ?? avaliacoes[0] ?? null;

  function atualizarCampo(campo: keyof FormularioAvaliacaoNutri, valor: string) {
    setFormulario((estadoAtual) => ({ ...estadoAtual, [campo]: valor }));
  }

  function registrarAvaliacao(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    criarAvaliacaoMutation.mutate({
      pesoKg: Number(formulario.pesoKg),
      alturaCm: Number(formulario.alturaCm),
      idade: Number(formulario.idade),
      sexo: formulario.sexo,
      objetivo: formulario.objetivo,
      fatorAtividade: Number(formulario.fatorAtividade),
      observacoes: formulario.observacoes.trim() || null
    });
  }

  return (
    <div className="grid gap-4">
      <div className="rounded-lg border border-amber-200 bg-amber-50 p-3 text-sm leading-6 text-amber-900">
        Cálculos estimativos. O AtendePro apoia a avaliação, mas a conduta final deve ser validada pela nutricionista.
      </div>

      <form onSubmit={registrarAvaliacao} className="grid gap-3 rounded-lg border bg-white p-4">
        <div className="grid gap-3 md:grid-cols-3">
          <CampoFormularioAvaliacao label="Peso (kg)" value={formulario.pesoKg} min="1" max="500" step="0.01" onChange={(valor) => atualizarCampo("pesoKg", valor)} />
          <CampoFormularioAvaliacao label="Altura (cm)" value={formulario.alturaCm} min="30" max="250" step="0.01" onChange={(valor) => atualizarCampo("alturaCm", valor)} />
          <CampoFormularioAvaliacao label="Idade" value={formulario.idade} min="1" max="120" step="1" onChange={(valor) => atualizarCampo("idade", valor)} />
        </div>
        <div className="grid gap-3 md:grid-cols-3">
          <label className="grid gap-1 text-sm font-medium text-card-foreground">
            Sexo biológico
            <select
              value={formulario.sexo}
              onChange={(event) => atualizarCampo("sexo", event.target.value as SexoBiologicoNutriPro)}
              className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
            >
              <option value="FEMININO">Feminino</option>
              <option value="MASCULINO">Masculino</option>
              <option value="NAO_INFORMADO">Não informado</option>
            </select>
          </label>
          <label className="grid gap-1 text-sm font-medium text-card-foreground">
            Objetivo
            <select
              value={formulario.objetivo}
              onChange={(event) => atualizarCampo("objetivo", event.target.value as ObjetivoNutricionalNutriPro)}
              className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
            >
              <option value="PERDA_DE_PESO">Perda de peso</option>
              <option value="GANHO_DE_MASSA">Ganho de massa</option>
              <option value="MANUTENCAO">Manutenção</option>
              <option value="PERFORMANCE">Performance</option>
              <option value="SAUDE">Saúde</option>
            </select>
          </label>
          <CampoFormularioAvaliacao label="Fator de atividade" value={formulario.fatorAtividade} min="1" max="3" step="0.01" onChange={(valor) => atualizarCampo("fatorAtividade", valor)} />
        </div>
        <label className="grid gap-1 text-sm font-medium text-card-foreground">
          Observações
          <textarea
            value={formulario.observacoes}
            onChange={(event) => atualizarCampo("observacoes", event.target.value)}
            className="min-h-24 rounded-md border bg-background px-3 py-2 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
            placeholder="Contexto clínico, rotina, objetivo ou observações técnicas"
          />
        </label>
        <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <p className="text-xs leading-5 text-muted-foreground">Fórmula inicial: Mifflin-St Jeor. Fórmulas configuráveis ficam para evolução futura.</p>
          <button
            type="submit"
            disabled={criarAvaliacaoMutation.isPending}
            className="inline-flex h-10 items-center justify-center rounded-md bg-primary px-4 text-sm font-semibold text-primary-foreground transition-colors hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {criarAvaliacaoMutation.isPending ? "Salvando avaliação" : "Salvar avaliação"}
          </button>
        </div>
        {criarAvaliacaoMutation.isError ? <p className="text-sm font-medium text-destructive">Não foi possível salvar a avaliação. Confira os dados e tente novamente.</p> : null}
      </form>

      {avaliacaoMaisRecente ? <ResumoAvaliacaoNutri avaliacao={avaliacaoMaisRecente} /> : null}

      <section className="rounded-lg border bg-white p-4">
        <div className="flex items-center justify-between gap-3">
          <div>
            <p className="text-sm font-semibold text-card-foreground">Histórico de avaliações</p>
            <p className="text-xs text-muted-foreground">Registros mais recentes do paciente.</p>
          </div>
          {avaliacoesQuery.isLoading ? <LoaderCircle className="h-4 w-4 animate-spin text-muted-foreground" /> : null}
        </div>
        <div className="mt-3 grid gap-2">
          {avaliacoes.length ? (
            avaliacoes.map((avaliacao) => <LinhaAvaliacaoNutri key={avaliacao.id} avaliacao={avaliacao} />)
          ) : (
            <div className="rounded-md border bg-background p-3 text-sm text-muted-foreground">Nenhuma avaliação antropométrica registrada ainda.</div>
          )}
        </div>
      </section>
    </div>
  );
}

function CampoFormularioAvaliacao({
  label,
  value,
  min,
  max,
  step,
  onChange
}: {
  label: string;
  value: string;
  min: string;
  max: string;
  step: string;
  onChange: (valor: string) => void;
}) {
  return (
    <label className="grid gap-1 text-sm font-medium text-card-foreground">
      {label}
      <input
        type="number"
        min={min}
        max={max}
        step={step}
        required
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
      />
    </label>
  );
}

function ResumoAvaliacaoNutri({ avaliacao }: { avaliacao: AvaliacaoAntropometricaNutriPro }) {
  return (
    <section className="rounded-lg border border-emerald-200 bg-emerald-50/70 p-4">
      <div className="flex flex-col gap-2 border-b border-emerald-200 pb-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-emerald-900">Resultado estimado</p>
          <p className="text-xs text-emerald-900/75">{avaliacao.formula}</p>
        </div>
        <span className="w-fit rounded-md border border-emerald-300 bg-white px-2 py-1 text-xs font-semibold text-emerald-800">
          {avaliacao.objetivoRotulo}
        </span>
      </div>
      <div className="mt-3 grid gap-3 sm:grid-cols-2 xl:grid-cols-5">
        <CampoPreparado label="IMC" value={formatarDecimal(avaliacao.imc)} />
        <CampoPreparado label="TMB" value={formatarKcal(avaliacao.tmbKcal)} />
        <CampoPreparado label="GEB" value={formatarKcal(avaliacao.gebKcal)} />
        <CampoPreparado label="GET" value={formatarKcal(avaliacao.getKcal)} />
        <CampoPreparado label="Meta energética" value={formatarKcal(avaliacao.metaEnergeticaKcal)} />
      </div>
      <p className="mt-3 text-xs leading-5 text-emerald-950/80">{avaliacao.aviso}</p>
    </section>
  );
}

function LinhaAvaliacaoNutri({ avaliacao }: { avaliacao: AvaliacaoAntropometricaNutriPro }) {
  return (
    <article className="grid gap-2 rounded-md border bg-background p-3 text-sm sm:grid-cols-[minmax(0,1fr)_auto]">
      <div>
        <p className="font-semibold text-card-foreground">{formatarDataHora(avaliacao.criadoEm)}</p>
        <p className="mt-1 text-xs leading-5 text-muted-foreground">
          {formatarDecimal(avaliacao.pesoKg)} kg · {formatarDecimal(avaliacao.alturaCm)} cm · IMC {formatarDecimal(avaliacao.imc)} · {avaliacao.sexoRotulo}
        </p>
      </div>
      <div className="flex flex-wrap gap-2 sm:justify-end">
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{avaliacao.objetivoRotulo}</span>
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-emerald-800">GET {formatarKcal(avaliacao.getKcal)}</span>
      </div>
    </article>
  );
}

function FluxoExamesLaboratoriais({ empresaId, prontuario }: { empresaId: string; prontuario: ProntuarioNutriPro }) {
  const queryClient = useQueryClient();
  const documentosQuery = useQuery({
    queryKey: ["nutri-pro-documentos", empresaId, prontuario.paciente.id, "SOLICITACAO_EXAMES"],
    queryFn: () => listarDocumentosProfissionaisNutriPro({ empresaId, pacienteId: prontuario.paciente.id, tipo: "SOLICITACAO_EXAMES" }),
    enabled: Boolean(empresaId && prontuario.paciente.id)
  });

  const criarSolicitacaoMutation = useMutation({
    mutationFn: () => criarDocumentoProfissionalNutriPro(criarSolicitacaoExamesInicial(empresaId, prontuario)),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-documentos", empresaId, prontuario.paciente.id] });
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-prontuario", empresaId, prontuario.paciente.id] });
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-visao", empresaId] });
    }
  });

  const documentos = documentosQuery.data?.itens ?? [];
  const documentoEmFoco = criarSolicitacaoMutation.data ?? documentos[0] ?? null;

  return (
    <div className="grid gap-4">
      <div className="rounded-lg border border-emerald-200 bg-emerald-50/70 p-4">
        <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
          <div>
            <p className="text-sm font-semibold text-emerald-900">Solicitação laboratorial</p>
            <p className="mt-1 text-sm leading-6 text-emerald-950/80">
              Gere uma solicitação inicial com exames frequentes no acompanhamento nutricional. O documento já fica no prontuário e pode virar PDF.
            </p>
          </div>
          <button
            type="button"
            onClick={() => criarSolicitacaoMutation.mutate()}
            disabled={criarSolicitacaoMutation.isPending}
            className="inline-flex h-10 shrink-0 items-center justify-center rounded-md bg-primary px-4 text-sm font-semibold text-primary-foreground transition-colors hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {criarSolicitacaoMutation.isPending ? "Criando solicitação" : "Criar solicitação de exames"}
          </button>
        </div>
        {criarSolicitacaoMutation.isError ? <p className="mt-3 text-sm font-medium text-destructive">Não foi possível criar a solicitação. Confira a conexão e tente novamente.</p> : null}
      </div>

      {documentoEmFoco ? <ResumoDocumentoNutri documento={documentoEmFoco} /> : null}
      <ListaDocumentosNutri titulo="Histórico de solicitações" documentos={documentos} carregando={documentosQuery.isLoading} vazio="Nenhuma solicitação de exames registrada ainda." />
    </div>
  );
}

function LinhaPlanoAlimentar({ plano }: { plano: PlanoAlimentarNutriPro }) {
  return (
    <article className="grid gap-2 rounded-md border bg-background p-3 text-sm sm:grid-cols-[minmax(0,1fr)_auto]">
      <div>
        <p className="font-semibold text-card-foreground">{plano.objetivo}</p>
        <p className="mt-1 text-xs leading-5 text-muted-foreground">
          {formatarDataHora(plano.criadoEm)} · {plano.refeicoes.length} refeições · {formatarKcal(plano.energiaTotalKcal)}
        </p>
      </div>
      <div className="flex flex-wrap gap-2 sm:justify-end">
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-emerald-800">{plano.statusRotulo}</span>
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">P {formatarMacro(plano.proteinasTotal)}</span>
      </div>
    </article>
  );
}

function FluxoPrescricoes({ empresaId, prontuario }: { empresaId: string; prontuario: ProntuarioNutriPro }) {
  const queryClient = useQueryClient();
  const documentosQuery = useQuery({
    queryKey: ["nutri-pro-documentos", empresaId, prontuario.paciente.id, "PRESCRICAO"],
    queryFn: () => listarDocumentosProfissionaisNutriPro({ empresaId, pacienteId: prontuario.paciente.id, tipo: "PRESCRICAO" }),
    enabled: Boolean(empresaId && prontuario.paciente.id)
  });

  const criarPrescricaoMutation = useMutation({
    mutationFn: () => criarDocumentoProfissionalNutriPro(criarPrescricaoInicial(empresaId, prontuario)),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-documentos", empresaId, prontuario.paciente.id] });
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-prontuario", empresaId, prontuario.paciente.id] });
      queryClient.invalidateQueries({ queryKey: ["nutri-pro-visao", empresaId] });
    }
  });

  const documentos = documentosQuery.data?.itens ?? [];
  const documentoEmFoco = criarPrescricaoMutation.data ?? documentos[0] ?? null;

  return (
    <div className="grid gap-4">
      <div className="rounded-lg border border-sky-200 bg-sky-50/70 p-4">
        <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
          <div>
            <p className="text-sm font-semibold text-sky-950">Prescrição de suplementação</p>
            <p className="mt-1 text-sm leading-6 text-sky-950/80">
              Registre uma prescrição inicial com suplementação e orientações. A responsabilidade técnica permanece com a nutricionista.
            </p>
          </div>
          <button
            type="button"
            onClick={() => criarPrescricaoMutation.mutate()}
            disabled={criarPrescricaoMutation.isPending}
            className="inline-flex h-10 shrink-0 items-center justify-center rounded-md bg-primary px-4 text-sm font-semibold text-primary-foreground transition-colors hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {criarPrescricaoMutation.isPending ? "Criando prescrição" : "Criar prescrição inicial"}
          </button>
        </div>
        {criarPrescricaoMutation.isError ? <p className="mt-3 text-sm font-medium text-destructive">Não foi possível criar a prescrição. Confira a conexão e tente novamente.</p> : null}
      </div>

      {documentoEmFoco ? <ResumoDocumentoNutri documento={documentoEmFoco} /> : null}
      <ListaDocumentosNutri titulo="Histórico de prescrições" documentos={documentos} carregando={documentosQuery.isLoading} vazio="Nenhuma prescrição registrada ainda." />
    </div>
  );
}

function ResumoDocumentoNutri({ documento, compacto = false }: { documento: DocumentoProfissionalNutriPro; compacto?: boolean }) {
  return (
    <article className={cn("rounded-lg border bg-white p-4", compacto ? "mt-3" : "")}>
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0">
          <p className="text-sm font-semibold text-card-foreground">{documento.titulo}</p>
          <p className="mt-1 text-xs leading-5 text-muted-foreground">
            {rotuloTipoDocumento(documento.tipo)} · {rotuloStatusDocumento(documento.status)} · {formatarDataHora(documento.criadoEm)}
          </p>
        </div>
        <a
          href={caminhoPdfDocumentoNutriPro(documento.id)}
          target="_blank"
          rel="noreferrer"
          className="inline-flex h-10 shrink-0 items-center justify-center rounded-md border bg-background px-4 text-sm font-semibold text-card-foreground transition-colors hover:border-primary/50"
        >
          Abrir PDF
        </a>
      </div>
      {!compacto ? <p className="mt-3 line-clamp-4 whitespace-pre-line text-sm leading-6 text-muted-foreground">{documento.conteudo}</p> : null}
    </article>
  );
}

function ListaDocumentosNutri({
  titulo,
  documentos,
  carregando,
  vazio
}: {
  titulo: string;
  documentos: DocumentoProfissionalNutriPro[];
  carregando: boolean;
  vazio: string;
}) {
  return (
    <section className="rounded-lg border bg-white p-4">
      <div className="flex items-center justify-between gap-3">
        <div>
          <p className="text-sm font-semibold text-card-foreground">{titulo}</p>
          <p className="text-xs text-muted-foreground">Documentos emitidos pelo módulo profissional.</p>
        </div>
        {carregando ? <LoaderCircle className="h-4 w-4 animate-spin text-muted-foreground" /> : null}
      </div>
      <div className="mt-3 grid gap-2">
        {documentos.length ? (
          documentos.map((documento) => (
            <article key={documento.id} className="grid gap-2 rounded-md border bg-background p-3 text-sm sm:grid-cols-[minmax(0,1fr)_auto]">
              <div className="min-w-0">
                <p className="truncate font-semibold text-card-foreground">{documento.titulo}</p>
                <p className="mt-1 text-xs leading-5 text-muted-foreground">{formatarDataHora(documento.criadoEm)} · {rotuloStatusDocumento(documento.status)}</p>
              </div>
              <a
                href={caminhoPdfDocumentoNutriPro(documento.id)}
                target="_blank"
                rel="noreferrer"
                className="inline-flex h-9 items-center justify-center rounded-md border bg-white px-3 text-xs font-semibold text-card-foreground transition-colors hover:border-primary/50"
              >
                PDF
              </a>
            </article>
          ))
        ) : (
          <div className="rounded-md border bg-background p-3 text-sm text-muted-foreground">{vazio}</div>
        )}
      </div>
    </section>
  );
}

function criarPlanoAlimentarInicial(): CriarPlanoAlimentarNutriProInput {
  return {
    objetivo: "Plano inicial de acompanhamento",
    descricao: "Plano alimentar operacional inicial criado no Nutri Pro.",
    status: "ATIVO",
    refeicoes: [
      {
        nome: "Café da manhã",
        horario: "08:00",
        observacoes: "Manter hidratação ao acordar.",
        ordenacao: 0,
        itens: [
          {
            tipoItem: "ALIMENTO",
            nome: "Pão integral",
            grupo: "Cereais",
            unidadeMedida: "g",
            quantidadeBase: 50,
            quantidade: 50,
            energiaKcalBase: 125,
            proteinasBase: 5,
            carboidratosBase: 23,
            lipidiosBase: 2,
            ordenacao: 0
          },
          {
            tipoItem: "ALIMENTO",
            nome: "Ovo",
            grupo: "Proteínas",
            unidadeMedida: "un",
            quantidadeBase: 1,
            quantidade: 2,
            energiaKcalBase: 70,
            proteinasBase: 6,
            carboidratosBase: 0,
            lipidiosBase: 5,
            ordenacao: 1
          }
        ]
      },
      {
        nome: "Almoço",
        horario: "12:30",
        observacoes: "Priorizar salada e mastigação lenta.",
        ordenacao: 1,
        itens: [
          {
            tipoItem: "ALIMENTO",
            nome: "Arroz integral",
            grupo: "Cereais",
            unidadeMedida: "g",
            quantidadeBase: 100,
            quantidade: 120,
            energiaKcalBase: 124,
            proteinasBase: 2.6,
            carboidratosBase: 25.8,
            lipidiosBase: 1,
            ordenacao: 0
          },
          {
            tipoItem: "ALIMENTO",
            nome: "Frango grelhado",
            grupo: "Proteínas",
            unidadeMedida: "g",
            quantidadeBase: 100,
            quantidade: 120,
            energiaKcalBase: 197,
            proteinasBase: 24.4,
            carboidratosBase: 0,
            lipidiosBase: 2,
            ordenacao: 1
          }
        ]
      },
      {
        nome: "Lanche da tarde",
        horario: "16:00",
        observacoes: "Usar nos dias de treino.",
        ordenacao: 2,
        itens: [
          {
            tipoItem: "ALIMENTO",
            nome: "Banana",
            grupo: "Frutas",
            unidadeMedida: "un",
            quantidadeBase: 1,
            quantidade: 1,
            energiaKcalBase: 89,
            proteinasBase: 1.1,
            carboidratosBase: 22.8,
            lipidiosBase: 0.3,
            ordenacao: 0
          },
          {
            tipoItem: "SUPLEMENTO",
            nome: "Whey protein",
            grupo: "Proteína",
            unidadeMedida: "g",
            quantidadeBase: 30,
            quantidade: 30,
            energiaKcalBase: 120,
            proteinasBase: 24,
            carboidratosBase: 3,
            lipidiosBase: 1.5,
            observacoes: "Diluir em água.",
            ordenacao: 1
          }
        ]
      }
    ]
  };
}

function criarSolicitacaoExamesInicial(empresaId: string, prontuario: ProntuarioNutriPro): CriarDocumentoProfissionalNutriProInput {
  const profissional = profissionalDocumento();
  const exames = ["Hemograma completo", "Glicemia de jejum", "Insulina", "Vitamina D", "Perfil lipídico", "Ferritina"];
  return {
    empresaId,
    clientePacienteId: prontuario.paciente.id,
    profissionalId: profissional.id,
    profissionalNome: profissional.nome,
    titulo: `Solicitação de exames laboratoriais - ${prontuario.paciente.nome}`,
    tipo: "SOLICITACAO_EXAMES",
    status: "EMITIDO",
    conteudo: [
      "Solicitação de exames laboratoriais",
      `Paciente: ${prontuario.paciente.nome}`,
      `Profissional: ${profissional.nome}`,
      "Conselho: CRN/RJ 00000-D",
      "",
      "Exames solicitados:",
      ...exames.map((exame) => `- ${exame}`),
      "",
      "Justificativa/observações:",
      "Acompanhamento nutricional inicial para avaliação metabólica, estado nutricional e apoio à conduta profissional.",
      "",
      "Aviso: o AtendePro organiza o documento, mas a solicitação e a responsabilidade técnica são da nutricionista."
    ].join("\n")
  };
}

function criarPrescricaoInicial(empresaId: string, prontuario: ProntuarioNutriPro): CriarDocumentoProfissionalNutriProInput {
  const profissional = profissionalDocumento();
  return {
    empresaId,
    clientePacienteId: prontuario.paciente.id,
    profissionalId: profissional.id,
    profissionalNome: profissional.nome,
    titulo: `Prescrição nutricional inicial - ${prontuario.paciente.nome}`,
    tipo: "PRESCRICAO",
    status: "EMITIDO",
    conteudo: [
      "Prescrição de suplementação e orientações nutricionais",
      `Paciente: ${prontuario.paciente.nome}`,
      `Profissional: ${profissional.nome}`,
      "Conselho: CRN/RJ 00000-D",
      "",
      "Itens iniciais:",
      "- Whey protein: 30 g após treino, conforme tolerância e avaliação profissional.",
      "- Creatina: 3 g ao dia, conforme indicação individual.",
      "",
      "Orientações:",
      "Manter hidratação, registrar sintomas e comunicar qualquer desconforto. Ajustes devem ser feitos pela nutricionista responsável.",
      "",
      "Aviso: documento demonstrativo local. A conduta final deve ser validada por profissional habilitado."
    ].join("\n")
  };
}

function criarDocumentoPlanoAlimentar(
  empresaId: string,
  prontuario: ProntuarioNutriPro,
  plano: PlanoAlimentarNutriPro
): CriarDocumentoProfissionalNutriProInput {
  const profissional = profissionalDocumento();
  const refeicoes = plano.refeicoes.flatMap((refeicao) => [
    `${refeicao.nome}${refeicao.horario ? ` (${refeicao.horario})` : ""}`,
    ...refeicao.itens.map((item) => `- ${item.nome}: ${formatarDecimal(item.quantidade)} ${item.unidadeMedida}`),
    refeicao.observacoes ? `Observações: ${refeicao.observacoes}` : ""
  ]);

  return {
    empresaId,
    clientePacienteId: prontuario.paciente.id,
    profissionalId: profissional.id,
    profissionalNome: profissional.nome,
    titulo: `${plano.objetivo} - ${prontuario.paciente.nome}`,
    tipo: "PLANO_ALIMENTAR",
    status: "EMITIDO",
    conteudo: [
      "Plano alimentar",
      `Paciente: ${prontuario.paciente.nome}`,
      `Profissional: ${profissional.nome}`,
      "Conselho: CRN/RJ 00000-D",
      `Objetivo: ${plano.objetivo}`,
      plano.descricao ? `Descrição: ${plano.descricao}` : "",
      "",
      "Resumo diário:",
      `Energia total: ${formatarKcal(plano.energiaTotalKcal)}`,
      `Proteínas: ${formatarMacro(plano.proteinasTotal)}`,
      `Carboidratos: ${formatarMacro(plano.carboidratosTotal)}`,
      `Lipídios: ${formatarMacro(plano.lipidiosTotal)}`,
      "",
      "Refeições:",
      ...refeicoes.filter(Boolean),
      "",
      "Aviso: o plano alimentar deve ser validado e acompanhado pela nutricionista responsável."
    ].join("\n")
  };
}

function profissionalDocumento() {
  const usuario = carregarSessaoAutenticada()?.usuario;
  return {
    id: usuario?.id ?? null,
    nome: usuario?.nome ?? "Nutricionista responsável"
  };
}

function rotuloTipoDocumento(tipo: string) {
  const rotulos: Record<string, string> = {
    SOLICITACAO_EXAMES: "Solicitação de exames",
    PRESCRICAO: "Prescrição",
    PLANO_ALIMENTAR: "Plano alimentar",
    ORIENTACAO: "Orientação",
    RELATORIO: "Relatório",
    TERMO: "Termo",
    DECLARACAO: "Declaração",
    RECIBO: "Recibo",
    OUTRO: "Documento"
  };
  return rotulos[tipo] ?? tipo;
}

function rotuloStatusDocumento(status: string) {
  const rotulos: Record<string, string> = {
    RASCUNHO: "Rascunho",
    EMITIDO: "Emitido",
    CANCELADO: "Cancelado"
  };
  return rotulos[status] ?? status;
}

function FluxoAnamnese() {
  return <CampoPreparado label="Anamnese inicial" value="Queixas, rotina, sintomas, preferências e observações do paciente." />;
}

function FluxoMetas() {
  return <CampoPreparado label="Metas" value="Definir objetivos, prazo de acompanhamento e retorno recomendado." />;
}

function CampoPreparado({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-md border bg-white p-3">
      <p className="text-xs font-semibold text-muted-foreground">{label}</p>
      <p className="mt-1 text-sm font-semibold text-card-foreground">{value}</p>
    </div>
  );
}

function ResumoProntuarioCard({ label, value }: { label: string; value: number }) {
  return (
    <article className="rounded-lg border bg-background p-3">
      <p className="text-xs font-semibold text-muted-foreground">{label}</p>
      <p className="mt-1 text-xl font-semibold text-card-foreground">{formatarNumero(value)}</p>
    </article>
  );
}

function LinhaPacienteNutri({
  paciente,
  selecionado,
  onSelecionar
}: {
  paciente: PacienteNutriResumo;
  selecionado: boolean;
  onSelecionar: () => void;
}) {
  return (
    <button
      type="button"
      aria-pressed={selecionado}
      onClick={onSelecionar}
      className={cn(
        "rounded-md border p-3 text-left transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
        selecionado ? "border-emerald-500 bg-emerald-50" : "bg-background hover:border-emerald-300"
      )}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-card-foreground">{paciente.nome}</p>
          <p className="mt-1 text-xs font-medium text-muted-foreground">{paciente.telefone ?? "Sem telefone"}</p>
        </div>
        <span className={cn("rounded-md border px-2 py-1 text-xs font-semibold", paciente.ativo ? "bg-emerald-50 text-emerald-800" : "bg-slate-50 text-slate-700")}>
          {paciente.ativo ? "Ativo" : "Inativo"}
        </span>
      </div>
      {paciente.observacoes ? <p className="mt-2 line-clamp-2 text-xs leading-5 text-muted-foreground">{paciente.observacoes}</p> : null}
    </button>
  );
}

function EstadoNutriPro({ titulo, descricao, alerta = false }: { titulo: string; descricao: string; alerta?: boolean }) {
  return (
    <section className={cn("flex min-h-52 flex-col items-center justify-center rounded-lg border p-6 text-center", alerta ? "border-amber-200 bg-amber-50" : "border-emerald-200 bg-emerald-50")}>
      {alerta ? <AlertTriangle className="h-8 w-8 text-amber-700" /> : <Apple className="h-8 w-8 text-emerald-800" />}
      <h4 className="mt-3 text-base font-semibold text-card-foreground">{titulo}</h4>
      <p className="mt-1 max-w-md text-sm leading-6 text-muted-foreground">{descricao}</p>
    </section>
  );
}

function classeStatusIndicador(status: string) {
  if (status === "ALERTA") {
    return "border-amber-300 bg-amber-50";
  }
  if (status === "SAUDAVEL" || status === "OPERACIONAL") {
    return "border-emerald-200 bg-emerald-50";
  }
  return "border bg-background";
}

function rotuloStatus(status: string) {
  const rotulos: Record<string, string> = {
    OPERACIONAL: "Operacional",
    PREPARADO: "Preparado",
    ALERTA: "Alerta",
    SAUDAVEL: "Saudável",
    PLANEJADO: "Planejado"
  };
  return rotulos[status] ?? status;
}

function rotuloStatusAtalho(status: string) {
  const rotulos: Record<string, string> = {
    DISPONIVEL: "Disponível",
    PLANEJADO_R10: "Planejado na R10",
    PROXIMA_TASK: "Próxima task"
  };
  return rotulos[status] ?? status;
}

function rotuloStatusAgenda(status: StatusAgendaNutriPro) {
  const rotulos: Record<StatusAgendaNutriPro, string> = {
    AGENDADO: "Agendado",
    CONFIRMADO: "Confirmado",
    REALIZADO: "Realizado",
    CANCELADO: "Cancelado",
    FALTOU: "Faltou",
    REMARCADO: "Remarcado"
  };
  return rotulos[status];
}

function rotuloTipoAgenda(tipo: string) {
  const rotulos: Record<string, string> = {
    PRESENCIAL: "Presencial",
    ONLINE: "Online",
    DOMICILIAR: "Domiciliar",
    SUBLOCACAO: "Sublocação",
    INTERNO: "Interno"
  };
  return rotulos[tipo] ?? tipo;
}

function classeStatusAgenda(status: StatusAgendaNutriPro) {
  if (status === "CANCELADO" || status === "FALTOU") {
    return "border-rose-200 bg-rose-50 text-rose-800";
  }
  if (status === "REALIZADO" || status === "CONFIRMADO") {
    return "border-emerald-200 bg-emerald-50 text-emerald-800";
  }
  if (status === "REMARCADO") {
    return "border-amber-200 bg-amber-50 text-amber-800";
  }
  return "border-sky-200 bg-sky-50 text-sky-800";
}

function filtrarAgendaNutri(agenda: CompromissoAgendaNutriPro[], termo: string, status: StatusAgendaNutriPro | "TODOS") {
  const termoNormalizado = termo.trim().toLowerCase();
  return agenda.filter((compromisso) => {
    const combinaStatus = status === "TODOS" || compromisso.status === status;
    if (!termoNormalizado) {
      return combinaStatus;
    }
    const texto = [compromisso.profissionalNome, compromisso.sala, compromisso.observacoes, compromisso.tipo, compromisso.status].filter(Boolean).join(" ").toLowerCase();
    return combinaStatus && texto.includes(termoNormalizado);
  });
}

function criarIntervaloAgendaNutri() {
  const inicio = new Date();
  inicio.setHours(0, 0, 0, 0);
  const fim = new Date(inicio);
  fim.setDate(fim.getDate() + 7);
  fim.setHours(23, 59, 59, 999);

  return {
    inicio: inicio.toISOString(),
    fim: fim.toISOString()
  };
}

function ehMesmoDia(dataA: Date, dataB: Date) {
  return dataA.getFullYear() === dataB.getFullYear() && dataA.getMonth() === dataB.getMonth() && dataA.getDate() === dataB.getDate();
}

function formatarAgendaPeriodo(inicio: string, fim: string) {
  const inicioData = new Date(inicio);
  const fimData = new Date(fim);
  const data = new Intl.DateTimeFormat("pt-BR", {
    weekday: "short",
    day: "2-digit",
    month: "2-digit"
  }).format(inicioData);
  const horaInicio = new Intl.DateTimeFormat("pt-BR", {
    hour: "2-digit",
    minute: "2-digit"
  }).format(inicioData);
  const horaFim = new Intl.DateTimeFormat("pt-BR", {
    hour: "2-digit",
    minute: "2-digit"
  }).format(fimData);

  return `${data} · ${horaInicio} às ${horaFim}`;
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR").format(valor);
}

function formatarDecimal(valor: number) {
  return new Intl.NumberFormat("pt-BR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  }).format(valor);
}

function formatarKcal(valor: number) {
  return `${formatarDecimal(valor)} kcal`;
}

function formatarMacro(valor: number) {
  return `${formatarDecimal(valor)} g`;
}

function formatarDataHora(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(valor));
}

function textoPaciente(prontuario: ProntuarioNutriPro) {
  const partes = [
    prontuario.paciente.idade ? `${prontuario.paciente.idade} anos` : null,
    prontuario.paciente.telefone,
    prontuario.paciente.email
  ].filter(Boolean);

  return partes.length ? partes.join(" · ") : "Dados básicos do paciente nutricional.";
}
