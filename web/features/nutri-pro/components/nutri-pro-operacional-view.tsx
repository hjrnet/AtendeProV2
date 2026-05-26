"use client";

import { useEffect, useMemo, useState, type FormEvent } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  AlertTriangle,
  Apple,
  CalendarDays,
  ChevronRight,
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
  listarAvaliacoesAntropometricasNutriPro,
  listarDocumentosProfissionaisNutriPro,
  listarPacientesNutriPro,
  listarPlanosAlimentaresNutriPro,
  type AcaoProntuarioNutriPro,
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
  type SexoBiologicoNutriPro
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
  const acaoInicial = acaoInicialPorFoco(focoWorkspace);

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

  return (
    <section className="grid gap-4 rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
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

      <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        {indicadoresPrincipais.map((indicador) => (
          <CardIndicadorNutri key={indicador.codigo} indicador={indicador} />
        ))}
      </div>

      <div className="grid gap-4 2xl:grid-cols-[minmax(0,1fr)_380px]">
        <section className="grid gap-4">
          <ProntuarioNutriProPainel empresaId={empresaId} pacienteId={pacienteSelecionadoId} acaoInicial={acaoInicial} />

          <div className="rounded-lg border bg-white p-4 shadow-sm">
            <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Ações prioritárias</p>
                <p className="text-sm leading-6 text-muted-foreground">Comece pelos três pontos que mais aparecem no atendimento nutricional.</p>
              </div>
              <span className="w-fit rounded-md border bg-emerald-50 px-2 py-1 text-xs font-semibold text-emerald-800">R10 em construção</span>
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
                {visao.proximasEvolucoes.map((atalho) => (
                  <CardAtalhoNutri key={atalho.codigo} atalho={atalho} />
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="rounded-lg border border-sky-100 bg-sky-50/45 p-4 shadow-sm">
          <div className="flex items-center justify-between gap-3 border-b pb-4">
            <div>
              <p className="text-sm font-semibold text-card-foreground">Pacientes Nutri</p>
              <p className="text-xs font-medium text-muted-foreground">Selecione para abrir o prontuário</p>
            </div>
            <Users className="h-5 w-5 text-emerald-800" />
          </div>
          <label className="mt-3 grid gap-1 text-sm font-medium text-card-foreground">
            Busca
            <input
              value={buscaPaciente}
              onChange={(event) => setBuscaPaciente(event.target.value)}
              className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Nome, email ou telefone"
            />
          </label>
          <div className="mt-3 grid max-h-[520px] gap-2 overflow-y-auto pr-1">
            {pacientesQuery.isLoading ? (
              <div className="flex min-h-24 items-center justify-center rounded-md border bg-background text-sm text-muted-foreground">
                <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
                Carregando pacientes
              </div>
            ) : pacientes.length ? (
              pacientes.map((paciente) => (
                <LinhaPacienteNutri
                  key={paciente.id}
                  paciente={paciente}
                  selecionado={paciente.id === pacienteSelecionadoId}
                  onSelecionar={() => setPacienteSelecionadoId(paciente.id)}
                />
              ))
            ) : (
              <div className="rounded-md border bg-background p-4 text-sm text-muted-foreground">Nenhum paciente de nutrição encontrado nesta empresa.</div>
            )}
          </div>
        </section>
      </div>
    </section>
  );
}

function ProntuarioNutriProPainel({
  empresaId,
  pacienteId,
  acaoInicial
}: {
  empresaId: string;
  pacienteId: string | null;
  acaoInicial: string | null;
}) {
  const [acaoAtiva, setAcaoAtiva] = useState<string | null>(null);

  const prontuarioQuery = useQuery({
    queryKey: ["nutri-pro-prontuario", empresaId, pacienteId],
    queryFn: () => consultarProntuarioNutriPro({ empresaId, pacienteId: pacienteId ?? "" }),
    enabled: Boolean(empresaId && pacienteId)
  });

  useEffect(() => {
    setAcaoAtiva(acaoInicial);
  }, [acaoInicial, pacienteId]);

  if (!pacienteId) {
    return <EstadoNutriPro titulo="Selecione um paciente" descricao="Escolha um paciente na lista para abrir o prontuário nutricional." />;
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
  const acaoSelecionada = prontuario.acoesRapidas.find((acao) => acao.codigo === acaoAtiva) ?? null;

  return (
    <section className="rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-3 border-b pb-4 lg:flex-row lg:items-start lg:justify-between">
        <div className="min-w-0">
          <p className="text-sm font-semibold text-emerald-800">Prontuário nutricional</p>
          <h5 className="mt-1 text-xl font-semibold text-card-foreground">{prontuario.paciente.nome}</h5>
          <p className="mt-2 text-sm leading-6 text-muted-foreground">
            {textoPaciente(prontuario)}
          </p>
        </div>
        <span className={cn("w-fit rounded-md border px-3 py-2 text-xs font-semibold", prontuario.paciente.ativo ? "bg-emerald-50 text-emerald-800" : "bg-slate-50 text-slate-700")}>
          {prontuario.paciente.ativo ? "Paciente ativo" : "Paciente inativo"}
        </span>
      </div>

      <div className="mt-4 grid gap-4 xl:grid-cols-[minmax(0,0.95fr)_minmax(340px,1.05fr)]">
        <div className="grid min-w-0 gap-3">
          <div className="grid gap-3 sm:grid-cols-2 2xl:grid-cols-4">
            <ResumoProntuarioCard label="Consultas futuras" value={prontuario.resumo.consultasFuturas} />
            <ResumoProntuarioCard label="Documentos" value={prontuario.resumo.documentos} />
            <ResumoProntuarioCard label="Simulações Nutri" value={prontuario.resumo.simulacoesPrecificacao} />
            <ResumoProntuarioCard label="Planos ativos" value={prontuario.resumo.planosAlimentaresAtivos} />
          </div>

          <div className="grid gap-3 sm:grid-cols-2 2xl:grid-cols-3">
            {prontuario.acoesRapidas.filter((acao) => acao.destaque).map((acao) => (
              <CardAcaoProntuario key={acao.codigo} acao={acao} ativo={acao.codigo === acaoAtiva} onSelecionar={() => setAcaoAtiva(acao.codigo)} />
            ))}
          </div>

          <div className="grid gap-2 sm:grid-cols-2 2xl:grid-cols-3">
            {prontuario.acoesRapidas.filter((acao) => !acao.destaque).map((acao) => (
              <CardAcaoProntuario key={acao.codigo} acao={acao} ativo={acao.codigo === acaoAtiva} onSelecionar={() => setAcaoAtiva(acao.codigo)} compacto />
            ))}
          </div>
        </div>

        <div className="min-w-0 xl:sticky xl:top-32 xl:self-start">
          <PainelAcaoProntuario empresaId={empresaId} prontuario={prontuario} acao={acaoSelecionada} />
        </div>
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

function CardAcaoProntuario({
  acao,
  ativo,
  compacto = false,
  onSelecionar
}: {
  acao: AcaoProntuarioNutriPro;
  ativo: boolean;
  compacto?: boolean;
  onSelecionar: () => void;
}) {
  const Icon = iconesAtalhos[acao.codigo] ?? Sparkles;

  return (
    <button
      type="button"
      onClick={onSelecionar}
      aria-pressed={ativo}
      className={cn(
        "rounded-lg border p-4 text-left transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
        ativo ? "border-emerald-500 bg-emerald-50" : "bg-background hover:border-emerald-300",
        compacto ? "min-h-20" : "min-h-32"
      )}
    >
      <span className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-emerald-800">
          <Icon className="h-5 w-5" />
        </span>
        <span className={cn("rounded-md border bg-white px-2 py-1 text-xs font-semibold", acao.status === "PROXIMA_TASK" ? "text-amber-800" : "text-emerald-800")}>
          {acao.statusRotulo}
        </span>
      </span>
      <span className="mt-3 block text-sm font-semibold text-card-foreground">{acao.titulo}</span>
      {!compacto ? <span className="mt-2 block text-xs leading-5 text-muted-foreground">{acao.descricao}</span> : null}
    </button>
  );
}

function PainelAcaoProntuario({ empresaId, prontuario, acao }: { empresaId: string; prontuario: ProntuarioNutriPro; acao: AcaoProntuarioNutriPro | null }) {
  if (!acao) {
    return (
      <div className="rounded-lg border border-emerald-200 bg-emerald-50/70 p-4 text-sm leading-6 text-muted-foreground">
        Selecione uma ação rápida para abrir o fluxo preparado do paciente. Gastos energéticos, exames laboratoriais e plano alimentar estão em destaque para a rotina da Karol.
      </div>
    );
  }

  return (
    <div className="rounded-lg border border-sky-100 bg-sky-50/45 p-4">
      <div className="flex flex-col gap-2 border-b pb-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-card-foreground">{acao.titulo}</p>
          <p className="text-sm leading-6 text-muted-foreground">{acao.descricao}</p>
        </div>
        <span className="w-fit rounded-md border bg-white px-2 py-1 text-xs font-semibold text-emerald-800">{acao.statusRotulo}</span>
      </div>
      <div className="mt-3">
        {acao.codigo === "gasto-energetico" ? <FluxoAvaliacaoEGastoEnergetico empresaId={empresaId} prontuario={prontuario} /> : null}
        {acao.codigo === "exames-laboratoriais" ? <FluxoExamesLaboratoriais empresaId={empresaId} prontuario={prontuario} /> : null}
        {acao.codigo === "plano-alimentar" ? <FluxoPlanoAlimentar empresaId={empresaId} prontuario={prontuario} /> : null}
        {acao.codigo === "avaliacao-antropometrica" ? <FluxoAvaliacaoEGastoEnergetico empresaId={empresaId} prontuario={prontuario} /> : null}
        {acao.codigo === "anamnese" ? <FluxoAnamnese /> : null}
        {acao.codigo === "prescricoes" ? <FluxoPrescricoes empresaId={empresaId} prontuario={prontuario} /> : null}
        {acao.codigo === "metas" ? <FluxoMetas /> : null}
      </div>
    </div>
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

function FluxoPlanoAlimentar({ empresaId, prontuario }: { empresaId: string; prontuario: ProntuarioNutriPro }) {
  const queryClient = useQueryClient();

  const planosQuery = useQuery({
    queryKey: ["nutri-pro-planos-alimentares", empresaId, prontuario.paciente.id],
    queryFn: () => listarPlanosAlimentaresNutriPro({ empresaId, pacienteId: prontuario.paciente.id }),
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

  const documentosPlanoQuery = useQuery({
    queryKey: ["nutri-pro-documentos", empresaId, prontuario.paciente.id, "PLANO_ALIMENTAR"],
    queryFn: () => listarDocumentosProfissionaisNutriPro({ empresaId, pacienteId: prontuario.paciente.id, tipo: "PLANO_ALIMENTAR" }),
    enabled: Boolean(empresaId && prontuario.paciente.id)
  });

  const planos = planosQuery.data?.itens ?? [];
  const planoEmFoco = criarPlanoMutation.data ?? planos[0] ?? null;
  const documentosPlano = documentosPlanoQuery.data?.itens ?? [];
  const documentoPlanoMaisRecente = documentosPlano[0] ?? null;

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

  return (
    <div className="grid gap-4">
      <div className="rounded-lg border border-emerald-200 bg-emerald-50/70 p-4">
        <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
          <div>
            <p className="text-sm font-semibold text-emerald-900">Plano alimentar operacional</p>
            <p className="mt-1 text-sm leading-6 text-emerald-950/80">
              Crie um plano inicial com refeições, alimentos, suplemento e resumo de macronutrientes para este paciente.
            </p>
          </div>
          <button
            type="button"
            onClick={() => criarPlanoMutation.mutate()}
            disabled={criarPlanoMutation.isPending}
            className="inline-flex h-10 shrink-0 items-center justify-center rounded-md bg-primary px-4 text-sm font-semibold text-primary-foreground transition-colors hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {criarPlanoMutation.isPending ? "Criando plano" : "Criar plano alimentar inicial"}
          </button>
        </div>
        {criarPlanoMutation.isError ? <p className="mt-3 text-sm font-medium text-destructive">Não foi possível criar o plano alimentar. Confira a conexão e tente novamente.</p> : null}
      </div>

      {planoEmFoco ? (
        <>
          <ResumoPlanoAlimentar plano={planoEmFoco} />
          <section className="rounded-lg border bg-white p-4">
            <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Documento e PDF do plano</p>
                <p className="mt-1 text-xs leading-5 text-muted-foreground">
                  Gere um documento profissional com refeições, macros e aviso de responsabilidade. O PDF usa o módulo de documentos e aceita carimbo CRN.
                </p>
              </div>
              <button
                type="button"
                onClick={() => criarDocumentoPlanoMutation.mutate()}
                disabled={criarDocumentoPlanoMutation.isPending}
                className="inline-flex h-10 shrink-0 items-center justify-center rounded-md border bg-background px-4 text-sm font-semibold text-card-foreground transition-colors hover:border-primary/50 disabled:cursor-not-allowed disabled:opacity-60"
              >
                {criarDocumentoPlanoMutation.isPending ? "Gerando documento" : "Gerar documento do plano"}
              </button>
            </div>
            {criarDocumentoPlanoMutation.isError ? <p className="mt-3 text-sm font-medium text-destructive">Não foi possível gerar o documento do plano.</p> : null}
            {criarDocumentoPlanoMutation.data ? <ResumoDocumentoNutri documento={criarDocumentoPlanoMutation.data} compacto /> : null}
            {!criarDocumentoPlanoMutation.data && documentoPlanoMaisRecente ? <ResumoDocumentoNutri documento={documentoPlanoMaisRecente} compacto /> : null}
          </section>
        </>
      ) : (
        <div className="rounded-lg border bg-white p-4 text-sm leading-6 text-muted-foreground">
          Nenhum plano alimentar criado ainda. Use o botão acima para gerar o primeiro plano operacional do paciente.
        </div>
      )}

      <section className="rounded-lg border bg-white p-4">
        <div className="flex items-center justify-between gap-3">
          <div>
            <p className="text-sm font-semibold text-card-foreground">Histórico de planos alimentares</p>
            <p className="text-xs text-muted-foreground">Planos mais recentes do paciente.</p>
          </div>
          {planosQuery.isLoading ? <LoaderCircle className="h-4 w-4 animate-spin text-muted-foreground" /> : null}
        </div>
        <div className="mt-3 grid gap-2">
          {planos.length ? (
            planos.map((plano) => <LinhaPlanoAlimentar key={plano.id} plano={plano} />)
          ) : (
            <div className="rounded-md border bg-background p-3 text-sm text-muted-foreground">Nenhum plano alimentar registrado ainda.</div>
          )}
        </div>
      </section>
    </div>
  );
}

function ResumoPlanoAlimentar({ plano }: { plano: PlanoAlimentarNutriPro }) {
  return (
    <section className="rounded-lg border bg-white p-4">
      <div className="flex flex-col gap-2 border-b pb-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-card-foreground">{plano.objetivo}</p>
          <p className="mt-1 text-xs leading-5 text-muted-foreground">{plano.descricao ?? "Plano alimentar com refeições e macros calculados automaticamente."}</p>
        </div>
        <span className="w-fit rounded-md border bg-emerald-50 px-2 py-1 text-xs font-semibold text-emerald-800">{plano.statusRotulo}</span>
      </div>

      <div className="mt-3 grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        <CampoPreparado label="Energia diária" value={formatarKcal(plano.energiaTotalKcal)} />
        <CampoPreparado label="Proteínas" value={formatarMacro(plano.proteinasTotal)} />
        <CampoPreparado label="Carboidratos" value={formatarMacro(plano.carboidratosTotal)} />
        <CampoPreparado label="Lipídios" value={formatarMacro(plano.lipidiosTotal)} />
      </div>

      <div className="mt-4 grid gap-3">
        <p className="text-sm font-semibold text-card-foreground">Refeições do plano</p>
        {plano.refeicoes.map((refeicao) => (
          <article key={refeicao.id} className="rounded-lg border bg-background p-3">
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
      </div>
    </section>
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

function acaoInicialPorFoco(foco: FocoWorkspaceNutriPro) {
  const acoesPorFoco: Partial<Record<FocoWorkspaceNutriPro, string>> = {
    "nutri-plano": "plano-alimentar",
    "nutri-avaliacoes": "gasto-energetico",
    "nutri-documentos": "exames-laboratoriais"
  };

  return acoesPorFoco[foco] ?? null;
}
