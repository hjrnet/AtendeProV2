"use client";

import { useEffect, useMemo, useState, type FormEvent } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  AlertTriangle,
  CalendarDays,
  CheckCircle2,
  ClipboardList,
  FileText,
  Gauge,
  LoaderCircle,
  PackageCheck,
  Save,
  Scissors,
  Sparkles,
  UserRoundCheck,
  Wrench
} from "lucide-react";

import {
  atualizarFichaEsteticaBeautyPro,
  consultarIntegracoesOperacionaisBeautyPro,
  consultarSegurancaOperacionalBeautyPro,
  consultarProntuarioBeautyPro,
  consultarVisaoBeautyPro,
  criarEvidenciaEvolucaoBeautyPro,
  criarFichaEsteticaBeautyPro,
  criarProtocoloBeautyPro,
  criarTermoConsentimentoBeautyPro,
  listarClientesBeautyPro,
  listarFichasEsteticasBeautyPro,
  listarProtocolosBeautyPro,
  registrarSessaoProtocoloBeautyPro,
  vincularProdutoBeautyPro,
  type AtalhoBeautyPro,
  type ClienteBeautyResumo,
  type CriarEvidenciaEvolucaoBeautyProInput,
  type CriarProtocoloBeautyProInput,
  type CriarTermoConsentimentoBeautyProInput,
  type EvidenciaEvolucaoBeautyPro,
  type FichaEsteticaBeautyPro,
  type IndicadorBeautyPro,
  type AgendaBeautyPro,
  type IntegracoesOperacionaisBeautyPro,
  type ObjetivoEsteticoBeautyPro,
  type ProdutoBeautyEstoque,
  type ProdutoUtilizadoBeautyPro,
  type ProtocoloBeautyPro,
  type RegistrarSessaoProtocoloBeautyProInput,
  type SalvarFichaEsteticaBeautyProInput,
  type ServicoBeautyPro,
  type SimulacaoBeautyPro,
  type TermoConsentimentoBeautyPro,
  type TipoPlaceholderEvolucaoBeautyPro,
  type TipoProtocoloBeautyPro,
  type VincularProdutoBeautyProInput
} from "@/features/beauty-pro/api/beauty-pro-client";
import { cn } from "@/lib/utils";

type Icone = typeof Scissors;
type EtapaOperacionalBeauty = "ficha" | "protocolos" | "seguranca" | "integracoes";

type FormularioFichaBeauty = {
  objetivo: ObjetivoEsteticoBeautyPro;
  queixaPrincipal: string;
  historicoEstetico: string;
  alergias: string;
  medicamentos: string;
  gestante: boolean;
  lactante: boolean;
  sensibilidadePele: boolean;
  usaAcidos: boolean;
  exposicaoSolarIntensa: boolean;
  procedimentosRecentes: string;
  contraindicacoes: string;
  observacoes: string;
};

type FormularioProtocoloBeauty = {
  nome: string;
  tipo: TipoProtocoloBeautyPro;
  objetivo: string;
  quantidadeSessoesPrevistas: string;
  observacoes: string;
};

type FormularioSessaoBeauty = {
  descricaoExecucao: string;
  evolucaoCliente: string;
  produtosUtilizados: string;
  orientacoes: string;
};

type FormularioTermoBeauty = {
  protocoloId: string;
  titulo: string;
  conteudo: string;
  aceiteProfissional: boolean;
};

type FormularioEvidenciaBeauty = {
  protocoloId: string;
  tipoPlaceholder: TipoPlaceholderEvolucaoBeautyPro;
  titulo: string;
  descricao: string;
  observacoesPrivacidade: string;
};

type FormularioProdutoBeauty = {
  protocoloId: string;
  produtoEstoqueId: string;
  nomeProduto: string;
  lote: string;
  validade: string;
  quantidade: string;
  unidade: string;
  observacoes: string;
};

const iconesIndicadores: Record<string, Icone> = {
  clientes: UserRoundCheck,
  agendaHoje: CalendarDays,
  agenda7Dias: CalendarDays,
  servicos: Scissors,
  protocolos: Sparkles,
  sessoes: ClipboardList,
  termos: FileText,
  evidencias: UserRoundCheck,
  produtos: PackageCheck,
  produtosVinculados: PackageCheck,
  alertasProdutos: AlertTriangle,
  equipamentos: Wrench,
  precificacao: Gauge,
  alertas: AlertTriangle
};

const iconesAtalhos: Record<string, Icone> = {
  "ficha-estetica": ClipboardList,
  protocolos: Sparkles,
  termos: FileText,
  produtos: PackageCheck,
  "fotos-placeholder": UserRoundCheck,
  dashboard: Gauge
};

const objetivosEsteticos: Array<{ value: ObjetivoEsteticoBeautyPro; label: string }> = [
  { value: "ACNE", label: "Acne" },
  { value: "MANCHAS", label: "Manchas" },
  { value: "REJUVENESCIMENTO", label: "Rejuvenescimento" },
  { value: "CORPORAL", label: "Corporal" },
  { value: "RELAXAMENTO", label: "Relaxamento" },
  { value: "CAPILAR", label: "Capilar" },
  { value: "CILIOS_SOBRANCELHAS", label: "Cílios e sobrancelhas" },
  { value: "SALAO", label: "Salão" }
];

const fichaVazia: FormularioFichaBeauty = {
  objetivo: "ACNE",
  queixaPrincipal: "",
  historicoEstetico: "",
  alergias: "",
  medicamentos: "",
  gestante: false,
  lactante: false,
  sensibilidadePele: false,
  usaAcidos: false,
  exposicaoSolarIntensa: false,
  procedimentosRecentes: "",
  contraindicacoes: "",
  observacoes: ""
};

const protocoloVazio: FormularioProtocoloBeauty = {
  nome: "",
  tipo: "FACIAL",
  objetivo: "",
  quantidadeSessoesPrevistas: "4",
  observacoes: ""
};

const sessaoVazia: FormularioSessaoBeauty = {
  descricaoExecucao: "",
  evolucaoCliente: "",
  produtosUtilizados: "",
  orientacoes: ""
};

const termoVazio: FormularioTermoBeauty = {
  protocoloId: "",
  titulo: "Termo de consentimento estético",
  conteudo: "Cliente orientada sobre objetivo, cuidados, contraindicações, riscos esperados e necessidade de acompanhamento profissional.",
  aceiteProfissional: true
};

const evidenciaVazia: FormularioEvidenciaBeauty = {
  protocoloId: "",
  tipoPlaceholder: "FACE_NEUTRA",
  titulo: "Evolução segura do protocolo",
  descricao: "Registro textual com área tratada, resposta observada e conduta, sem armazenar foto real de pessoa.",
  observacoesPrivacidade: "Usar placeholder seguro até existir fluxo formal de upload e consentimento de imagem."
};

const produtoVazio: FormularioProdutoBeauty = {
  protocoloId: "",
  produtoEstoqueId: "",
  nomeProduto: "",
  lote: "",
  validade: "",
  quantidade: "1",
  unidade: "UN",
  observacoes: ""
};

const tiposProtocolo: Array<{ value: TipoProtocoloBeautyPro; label: string }> = [
  { value: "FACIAL", label: "Facial" },
  { value: "CORPORAL", label: "Corporal" },
  { value: "CAPILAR", label: "Capilar" },
  { value: "CILIOS_SOBRANCELHAS", label: "Cílios e sobrancelhas" },
  { value: "SALAO", label: "Salão" },
  { value: "PERSONALIZADO", label: "Personalizado" }
];

const tiposPlaceholder: Array<{ value: TipoPlaceholderEvolucaoBeautyPro; label: string }> = [
  { value: "FACE_NEUTRA", label: "Face neutra" },
  { value: "CORPORAL_NEUTRO", label: "Corporal neutro" },
  { value: "AREA_TRATADA", label: "Área tratada" },
  { value: "TEXTUAL", label: "Registro textual" }
];

const etapasOperacionais: Array<{ value: EtapaOperacionalBeauty; label: string; descricao: string; icon: Icone }> = [
  { value: "ficha", label: "Ficha", descricao: "Anamnese e segurança", icon: ClipboardList },
  { value: "protocolos", label: "Protocolos", descricao: "Pacotes e sessões", icon: Sparkles },
  { value: "seguranca", label: "Termos", descricao: "Evidências e lotes", icon: FileText },
  { value: "integracoes", label: "Agenda e preços", descricao: "Agenda, serviços e margem", icon: Gauge }
];

export function BeautyProOperacionalView({ empresaId }: { empresaId: string }) {
  const [buscaCliente, setBuscaCliente] = useState("");
  const [clienteSelecionadoId, setClienteSelecionadoId] = useState<string | null>(null);
  const [etapaAtiva, setEtapaAtiva] = useState<EtapaOperacionalBeauty>("ficha");

  const visaoQuery = useQuery({
    queryKey: ["beauty-pro-visao", empresaId],
    queryFn: () => consultarVisaoBeautyPro(empresaId),
    enabled: Boolean(empresaId)
  });

  const clientesQuery = useQuery({
    queryKey: ["beauty-pro-clientes", empresaId, buscaCliente],
    queryFn: () => listarClientesBeautyPro({ empresaId, busca: buscaCliente }),
    enabled: Boolean(empresaId)
  });

  const clientes = clientesQuery.data?.itens ?? [];

  useEffect(() => {
    if (!clientes.length) {
      setClienteSelecionadoId(null);
      return;
    }

    if (!clienteSelecionadoId || !clientes.some((cliente) => cliente.id === clienteSelecionadoId)) {
      setClienteSelecionadoId(clientes[0].id);
    }
  }, [clienteSelecionadoId, clientes]);

  const indicadoresPrincipais = useMemo(
    () =>
      (visaoQuery.data?.indicadores ?? []).filter((indicador) =>
        ["clientes", "protocolos", "sessoes", "precificacao", "alertas", "alertasProdutos"].includes(indicador.codigo)
      ),
    [visaoQuery.data]
  );

  const indicadoresApoio = useMemo(
    () =>
      (visaoQuery.data?.indicadores ?? []).filter(
        (indicador) => !["clientes", "protocolos", "sessoes", "precificacao", "alertas", "alertasProdutos"].includes(indicador.codigo)
      ),
    [visaoQuery.data]
  );

  if (!empresaId) {
    return <EstadoBeautyPro titulo="Selecione uma empresa" descricao="Escolha uma empresa para carregar a área operacional do Beauty Pro." />;
  }

  if (visaoQuery.isLoading) {
    return (
      <section className="rounded-lg border border-rose-200 bg-rose-50/45 p-4">
        <div className="flex min-h-44 items-center justify-center text-sm font-medium text-rose-800">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando Beauty Pro
        </div>
      </section>
    );
  }

  if (visaoQuery.isError || !visaoQuery.data) {
    return <EstadoBeautyPro titulo="Não foi possível carregar o Beauty Pro" descricao="Confira a sessão atual e tente novamente." alerta />;
  }

  const visao = visaoQuery.data;

  return (
    <section className="grid gap-4 rounded-lg border border-rose-200 bg-rose-50/45 p-4">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
        <div className="min-w-0">
          <p className="text-sm font-semibold text-rose-900">Beauty Pro operacional</p>
          <h4 className="mt-1 text-xl font-semibold text-card-foreground">{visao.empresaNome}</h4>
          <p className="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">{visao.mensagemStatus}</p>
        </div>
        <span className={cn("inline-flex w-fit items-center gap-2 rounded-md border px-3 py-2 text-xs font-semibold", visao.statusOperacional === "OPERACIONAL" ? "border-rose-200 bg-white text-rose-900" : "border-amber-200 bg-amber-50 text-amber-800")}>
          <Sparkles className="h-4 w-4" />
          {visao.statusOperacionalRotulo}
        </span>
      </div>

      <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        {indicadoresPrincipais.map((indicador) => (
          <CardIndicadorBeauty key={indicador.codigo} indicador={indicador} />
        ))}
      </div>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_360px]">
        <section className="grid gap-4">
          <nav className="grid gap-2 rounded-lg border bg-white p-2 shadow-sm sm:grid-cols-2 xl:grid-cols-4" aria-label="Fluxo operacional Beauty Pro">
            {etapasOperacionais.map((etapa) => {
              const Icon = etapa.icon;
              const ativo = etapaAtiva === etapa.value;
              return (
                <button
                  key={etapa.value}
                  type="button"
                  onClick={() => setEtapaAtiva(etapa.value)}
                  className={cn(
                    "flex min-h-16 items-center gap-3 rounded-md border px-3 py-2 text-left transition hover:border-rose-300 hover:bg-rose-50",
                    ativo ? "border-rose-500 bg-rose-50 shadow-sm" : "bg-background"
                  )}
                >
                  <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-white text-rose-900">
                    <Icon className="h-5 w-5" />
                  </span>
                  <span className="min-w-0">
                    <span className="block text-sm font-semibold text-card-foreground">{etapa.label}</span>
                    <span className="mt-1 block text-xs leading-5 text-muted-foreground">{etapa.descricao}</span>
                  </span>
                </button>
              );
            })}
          </nav>

          {etapaAtiva === "ficha" ? <FichaEsteticaBeautyPainel empresaId={empresaId} clienteId={clienteSelecionadoId} /> : null}
          {etapaAtiva === "protocolos" ? <ProtocolosBeautyPainel empresaId={empresaId} clienteId={clienteSelecionadoId} /> : null}
          {etapaAtiva === "seguranca" ? <SegurancaOperacionalBeautyPainel empresaId={empresaId} clienteId={clienteSelecionadoId} /> : null}
          {etapaAtiva === "integracoes" ? <IntegracoesOperacionaisBeautyPainel empresaId={empresaId} /> : null}

          <div className="rounded-lg border bg-white p-4 shadow-sm">
            <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Contratos operacionais preparados</p>
                <p className="text-sm leading-6 text-muted-foreground">A base da vertical já aponta os próximos fluxos reais: protocolos, sessões, termos e rastreabilidade.</p>
              </div>
              <span className="w-fit rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">R10 em construção</span>
            </div>
            <div className="mt-4 grid gap-3 md:grid-cols-3">
              {visao.atalhosPrioritarios.map((atalho) => (
                <CardAtalhoBeauty key={atalho.codigo} atalho={atalho} principal />
              ))}
            </div>
          </div>

          <div className="grid gap-3 lg:grid-cols-2">
            <GrupoBeauty titulo="Indicadores de apoio" itens={indicadoresApoio} />
            <div className="rounded-lg border bg-white p-4 shadow-sm">
              <p className="text-sm font-semibold text-card-foreground">Próximas evoluções</p>
              <div className="mt-3 grid gap-2">
                {visao.proximasEvolucoes.map((atalho) => (
                  <CardAtalhoBeauty key={atalho.codigo} atalho={atalho} />
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="rounded-lg border bg-white p-4 shadow-sm">
          <div className="flex items-center justify-between gap-3 border-b pb-4">
            <div>
              <p className="text-sm font-semibold text-card-foreground">Clientes Beauty</p>
              <p className="text-xs font-medium text-muted-foreground">Selecione para abrir a ficha estética</p>
            </div>
            <UserRoundCheck className="h-5 w-5 text-rose-900" />
          </div>
          <label className="mt-3 grid gap-1 text-sm font-medium text-card-foreground">
            Busca
            <input
              value={buscaCliente}
              onChange={(event) => setBuscaCliente(event.target.value)}
              className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Nome, email ou telefone"
            />
          </label>
          <div className="mt-3 grid max-h-[480px] gap-2 overflow-y-auto pr-1">
            {clientesQuery.isLoading ? (
              <div className="flex min-h-24 items-center justify-center rounded-md border bg-background text-sm text-muted-foreground">
                <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
                Carregando clientes
              </div>
            ) : clientes.length ? (
              clientes.map((cliente) => (
                <LinhaClienteBeauty
                  key={cliente.id}
                  cliente={cliente}
                  selecionado={cliente.id === clienteSelecionadoId}
                  onSelecionar={() => setClienteSelecionadoId(cliente.id)}
                />
              ))
            ) : (
              <div className="rounded-md border bg-background p-4 text-sm text-muted-foreground">Nenhum cliente Beauty encontrado nesta empresa.</div>
            )}
          </div>
        </section>
      </div>
    </section>
  );
}

function FichaEsteticaBeautyPainel({ empresaId, clienteId }: { empresaId: string; clienteId: string | null }) {
  const queryClient = useQueryClient();
  const [formulario, setFormulario] = useState<FormularioFichaBeauty>(fichaVazia);
  const [mensagem, setMensagem] = useState<string | null>(null);

  const prontuarioQuery = useQuery({
    queryKey: ["beauty-pro-prontuario", empresaId, clienteId],
    queryFn: () => consultarProntuarioBeautyPro({ empresaId, clienteId: clienteId ?? "" }),
    enabled: Boolean(empresaId && clienteId)
  });

  const fichasQuery = useQuery({
    queryKey: ["beauty-pro-fichas", empresaId, clienteId],
    queryFn: () => listarFichasEsteticasBeautyPro({ empresaId, clienteId: clienteId ?? "" }),
    enabled: Boolean(empresaId && clienteId)
  });

  const fichaAtual = prontuarioQuery.data?.fichaAtual ?? null;

  useEffect(() => {
    setMensagem(null);
    setFormulario(fichaAtual ? formularioDeFicha(fichaAtual) : fichaVazia);
  }, [clienteId, fichaAtual]);

  const salvarFichaMutation = useMutation({
    mutationFn: (dados: SalvarFichaEsteticaBeautyProInput) => {
      if (!clienteId) {
        throw new Error("Selecione um cliente.");
      }
      if (fichaAtual) {
        return atualizarFichaEsteticaBeautyPro({ empresaId, clienteId, fichaId: fichaAtual.id, dados });
      }
      return criarFichaEsteticaBeautyPro({ empresaId, clienteId, dados });
    },
    onSuccess: async () => {
      setMensagem("Ficha estética salva com sucesso.");
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["beauty-pro-prontuario", empresaId, clienteId] }),
        queryClient.invalidateQueries({ queryKey: ["beauty-pro-fichas", empresaId, clienteId] })
      ]);
    }
  });

  if (!clienteId) {
    return <EstadoBeautyPro titulo="Selecione um cliente" descricao="Escolha um cliente Beauty para abrir ficha estética, anamnese e avaliação inicial." />;
  }

  if (prontuarioQuery.isLoading) {
    return (
      <section className="rounded-lg border bg-white p-4 shadow-sm">
        <div className="flex min-h-44 items-center justify-center text-sm text-muted-foreground">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando ficha estética
        </div>
      </section>
    );
  }

  if (prontuarioQuery.isError || !prontuarioQuery.data) {
    return <EstadoBeautyPro titulo="Não foi possível abrir o cliente" descricao="Confira se o cliente pertence à área Beauty desta empresa." alerta />;
  }

  const prontuario = prontuarioQuery.data;

  function salvarFicha(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMensagem(null);
    salvarFichaMutation.mutate({
      objetivo: formulario.objetivo,
      queixaPrincipal: formulario.queixaPrincipal.trim(),
      historicoEstetico: textoOuNull(formulario.historicoEstetico),
      alergias: textoOuNull(formulario.alergias),
      medicamentos: textoOuNull(formulario.medicamentos),
      gestante: formulario.gestante,
      lactante: formulario.lactante,
      sensibilidadePele: formulario.sensibilidadePele,
      usaAcidos: formulario.usaAcidos,
      exposicaoSolarIntensa: formulario.exposicaoSolarIntensa,
      procedimentosRecentes: textoOuNull(formulario.procedimentosRecentes),
      contraindicacoes: textoOuNull(formulario.contraindicacoes),
      observacoes: textoOuNull(formulario.observacoes)
    });
  }

  return (
    <section className="rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-3 border-b pb-4 lg:flex-row lg:items-start lg:justify-between">
        <div>
          <p className="text-sm font-semibold text-rose-900">Ficha estética e anamnese</p>
          <h5 className="mt-1 text-lg font-semibold text-card-foreground">{prontuario.cliente.nome}</h5>
          <p className="mt-1 text-sm leading-6 text-muted-foreground">
            {prontuario.cliente.telefone ?? "Sem telefone"} {prontuario.cliente.idade ? `• ${prontuario.cliente.idade} anos` : ""}
          </p>
        </div>
        <div className="grid grid-cols-3 gap-2 text-center text-xs">
          <ResumoFichaBeauty rotulo="Fichas" valor={prontuario.resumo.fichasEsteticas} />
          <ResumoFichaBeauty rotulo="Ficha" valor={rotuloStatusFicha(prontuario.resumo.statusFichaEstetica)} texto />
          <ResumoFichaBeauty rotulo="Alertas" valor={rotuloStatusContraindicacao(prontuario.resumo.statusContraindicacoes)} texto destaque={prontuario.resumo.statusContraindicacoes === "ALERTA"} />
        </div>
      </div>

      {fichaAtual?.possuiAlertaContraindicacao ? (
        <div className="mt-4 rounded-lg border border-amber-300 bg-amber-50 p-4 text-sm text-amber-900">
          <div className="flex items-start gap-2">
            <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0" />
            <div>
              <p className="font-semibold">Contraindicações e alertas registrados</p>
              <p className="mt-1 leading-6">{fichaAtual.alertaContraindicacoes}</p>
            </div>
          </div>
        </div>
      ) : (
        <div className="mt-4 rounded-lg border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-900">
          <div className="flex items-start gap-2">
            <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0" />
            <p>Sem contraindicações ou alertas informados na ficha atual.</p>
          </div>
        </div>
      )}

      <form className="mt-4 grid gap-4" onSubmit={salvarFicha}>
        <div className="grid gap-3 md:grid-cols-2">
          <label className="grid gap-1 text-sm font-medium text-card-foreground">
            Objetivo principal
            <select
              value={formulario.objetivo}
              onChange={(event) => setFormulario((atual) => ({ ...atual, objetivo: event.target.value as ObjetivoEsteticoBeautyPro }))}
              className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
            >
              {objetivosEsteticos.map((objetivo) => (
                <option key={objetivo.value} value={objetivo.value}>
                  {objetivo.label}
                </option>
              ))}
            </select>
          </label>
          <label className="grid gap-1 text-sm font-medium text-card-foreground">
            Queixa principal
            <input
              value={formulario.queixaPrincipal}
              onChange={(event) => setFormulario((atual) => ({ ...atual, queixaPrincipal: event.target.value }))}
              className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Ex.: manchas, acne, flacidez, relaxamento"
              required
            />
          </label>
        </div>

        <div className="grid gap-3 lg:grid-cols-2">
          <CampoTextoBeauty label="Histórico estético" value={formulario.historicoEstetico} onChange={(value) => setFormulario((atual) => ({ ...atual, historicoEstetico: value }))} placeholder="Procedimentos já realizados, rotina de cuidados e resposta da pele." />
          <CampoTextoBeauty label="Procedimentos recentes" value={formulario.procedimentosRecentes} onChange={(value) => setFormulario((atual) => ({ ...atual, procedimentosRecentes: value }))} placeholder="Peelings, laser, depilação, tratamentos capilares ou outros procedimentos." />
          <CampoTextoBeauty label="Alergias" value={formulario.alergias} onChange={(value) => setFormulario((atual) => ({ ...atual, alergias: value }))} placeholder="Alergias conhecidas a cosméticos, ativos, alimentos ou medicamentos." />
          <CampoTextoBeauty label="Medicamentos em uso" value={formulario.medicamentos} onChange={(value) => setFormulario((atual) => ({ ...atual, medicamentos: value }))} placeholder="Medicamentos, ácidos, isotretinoína, anticoagulantes ou outros pontos relevantes." />
          <CampoTextoBeauty label="Contraindicações" value={formulario.contraindicacoes} onChange={(value) => setFormulario((atual) => ({ ...atual, contraindicacoes: value }))} placeholder="Registre contraindicações, restrições e cuidados obrigatórios." />
          <CampoTextoBeauty label="Observações profissionais" value={formulario.observacoes} onChange={(value) => setFormulario((atual) => ({ ...atual, observacoes: value }))} placeholder="Observações de avaliação inicial e conduta profissional." />
        </div>

        <div className="rounded-lg border bg-background p-3">
          <p className="text-sm font-semibold text-card-foreground">Alertas de segurança</p>
          <p className="mt-1 text-xs leading-5 text-muted-foreground">Use texto e marcações para que a equipe não dependa apenas de cor ao avaliar riscos.</p>
          <div className="mt-3 grid gap-2 sm:grid-cols-2 lg:grid-cols-3">
            <CheckboxFichaBeauty label="Gestante" checked={formulario.gestante} onChange={(checked) => setFormulario((atual) => ({ ...atual, gestante: checked }))} />
            <CheckboxFichaBeauty label="Lactante" checked={formulario.lactante} onChange={(checked) => setFormulario((atual) => ({ ...atual, lactante: checked }))} />
            <CheckboxFichaBeauty label="Pele sensível" checked={formulario.sensibilidadePele} onChange={(checked) => setFormulario((atual) => ({ ...atual, sensibilidadePele: checked }))} />
            <CheckboxFichaBeauty label="Usa ácidos" checked={formulario.usaAcidos} onChange={(checked) => setFormulario((atual) => ({ ...atual, usaAcidos: checked }))} />
            <CheckboxFichaBeauty label="Exposição solar intensa" checked={formulario.exposicaoSolarIntensa} onChange={(checked) => setFormulario((atual) => ({ ...atual, exposicaoSolarIntensa: checked }))} />
          </div>
        </div>

        <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div className="min-h-5 text-sm">
            {mensagem ? <span className="font-medium text-emerald-700">{mensagem}</span> : null}
            {salvarFichaMutation.isError ? <span className="font-medium text-destructive">Não foi possível salvar a ficha estética.</span> : null}
          </div>
          <button
            type="submit"
            disabled={salvarFichaMutation.isPending}
            className="inline-flex h-11 items-center justify-center gap-2 rounded-md bg-rose-900 px-4 text-sm font-semibold text-white shadow-sm transition hover:bg-rose-950 disabled:cursor-not-allowed disabled:opacity-70"
          >
            {salvarFichaMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <Save className="h-4 w-4" />}
            {fichaAtual ? "Atualizar ficha estética" : "Criar ficha estética"}
          </button>
        </div>
      </form>

      <div className="mt-4 rounded-lg border bg-background p-4">
        <p className="text-sm font-semibold text-card-foreground">Histórico de avaliações</p>
        <div className="mt-3 grid gap-2">
          {fichasQuery.isLoading ? (
            <div className="flex min-h-20 items-center justify-center text-sm text-muted-foreground">
              <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
              Carregando histórico
            </div>
          ) : fichasQuery.data?.itens.length ? (
            fichasQuery.data.itens.map((ficha) => <LinhaHistoricoFichaBeauty key={ficha.id} ficha={ficha} />)
          ) : (
            <div className="rounded-md border bg-white p-3 text-sm text-muted-foreground">Nenhuma ficha estética registrada para este cliente.</div>
          )}
        </div>
      </div>
    </section>
  );
}

function ProtocolosBeautyPainel({ empresaId, clienteId }: { empresaId: string; clienteId: string | null }) {
  const queryClient = useQueryClient();
  const [protocoloSelecionadoId, setProtocoloSelecionadoId] = useState<string | null>(null);
  const [formularioProtocolo, setFormularioProtocolo] = useState<FormularioProtocoloBeauty>(protocoloVazio);
  const [formularioSessao, setFormularioSessao] = useState<FormularioSessaoBeauty>(sessaoVazia);
  const [mensagem, setMensagem] = useState<string | null>(null);

  const protocolosQuery = useQuery({
    queryKey: ["beauty-pro-protocolos", empresaId, clienteId],
    queryFn: () => listarProtocolosBeautyPro({ empresaId, clienteId: clienteId ?? "" }),
    enabled: Boolean(empresaId && clienteId)
  });

  const protocolos = protocolosQuery.data?.itens ?? [];

  useEffect(() => {
    setMensagem(null);
    if (!protocolos.length) {
      setProtocoloSelecionadoId(null);
      return;
    }
    if (!protocoloSelecionadoId || !protocolos.some((protocolo) => protocolo.id === protocoloSelecionadoId)) {
      setProtocoloSelecionadoId(protocolos[0].id);
    }
  }, [clienteId, protocoloSelecionadoId, protocolos]);

  useEffect(() => {
    setFormularioProtocolo(protocoloVazio);
    setFormularioSessao(sessaoVazia);
  }, [clienteId]);

  const protocoloSelecionado = protocolos.find((protocolo) => protocolo.id === protocoloSelecionadoId) ?? null;

  const criarProtocoloMutation = useMutation({
    mutationFn: (dados: CriarProtocoloBeautyProInput) => {
      if (!clienteId) {
        throw new Error("Selecione um cliente.");
      }
      return criarProtocoloBeautyPro({ empresaId, clienteId, dados });
    },
    onSuccess: async (protocolo) => {
      setMensagem("Protocolo criado com sucesso.");
      setFormularioProtocolo(protocoloVazio);
      setProtocoloSelecionadoId(protocolo.id);
      await queryClient.invalidateQueries({ queryKey: ["beauty-pro-protocolos", empresaId, clienteId] });
    }
  });

  const registrarSessaoMutation = useMutation({
    mutationFn: (dados: RegistrarSessaoProtocoloBeautyProInput) => {
      if (!clienteId || !protocoloSelecionadoId) {
        throw new Error("Selecione um protocolo.");
      }
      return registrarSessaoProtocoloBeautyPro({ empresaId, clienteId, protocoloId: protocoloSelecionadoId, dados });
    },
    onSuccess: async () => {
      setMensagem("Sessão registrada no histórico do protocolo.");
      setFormularioSessao(sessaoVazia);
      await queryClient.invalidateQueries({ queryKey: ["beauty-pro-protocolos", empresaId, clienteId] });
    }
  });

  if (!clienteId) {
    return <EstadoBeautyPro titulo="Protocolos Beauty" descricao="Selecione um cliente para criar protocolos, pacotes de sessões e evoluções." />;
  }

  function criarProtocolo(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMensagem(null);
    criarProtocoloMutation.mutate({
      nome: formularioProtocolo.nome.trim(),
      tipo: formularioProtocolo.tipo,
      objetivo: formularioProtocolo.objetivo.trim(),
      quantidadeSessoesPrevistas: Number(formularioProtocolo.quantidadeSessoesPrevistas),
      observacoes: textoOuNull(formularioProtocolo.observacoes)
    });
  }

  function registrarSessao(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMensagem(null);
    registrarSessaoMutation.mutate({
      realizadaEm: new Date().toISOString(),
      descricaoExecucao: formularioSessao.descricaoExecucao.trim(),
      evolucaoCliente: textoOuNull(formularioSessao.evolucaoCliente),
      produtosUtilizados: textoOuNull(formularioSessao.produtosUtilizados),
      orientacoes: textoOuNull(formularioSessao.orientacoes)
    });
  }

  return (
    <section className="rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-rose-900">Protocolos, sessões e evolução</p>
          <p className="text-sm leading-6 text-muted-foreground">Crie pacotes por cliente, registre execução e acompanhe evolução por sessão.</p>
        </div>
        <span className="w-fit rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">Operacional</span>
      </div>

      <div className="mt-4 grid gap-4 xl:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)]">
        <form className="grid gap-3 rounded-lg border bg-background p-3" onSubmit={criarProtocolo}>
          <p className="text-sm font-semibold text-card-foreground">Novo protocolo</p>
          <label className="grid gap-1 text-sm font-medium text-card-foreground">
            Nome
            <input
              value={formularioProtocolo.nome}
              onChange={(event) => setFormularioProtocolo((atual) => ({ ...atual, nome: event.target.value }))}
              className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              placeholder="Ex.: Protocolo facial clareador"
              required
            />
          </label>
          <div className="grid gap-3 sm:grid-cols-2">
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Tipo
              <select
                value={formularioProtocolo.tipo}
                onChange={(event) => setFormularioProtocolo((atual) => ({ ...atual, tipo: event.target.value as TipoProtocoloBeautyPro }))}
                className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
              >
                {tiposProtocolo.map((tipo) => (
                  <option key={tipo.value} value={tipo.value}>
                    {tipo.label}
                  </option>
                ))}
              </select>
            </label>
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Sessões previstas
              <input
                type="number"
                min={1}
                max={60}
                value={formularioProtocolo.quantidadeSessoesPrevistas}
                onChange={(event) => setFormularioProtocolo((atual) => ({ ...atual, quantidadeSessoesPrevistas: event.target.value }))}
                className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                required
              />
            </label>
          </div>
          <CampoTextoBeauty label="Objetivo do protocolo" value={formularioProtocolo.objetivo} onChange={(value) => setFormularioProtocolo((atual) => ({ ...atual, objetivo: value }))} placeholder="Objetivo, área tratada e resultado esperado." />
          <CampoTextoBeauty label="Observações do pacote" value={formularioProtocolo.observacoes} onChange={(value) => setFormularioProtocolo((atual) => ({ ...atual, observacoes: value }))} placeholder="Cuidados, frequência e observações operacionais." />
          <button
            type="submit"
            disabled={criarProtocoloMutation.isPending}
            className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-rose-900 px-4 text-sm font-semibold text-white transition hover:bg-rose-950 disabled:opacity-70"
          >
            {criarProtocoloMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <Sparkles className="h-4 w-4" />}
            Criar protocolo
          </button>
        </form>

        <div className="grid gap-3">
          <div className="grid max-h-[360px] gap-2 overflow-y-auto pr-1">
            {protocolosQuery.isLoading ? (
              <div className="flex min-h-24 items-center justify-center rounded-md border bg-background text-sm text-muted-foreground">
                <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
                Carregando protocolos
              </div>
            ) : protocolos.length ? (
              protocolos.map((protocolo) => (
                <LinhaProtocoloBeauty
                  key={protocolo.id}
                  protocolo={protocolo}
                  selecionado={protocolo.id === protocoloSelecionadoId}
                  onSelecionar={() => setProtocoloSelecionadoId(protocolo.id)}
                />
              ))
            ) : (
              <div className="rounded-md border bg-background p-4 text-sm text-muted-foreground">Nenhum protocolo criado para este cliente.</div>
            )}
          </div>

          {protocoloSelecionado ? (
            <form className="grid gap-3 rounded-lg border bg-background p-3" onSubmit={registrarSessao}>
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-sm font-semibold text-card-foreground">Registrar sessão</p>
                  <p className="mt-1 text-xs text-muted-foreground">
                    {protocoloSelecionado.status === "CONCLUIDO"
                      ? "Pacote concluído. Histórico de sessões preservado."
                      : `Próxima sessão: ${protocoloSelecionado.sessoesRealizadas + 1} de ${protocoloSelecionado.quantidadeSessoesPrevistas}`}
                  </p>
                </div>
                <span className={cn("rounded-md border px-2 py-1 text-xs font-semibold", classeStatusPacote(protocoloSelecionado.status))}>
                  {protocoloSelecionado.statusRotulo}
                </span>
              </div>
              <CampoTextoBeauty label="Execução da sessão" value={formularioSessao.descricaoExecucao} onChange={(value) => setFormularioSessao((atual) => ({ ...atual, descricaoExecucao: value }))} placeholder="Descreva o que foi executado na sessão." />
              <CampoTextoBeauty label="Evolução do cliente" value={formularioSessao.evolucaoCliente} onChange={(value) => setFormularioSessao((atual) => ({ ...atual, evolucaoCliente: value }))} placeholder="Resposta observada, tolerância, percepção e evolução." />
              <CampoTextoBeauty label="Produtos utilizados" value={formularioSessao.produtosUtilizados} onChange={(value) => setFormularioSessao((atual) => ({ ...atual, produtosUtilizados: value }))} placeholder="Produtos, insumos ou equipamentos usados." />
              <CampoTextoBeauty label="Orientações" value={formularioSessao.orientacoes} onChange={(value) => setFormularioSessao((atual) => ({ ...atual, orientacoes: value }))} placeholder="Cuidados pós sessão e recomendações de acompanhamento." />
              <button
                type="submit"
                disabled={registrarSessaoMutation.isPending || protocoloSelecionado.status === "CONCLUIDO" || protocoloSelecionado.status === "CANCELADO"}
                className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-rose-900 px-4 text-sm font-semibold text-white transition hover:bg-rose-950 disabled:cursor-not-allowed disabled:opacity-70"
              >
                {registrarSessaoMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <Save className="h-4 w-4" />}
                Registrar sessão
              </button>
            </form>
          ) : null}

          <div className="min-h-5 text-sm">
            {mensagem ? <span className="font-medium text-emerald-700">{mensagem}</span> : null}
            {criarProtocoloMutation.isError || registrarSessaoMutation.isError ? <span className="font-medium text-destructive">Não foi possível salvar protocolo ou sessão.</span> : null}
          </div>
        </div>
      </div>
    </section>
  );
}

function SegurancaOperacionalBeautyPainel({ empresaId, clienteId }: { empresaId: string; clienteId: string | null }) {
  const queryClient = useQueryClient();
  const [formularioTermo, setFormularioTermo] = useState<FormularioTermoBeauty>(termoVazio);
  const [formularioEvidencia, setFormularioEvidencia] = useState<FormularioEvidenciaBeauty>(evidenciaVazia);
  const [formularioProduto, setFormularioProduto] = useState<FormularioProdutoBeauty>(produtoVazio);
  const [mensagem, setMensagem] = useState<string | null>(null);

  const protocolosQuery = useQuery({
    queryKey: ["beauty-pro-protocolos", empresaId, clienteId],
    queryFn: () => listarProtocolosBeautyPro({ empresaId, clienteId: clienteId ?? "" }),
    enabled: Boolean(empresaId && clienteId)
  });

  const segurancaQuery = useQuery({
    queryKey: ["beauty-pro-seguranca", empresaId, clienteId],
    queryFn: () => consultarSegurancaOperacionalBeautyPro({ empresaId, clienteId: clienteId ?? "" }),
    enabled: Boolean(empresaId && clienteId)
  });

  const protocolos = protocolosQuery.data?.itens ?? [];
  const seguranca = segurancaQuery.data;

  useEffect(() => {
    setMensagem(null);
    setFormularioTermo(termoVazio);
    setFormularioEvidencia(evidenciaVazia);
    setFormularioProduto(produtoVazio);
  }, [clienteId]);

  const criarTermoMutation = useMutation({
    mutationFn: (dados: CriarTermoConsentimentoBeautyProInput) => {
      if (!clienteId) {
        throw new Error("Selecione um cliente.");
      }
      return criarTermoConsentimentoBeautyPro({ empresaId, clienteId, dados });
    },
    onSuccess: async () => {
      setMensagem("Termo registrado no histórico do cliente.");
      setFormularioTermo(termoVazio);
      await invalidarSegurancaBeauty(queryClient, empresaId, clienteId);
    }
  });

  const criarEvidenciaMutation = useMutation({
    mutationFn: (dados: CriarEvidenciaEvolucaoBeautyProInput) => {
      if (!clienteId) {
        throw new Error("Selecione um cliente.");
      }
      return criarEvidenciaEvolucaoBeautyPro({ empresaId, clienteId, dados });
    },
    onSuccess: async () => {
      setMensagem("Evidência segura registrada sem foto real.");
      setFormularioEvidencia(evidenciaVazia);
      await invalidarSegurancaBeauty(queryClient, empresaId, clienteId);
    }
  });

  const vincularProdutoMutation = useMutation({
    mutationFn: (dados: VincularProdutoBeautyProInput) => {
      if (!clienteId) {
        throw new Error("Selecione um cliente.");
      }
      return vincularProdutoBeautyPro({ empresaId, clienteId, dados });
    },
    onSuccess: async () => {
      setMensagem("Produto e lote vinculados ao histórico Beauty.");
      setFormularioProduto(produtoVazio);
      await invalidarSegurancaBeauty(queryClient, empresaId, clienteId);
    }
  });

  if (!clienteId) {
    return <EstadoBeautyPro titulo="Termos, evidências e produtos" descricao="Selecione um cliente para registrar consentimentos, placeholders seguros e rastreabilidade de lotes." />;
  }

  function criarTermo(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMensagem(null);
    criarTermoMutation.mutate({
      protocoloId: textoOuNull(formularioTermo.protocoloId),
      titulo: formularioTermo.titulo.trim(),
      conteudo: formularioTermo.conteudo.trim(),
      aceiteProfissional: formularioTermo.aceiteProfissional
    });
  }

  function criarEvidencia(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMensagem(null);
    criarEvidenciaMutation.mutate({
      protocoloId: textoOuNull(formularioEvidencia.protocoloId),
      tipoPlaceholder: formularioEvidencia.tipoPlaceholder,
      titulo: formularioEvidencia.titulo.trim(),
      descricao: formularioEvidencia.descricao.trim(),
      observacoesPrivacidade: textoOuNull(formularioEvidencia.observacoesPrivacidade)
    });
  }

  function selecionarProdutoEstoque(produtoId: string) {
    const produto = seguranca?.produtosEstoque.find((item) => item.id === produtoId) ?? null;
    setFormularioProduto((atual) => ({
      ...atual,
      produtoEstoqueId: produtoId,
      nomeProduto: produto?.nome ?? "",
      lote: produto?.lote ?? "",
      validade: produto?.validade ?? "",
      unidade: produto?.unidade ?? atual.unidade
    }));
  }

  function vincularProduto(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMensagem(null);
    vincularProdutoMutation.mutate({
      protocoloId: textoOuNull(formularioProduto.protocoloId),
      produtoEstoqueId: textoOuNull(formularioProduto.produtoEstoqueId),
      nomeProduto: textoOuNull(formularioProduto.nomeProduto),
      lote: textoOuNull(formularioProduto.lote),
      validade: textoOuNull(formularioProduto.validade),
      quantidade: Number(formularioProduto.quantidade),
      unidade: formularioProduto.unidade.trim(),
      observacoes: textoOuNull(formularioProduto.observacoes)
    });
  }

  return (
    <section className="rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-rose-900">Termos, evidências e produtos</p>
          <p className="text-sm leading-6 text-muted-foreground">Registre consentimentos, evolução visual segura e produtos/lotes vinculados ao atendimento.</p>
        </div>
        <span className="w-fit rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">Segurança operacional</span>
      </div>

      {segurancaQuery.isLoading ? (
        <div className="mt-4 flex min-h-32 items-center justify-center rounded-lg border bg-background text-sm text-muted-foreground">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando segurança operacional
        </div>
      ) : (
        <div className="mt-4 grid gap-4">
          <div className="grid gap-3 md:grid-cols-3">
            <ResumoFichaBeauty rotulo="Termos" valor={seguranca?.termos.length ?? 0} />
            <ResumoFichaBeauty rotulo="Evidências" valor={seguranca?.evidencias.length ?? 0} />
            <ResumoFichaBeauty rotulo="Produtos" valor={seguranca?.produtosUtilizados.length ?? 0} destaque={(seguranca?.produtosUtilizados ?? []).some((produto) => produto.alertaValidade || produto.alertaEstoqueBaixo)} />
          </div>

          <div className="grid gap-4 xl:grid-cols-3">
            <form className="grid gap-3 rounded-lg border bg-background p-3" onSubmit={criarTermo}>
              <p className="text-sm font-semibold text-card-foreground">Novo termo</p>
              <SelecaoProtocoloBeauty value={formularioTermo.protocoloId} protocolos={protocolos} onChange={(value) => setFormularioTermo((atual) => ({ ...atual, protocoloId: value }))} />
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Título
                <input
                  value={formularioTermo.titulo}
                  onChange={(event) => setFormularioTermo((atual) => ({ ...atual, titulo: event.target.value }))}
                  className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                  required
                />
              </label>
              <CampoTextoBeauty label="Conteúdo" value={formularioTermo.conteudo} onChange={(value) => setFormularioTermo((atual) => ({ ...atual, conteudo: value }))} placeholder="Orientações, riscos, cuidados e ciência do cliente." />
              <CheckboxFichaBeauty label="Aceite profissional registrado" checked={formularioTermo.aceiteProfissional} onChange={(checked) => setFormularioTermo((atual) => ({ ...atual, aceiteProfissional: checked }))} />
              <button type="submit" disabled={criarTermoMutation.isPending} className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-rose-900 px-4 text-sm font-semibold text-white transition hover:bg-rose-950 disabled:opacity-70">
                {criarTermoMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <FileText className="h-4 w-4" />}
                Registrar termo
              </button>
            </form>

            <form className="grid gap-3 rounded-lg border bg-background p-3" onSubmit={criarEvidencia}>
              <p className="text-sm font-semibold text-card-foreground">Evidência segura</p>
              <SelecaoProtocoloBeauty value={formularioEvidencia.protocoloId} protocolos={protocolos} onChange={(value) => setFormularioEvidencia((atual) => ({ ...atual, protocoloId: value }))} />
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Tipo de placeholder
                <select
                  value={formularioEvidencia.tipoPlaceholder}
                  onChange={(event) => setFormularioEvidencia((atual) => ({ ...atual, tipoPlaceholder: event.target.value as TipoPlaceholderEvolucaoBeautyPro }))}
                  className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                >
                  {tiposPlaceholder.map((tipo) => (
                    <option key={tipo.value} value={tipo.value}>
                      {tipo.label}
                    </option>
                  ))}
                </select>
              </label>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Título
                <input
                  value={formularioEvidencia.titulo}
                  onChange={(event) => setFormularioEvidencia((atual) => ({ ...atual, titulo: event.target.value }))}
                  className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                  required
                />
              </label>
              <CampoTextoBeauty label="Descrição" value={formularioEvidencia.descricao} onChange={(value) => setFormularioEvidencia((atual) => ({ ...atual, descricao: value }))} placeholder="Evolução textual, mapa da área tratada e resposta observada." />
              <CampoTextoBeauty label="Privacidade" value={formularioEvidencia.observacoesPrivacidade} onChange={(value) => setFormularioEvidencia((atual) => ({ ...atual, observacoesPrivacidade: value }))} placeholder="Observação de privacidade e autorização de imagem futura." />
              <button type="submit" disabled={criarEvidenciaMutation.isPending} className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-rose-900 px-4 text-sm font-semibold text-white transition hover:bg-rose-950 disabled:opacity-70">
                {criarEvidenciaMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <UserRoundCheck className="h-4 w-4" />}
                Registrar evidência
              </button>
            </form>

            <form className="grid gap-3 rounded-lg border bg-background p-3" onSubmit={vincularProduto}>
              <p className="text-sm font-semibold text-card-foreground">Produto e lote</p>
              <SelecaoProtocoloBeauty value={formularioProduto.protocoloId} protocolos={protocolos} onChange={(value) => setFormularioProduto((atual) => ({ ...atual, protocoloId: value }))} />
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Produto do estoque
                <select
                  value={formularioProduto.produtoEstoqueId}
                  onChange={(event) => selecionarProdutoEstoque(event.target.value)}
                  className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                >
                  <option value="">Produto manual</option>
                  {(seguranca?.produtosEstoque ?? []).map((produto) => (
                    <option key={produto.id} value={produto.id}>
                      {produto.nome} {produto.lote ? `• ${produto.lote}` : ""}
                    </option>
                  ))}
                </select>
              </label>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Nome do produto
                <input
                  value={formularioProduto.nomeProduto}
                  onChange={(event) => setFormularioProduto((atual) => ({ ...atual, nomeProduto: event.target.value }))}
                  className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                  placeholder="Ex.: sérum facial"
                  required={!formularioProduto.produtoEstoqueId}
                />
              </label>
              <div className="grid gap-3 sm:grid-cols-3">
                <label className="grid gap-1 text-sm font-medium text-card-foreground">
                  Lote
                  <input value={formularioProduto.lote} onChange={(event) => setFormularioProduto((atual) => ({ ...atual, lote: event.target.value }))} className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" />
                </label>
                <label className="grid gap-1 text-sm font-medium text-card-foreground">
                  Validade
                  <input type="date" value={formularioProduto.validade} onChange={(event) => setFormularioProduto((atual) => ({ ...atual, validade: event.target.value }))} className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" />
                </label>
                <label className="grid gap-1 text-sm font-medium text-card-foreground">
                  Qtd.
                  <input type="number" min="0.001" step="0.001" value={formularioProduto.quantidade} onChange={(event) => setFormularioProduto((atual) => ({ ...atual, quantidade: event.target.value }))} className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" required />
                </label>
              </div>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Unidade
                <input value={formularioProduto.unidade} onChange={(event) => setFormularioProduto((atual) => ({ ...atual, unidade: event.target.value }))} className="h-10 rounded-md border bg-white px-3 text-sm uppercase outline-none focus:border-primary focus:ring-2 focus:ring-ring" required />
              </label>
              <CampoTextoBeauty label="Observações" value={formularioProduto.observacoes} onChange={(value) => setFormularioProduto((atual) => ({ ...atual, observacoes: value }))} placeholder="Uso no protocolo, lote, validade e cuidados." />
              <button type="submit" disabled={vincularProdutoMutation.isPending} className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-rose-900 px-4 text-sm font-semibold text-white transition hover:bg-rose-950 disabled:opacity-70">
                {vincularProdutoMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <PackageCheck className="h-4 w-4" />}
                Vincular produto
              </button>
            </form>
          </div>

          <div className="grid gap-3 lg:grid-cols-3">
            <HistoricoTermosBeauty termos={seguranca?.termos ?? []} />
            <HistoricoEvidenciasBeauty evidencias={seguranca?.evidencias ?? []} />
            <HistoricoProdutosBeauty produtos={seguranca?.produtosUtilizados ?? []} estoque={seguranca?.produtosEstoque ?? []} />
          </div>

          <div className="min-h-5 text-sm">
            {mensagem ? <span className="font-medium text-emerald-700">{mensagem}</span> : null}
            {criarTermoMutation.isError || criarEvidenciaMutation.isError || vincularProdutoMutation.isError ? <span className="font-medium text-destructive">Não foi possível registrar segurança operacional.</span> : null}
          </div>
        </div>
      )}
    </section>
  );
}

function IntegracoesOperacionaisBeautyPainel({ empresaId }: { empresaId: string }) {
  const [busca, setBusca] = useState("");
  const [filtroMargem, setFiltroMargem] = useState<"TODAS" | "ALERTA">("TODAS");

  const integracoesQuery = useQuery({
    queryKey: ["beauty-pro-integracoes", empresaId],
    queryFn: () => consultarIntegracoesOperacionaisBeautyPro(empresaId),
    enabled: Boolean(empresaId)
  });

  const integracoes = integracoesQuery.data;
  const termo = busca.trim().toLowerCase();
  const servicos = filtrarServicosBeauty(integracoes?.servicos ?? [], termo);
  const simulacoes = filtrarSimulacoesBeauty(integracoes?.simulacoes ?? [], termo, filtroMargem);
  const agenda = filtrarAgendaBeauty(integracoes?.agenda ?? [], termo);

  return (
    <section className="rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-rose-900">Agenda, serviços e precificação</p>
          <p className="text-sm leading-6 text-muted-foreground">Fluxo integrado para sair do cliente, passar por protocolo/sessão e revisar agenda, serviço e margem.</p>
        </div>
        <span className="w-fit rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">Beauty Pro conectado</span>
      </div>

      <div className="mt-4 grid gap-3 lg:grid-cols-[minmax(0,1fr)_220px]">
        <label className="grid gap-1 text-sm font-medium text-card-foreground">
          Busca operacional
          <input
            value={busca}
            onChange={(event) => setBusca(event.target.value)}
            className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
            placeholder="Cliente, serviço, sala, procedimento"
          />
        </label>
        <label className="grid gap-1 text-sm font-medium text-card-foreground">
          Margem
          <select value={filtroMargem} onChange={(event) => setFiltroMargem(event.target.value as "TODAS" | "ALERTA")} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring">
            <option value="TODAS">Todas</option>
            <option value="ALERTA">Somente alertas</option>
          </select>
        </label>
      </div>

      {integracoesQuery.isLoading ? (
        <div className="mt-4 flex min-h-32 items-center justify-center rounded-lg border bg-background text-sm text-muted-foreground">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando integrações Beauty
        </div>
      ) : integracoesQuery.isError || !integracoes ? (
        <EstadoBeautyPro titulo="Integrações indisponíveis" descricao="Não foi possível carregar agenda, serviços e precificação agora." alerta />
      ) : (
        <div className="mt-4 grid gap-4">
          <div className="grid gap-3 md:grid-cols-3">
            <ResumoFichaBeauty rotulo="Agenda 14 dias" valor={agenda.length} />
            <ResumoFichaBeauty rotulo="Serviços Beauty" valor={servicos.length} />
            <ResumoFichaBeauty rotulo="Alertas margem" valor={simulacoes.filter((simulacao) => simulacao.alerta).length} destaque={simulacoes.some((simulacao) => simulacao.alerta)} />
          </div>

          <div className="grid gap-4 xl:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)]">
            <div className="grid gap-4">
              <ListaAgendaBeauty agenda={agenda} />
              <ListaServicosBeauty servicos={servicos} />
            </div>
            <ListaSimulacoesBeauty simulacoes={simulacoes} />
          </div>
        </div>
      )}
    </section>
  );
}

async function invalidarSegurancaBeauty(queryClient: ReturnType<typeof useQueryClient>, empresaId: string, clienteId: string | null) {
  await Promise.all([
    queryClient.invalidateQueries({ queryKey: ["beauty-pro-seguranca", empresaId, clienteId] }),
    queryClient.invalidateQueries({ queryKey: ["beauty-pro-visao", empresaId] })
  ]);
}

function SelecaoProtocoloBeauty({ value, protocolos, onChange }: { value: string; protocolos: ProtocoloBeautyPro[]; onChange: (value: string) => void }) {
  return (
    <label className="grid gap-1 text-sm font-medium text-card-foreground">
      Protocolo vinculado
      <select value={value} onChange={(event) => onChange(event.target.value)} className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring">
        <option value="">Sem protocolo específico</option>
        {protocolos.map((protocolo) => (
          <option key={protocolo.id} value={protocolo.id}>
            {protocolo.nome}
          </option>
        ))}
      </select>
    </label>
  );
}

function HistoricoTermosBeauty({ termos }: { termos: TermoConsentimentoBeautyPro[] }) {
  return (
    <div className="rounded-lg border bg-background p-3">
      <p className="text-sm font-semibold text-card-foreground">Histórico de termos</p>
      <div className="mt-3 grid max-h-72 gap-2 overflow-y-auto pr-1">
        {termos.length ? (
          termos.map((termo) => (
            <article key={termo.id} className="rounded-md border bg-white p-3">
              <div className="flex items-start justify-between gap-3">
                <p className="text-sm font-semibold text-card-foreground">{termo.titulo}</p>
                <span className={cn("shrink-0 rounded-md border px-2 py-1 text-xs font-semibold", termo.aceiteProfissional ? "bg-emerald-50 text-emerald-800" : "bg-amber-50 text-amber-900")}>{termo.statusRotulo}</span>
              </div>
              <p className="mt-2 line-clamp-3 text-xs leading-5 text-muted-foreground">{termo.conteudo}</p>
              <p className="mt-2 text-xs font-medium text-muted-foreground">{formatarDataHora(termo.criadoEm)}</p>
            </article>
          ))
        ) : (
          <div className="rounded-md border bg-white p-3 text-sm text-muted-foreground">Nenhum termo registrado para este cliente.</div>
        )}
      </div>
    </div>
  );
}

function HistoricoEvidenciasBeauty({ evidencias }: { evidencias: EvidenciaEvolucaoBeautyPro[] }) {
  return (
    <div className="rounded-lg border bg-background p-3">
      <p className="text-sm font-semibold text-card-foreground">Evidências seguras</p>
      <div className="mt-3 grid max-h-72 gap-2 overflow-y-auto pr-1">
        {evidencias.length ? (
          evidencias.map((evidencia) => (
            <article key={evidencia.id} className="rounded-md border border-sky-200 bg-white p-3">
              <div className="flex items-start justify-between gap-3">
                <p className="text-sm font-semibold text-card-foreground">{evidencia.titulo}</p>
                <span className="shrink-0 rounded-md border bg-sky-50 px-2 py-1 text-xs font-semibold text-sky-800">{evidencia.tipoPlaceholderRotulo}</span>
              </div>
              <p className="mt-2 text-xs leading-5 text-muted-foreground">{evidencia.descricao}</p>
              <p className="mt-2 rounded-md border bg-sky-50 px-2 py-1 text-xs font-medium text-sky-900">{evidencia.avisoPrivacidade}</p>
            </article>
          ))
        ) : (
          <div className="rounded-md border bg-white p-3 text-sm text-muted-foreground">Nenhuma evidência segura registrada.</div>
        )}
      </div>
    </div>
  );
}

function HistoricoProdutosBeauty({ produtos, estoque }: { produtos: ProdutoUtilizadoBeautyPro[]; estoque: ProdutoBeautyEstoque[] }) {
  return (
    <div className="rounded-lg border bg-background p-3">
      <p className="text-sm font-semibold text-card-foreground">Produtos e lotes</p>
      <div className="mt-3 grid max-h-72 gap-2 overflow-y-auto pr-1">
        {produtos.length ? (
          produtos.map((produto) => (
            <article key={produto.id} className={cn("rounded-md border bg-white p-3", produto.alertaValidade || produto.alertaEstoqueBaixo ? "border-amber-300 bg-amber-50/50" : "")}>
              <div className="flex items-start justify-between gap-3">
                <p className="text-sm font-semibold text-card-foreground">{produto.nomeProduto}</p>
                <span className={cn("shrink-0 rounded-md border px-2 py-1 text-xs font-semibold", produto.alertaValidade || produto.alertaEstoqueBaixo ? "bg-amber-50 text-amber-900" : "bg-emerald-50 text-emerald-800")}>{produto.statusRotulo}</span>
              </div>
              <p className="mt-2 text-xs leading-5 text-muted-foreground">
                {produto.quantidade} {produto.unidade} {produto.lote ? `• lote ${produto.lote}` : ""} {produto.validade ? `• validade ${formatarDataCurta(produto.validade)}` : ""}
              </p>
              {produto.observacoes ? <p className="mt-2 text-xs leading-5 text-muted-foreground">{produto.observacoes}</p> : null}
            </article>
          ))
        ) : (
          <div className="rounded-md border bg-white p-3 text-sm text-muted-foreground">Nenhum produto vinculado a protocolos ou sessões.</div>
        )}
      </div>
      {estoque.some((produto) => produto.estoqueBaixo || produto.validadeEmAlerta) ? (
        <div className="mt-3 rounded-md border border-amber-300 bg-amber-50 p-3 text-xs leading-5 text-amber-900">
          Existem produtos do estoque com validade próxima ou estoque baixo. Vincule com atenção antes de registrar sessões.
        </div>
      ) : null}
    </div>
  );
}

function ListaAgendaBeauty({ agenda }: { agenda: AgendaBeautyPro[] }) {
  return (
    <div className="rounded-lg border bg-background p-3">
      <p className="text-sm font-semibold text-card-foreground">Agenda Beauty</p>
      <div className="mt-3 grid max-h-72 gap-2 overflow-y-auto pr-1">
        {agenda.length ? (
          agenda.map((compromisso) => (
            <article key={compromisso.id} className="rounded-md border bg-white p-3">
              <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <p className="text-sm font-semibold text-card-foreground">{compromisso.clienteNome ?? "Cliente não informado"}</p>
                  <p className="mt-1 text-xs leading-5 text-muted-foreground">
                    {formatarDataHora(compromisso.inicio)} • {compromisso.profissionalNome ?? "Profissional não informado"} {compromisso.sala ? `• ${compromisso.sala}` : ""}
                  </p>
                </div>
                <span className={cn("w-fit rounded-md border px-2 py-1 text-xs font-semibold", classeStatusAgenda(compromisso.status))}>{compromisso.statusRotulo}</span>
              </div>
              {compromisso.observacoes ? <p className="mt-2 text-xs leading-5 text-muted-foreground">{compromisso.observacoes}</p> : null}
            </article>
          ))
        ) : (
          <div className="rounded-md border bg-white p-3 text-sm text-muted-foreground">Nenhum compromisso Beauty encontrado para o período.</div>
        )}
      </div>
    </div>
  );
}

function ListaServicosBeauty({ servicos }: { servicos: ServicoBeautyPro[] }) {
  return (
    <div className="rounded-lg border bg-background p-3">
      <p className="text-sm font-semibold text-card-foreground">Serviços e procedimentos</p>
      <div className="mt-3 grid max-h-72 gap-2 overflow-y-auto pr-1">
        {servicos.length ? (
          servicos.map((servico) => (
            <article key={servico.id} className="rounded-md border bg-white p-3">
              <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <p className="text-sm font-semibold text-card-foreground">{servico.nome}</p>
                  <p className="mt-1 text-xs leading-5 text-muted-foreground">
                    {servico.duracaoMinutos} min • {formatarMoeda(servico.precoBase)}
                  </p>
                </div>
                <span className={cn("w-fit rounded-md border px-2 py-1 text-xs font-semibold", servico.ativo ? "bg-emerald-50 text-emerald-800" : "bg-slate-50 text-slate-700")}>{servico.ativo ? "Ativo" : "Inativo"}</span>
              </div>
              {servico.descricao ? <p className="mt-2 line-clamp-2 text-xs leading-5 text-muted-foreground">{servico.descricao}</p> : null}
            </article>
          ))
        ) : (
          <div className="rounded-md border bg-white p-3 text-sm text-muted-foreground">Nenhum serviço Beauty encontrado.</div>
        )}
      </div>
    </div>
  );
}

function ListaSimulacoesBeauty({ simulacoes }: { simulacoes: SimulacaoBeautyPro[] }) {
  return (
    <div className="rounded-lg border bg-background p-3">
      <p className="text-sm font-semibold text-card-foreground">Precificação Beauty</p>
      <div className="mt-3 grid max-h-[600px] gap-2 overflow-y-auto pr-1">
        {simulacoes.length ? (
          simulacoes.map((simulacao) => (
            <article key={simulacao.id} className={cn("rounded-md border bg-white p-3", simulacao.alerta ? "border-amber-300 bg-amber-50/60" : "border-emerald-200")}>
              <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <p className="text-sm font-semibold text-card-foreground">{simulacao.nomeProcedimento}</p>
                  <p className="mt-1 text-xs leading-5 text-muted-foreground">
                    Venda {formatarMoeda(simulacao.precoVenda)} • Recomendado {formatarMoeda(simulacao.precoRecomendado)}
                  </p>
                </div>
                <span className={cn("w-fit rounded-md border px-2 py-1 text-xs font-semibold", classeStatusMargem(simulacao.statusMargem))}>{simulacao.statusRotulo}</span>
              </div>
              <div className="mt-3 grid gap-2 sm:grid-cols-3">
                <ResumoFichaBeauty rotulo="Custo" valor={formatarMoeda(simulacao.custoTotal)} texto />
                <ResumoFichaBeauty rotulo="Lucro" valor={formatarMoeda(simulacao.lucroEstimado)} texto destaque={simulacao.lucroEstimado < 0} />
                <ResumoFichaBeauty rotulo="Margem" valor={`${formatarNumero(simulacao.margemRealPercentual)}%`} texto destaque={simulacao.alerta} />
              </div>
              {simulacao.alerta ? <p className="mt-2 text-xs font-medium text-amber-900">Revise custo, duração, preço praticado ou margem antes de repetir este procedimento.</p> : null}
            </article>
          ))
        ) : (
          <div className="rounded-md border bg-white p-3 text-sm text-muted-foreground">Nenhuma simulação Beauty encontrada para o filtro atual.</div>
        )}
      </div>
    </div>
  );
}

function CardIndicadorBeauty({ indicador }: { indicador: IndicadorBeautyPro }) {
  const Icon = iconesIndicadores[indicador.codigo] ?? Sparkles;

  return (
    <article className={cn("rounded-lg border p-4 shadow-sm", classeStatusIndicador(indicador.status))}>
      <div className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-rose-900">
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

function GrupoBeauty({ titulo, itens }: { titulo: string; itens: IndicadorBeautyPro[] }) {
  return (
    <div className="rounded-lg border bg-white p-4 shadow-sm">
      <p className="text-sm font-semibold text-card-foreground">{titulo}</p>
      <div className="mt-3 grid gap-2">
        {itens.map((indicador) => {
          const Icon = iconesIndicadores[indicador.codigo] ?? Sparkles;
          return (
            <div key={indicador.codigo} className="flex items-start justify-between gap-3 rounded-md border bg-background p-3">
              <span className="flex min-w-0 items-start gap-3">
                <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-rose-100 text-rose-900">
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

function CardAtalhoBeauty({ atalho, principal = false }: { atalho: AtalhoBeautyPro; principal?: boolean }) {
  const Icon = iconesAtalhos[atalho.codigo] ?? Sparkles;

  return (
    <article className={cn("min-h-24 rounded-lg border bg-background p-4", principal ? "border-rose-300 bg-rose-50/70" : "")}>
      <span className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-rose-900">
          <Icon className="h-5 w-5" />
        </span>
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-rose-900">{rotuloStatusAtalho(atalho.status)}</span>
      </span>
      <span className="mt-3 block text-sm font-semibold text-card-foreground">{atalho.titulo}</span>
      <span className="mt-2 block text-xs leading-5 text-muted-foreground">{atalho.descricao}</span>
    </article>
  );
}

function LinhaClienteBeauty({ cliente, selecionado, onSelecionar }: { cliente: ClienteBeautyResumo; selecionado: boolean; onSelecionar: () => void }) {
  return (
    <button
      type="button"
      onClick={onSelecionar}
      className={cn("rounded-md border bg-background p-3 text-left transition hover:border-rose-300 hover:bg-rose-50/50", selecionado ? "border-rose-500 bg-rose-50 shadow-sm" : "")}
    >
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-card-foreground">{cliente.nome}</p>
          <p className="mt-1 text-xs font-medium text-muted-foreground">{cliente.telefone ?? "Sem telefone"}</p>
        </div>
        <span className={cn("rounded-md border px-2 py-1 text-xs font-semibold", cliente.ativo ? "bg-rose-50 text-rose-900" : "bg-slate-50 text-slate-700")}>
          {cliente.ativo ? "Ativo" : "Inativo"}
        </span>
      </div>
      {cliente.observacoes ? <p className="mt-2 line-clamp-2 text-xs leading-5 text-muted-foreground">{cliente.observacoes}</p> : null}
    </button>
  );
}

function CampoTextoBeauty({ label, value, onChange, placeholder }: { label: string; value: string; onChange: (value: string) => void; placeholder: string }) {
  return (
    <label className="grid gap-1 text-sm font-medium text-card-foreground">
      {label}
      <textarea
        value={value}
        onChange={(event) => onChange(event.target.value)}
        className="min-h-24 rounded-md border bg-background px-3 py-2 text-sm leading-6 outline-none focus:border-primary focus:ring-2 focus:ring-ring"
        placeholder={placeholder}
      />
    </label>
  );
}

function CheckboxFichaBeauty({ label, checked, onChange }: { label: string; checked: boolean; onChange: (checked: boolean) => void }) {
  return (
    <label className="flex min-h-11 items-center gap-2 rounded-md border bg-white px-3 text-sm font-medium text-card-foreground">
      <input
        type="checkbox"
        checked={checked}
        onChange={(event) => onChange(event.target.checked)}
        className="h-4 w-4 rounded border-muted-foreground accent-rose-900"
      />
      {label}
    </label>
  );
}

function LinhaHistoricoFichaBeauty({ ficha }: { ficha: FichaEsteticaBeautyPro }) {
  return (
    <article className={cn("rounded-md border bg-white p-3", ficha.possuiAlertaContraindicacao ? "border-amber-300" : "")}>
      <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-card-foreground">{ficha.objetivoRotulo}</p>
          <p className="mt-1 text-xs leading-5 text-muted-foreground">{ficha.queixaPrincipal}</p>
        </div>
        <span className={cn("w-fit rounded-md border px-2 py-1 text-xs font-semibold", ficha.possuiAlertaContraindicacao ? "bg-amber-50 text-amber-900" : "bg-emerald-50 text-emerald-800")}>
          {ficha.possuiAlertaContraindicacao ? "Com alerta" : "Sem alerta"}
        </span>
      </div>
      <p className="mt-2 text-xs font-medium text-muted-foreground">Atualizada em {formatarDataHora(ficha.atualizadoEm)}</p>
    </article>
  );
}

function LinhaProtocoloBeauty({ protocolo, selecionado, onSelecionar }: { protocolo: ProtocoloBeautyPro; selecionado: boolean; onSelecionar: () => void }) {
  return (
    <button
      type="button"
      onClick={onSelecionar}
      className={cn("rounded-md border bg-background p-3 text-left transition hover:border-rose-300 hover:bg-rose-50/50", selecionado ? "border-rose-500 bg-rose-50 shadow-sm" : "")}
    >
      <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-card-foreground">{protocolo.nome}</p>
          <p className="mt-1 text-xs leading-5 text-muted-foreground">{protocolo.tipoRotulo} • {protocolo.objetivo}</p>
        </div>
        <span className={cn("w-fit rounded-md border px-2 py-1 text-xs font-semibold", classeStatusPacote(protocolo.status))}>{protocolo.statusRotulo}</span>
      </div>
      <div className="mt-3 grid gap-2 sm:grid-cols-3">
        <ResumoFichaBeauty rotulo="Previstas" valor={protocolo.quantidadeSessoesPrevistas} />
        <ResumoFichaBeauty rotulo="Realizadas" valor={protocolo.sessoesRealizadas} />
        <ResumoFichaBeauty rotulo="Restantes" valor={protocolo.sessoesRestantes} />
      </div>
      {protocolo.sessoes.length ? (
        <div className="mt-3 grid gap-2">
          {protocolo.sessoes.slice(0, 2).map((sessao) => (
            <div key={sessao.id} className="rounded-md border bg-white p-2 text-xs leading-5 text-muted-foreground">
              <span className="font-semibold text-card-foreground">Sessão {sessao.numeroSessao}:</span> {sessao.descricaoExecucao}
            </div>
          ))}
        </div>
      ) : null}
    </button>
  );
}

function ResumoFichaBeauty({ rotulo, valor, texto = false, destaque = false }: { rotulo: string; valor: number | string; texto?: boolean; destaque?: boolean }) {
  return (
    <div className={cn("rounded-md border bg-background px-3 py-2", destaque ? "border-amber-300 bg-amber-50 text-amber-900" : "")}>
      <p className="text-[11px] font-semibold uppercase text-muted-foreground">{rotulo}</p>
      <p className={cn("mt-1 font-semibold text-card-foreground", texto ? "text-xs" : "text-lg")}>{valor}</p>
    </div>
  );
}

function EstadoBeautyPro({ titulo, descricao, alerta = false }: { titulo: string; descricao: string; alerta?: boolean }) {
  return (
    <section className={cn("flex min-h-52 flex-col items-center justify-center rounded-lg border p-6 text-center", alerta ? "border-amber-200 bg-amber-50" : "border-rose-200 bg-rose-50")}>
      {alerta ? <AlertTriangle className="h-8 w-8 text-amber-700" /> : <Scissors className="h-8 w-8 text-rose-900" />}
      <h4 className="mt-3 text-base font-semibold text-card-foreground">{titulo}</h4>
      <p className="mt-1 max-w-md text-sm leading-6 text-muted-foreground">{descricao}</p>
    </section>
  );
}

function formularioDeFicha(ficha: FichaEsteticaBeautyPro): FormularioFichaBeauty {
  return {
    objetivo: ficha.objetivo,
    queixaPrincipal: ficha.queixaPrincipal,
    historicoEstetico: ficha.historicoEstetico ?? "",
    alergias: ficha.alergias ?? "",
    medicamentos: ficha.medicamentos ?? "",
    gestante: ficha.gestante,
    lactante: ficha.lactante,
    sensibilidadePele: ficha.sensibilidadePele,
    usaAcidos: ficha.usaAcidos,
    exposicaoSolarIntensa: ficha.exposicaoSolarIntensa,
    procedimentosRecentes: ficha.procedimentosRecentes ?? "",
    contraindicacoes: ficha.contraindicacoes ?? "",
    observacoes: ficha.observacoes ?? ""
  };
}

function textoOuNull(valor: string) {
  const texto = valor.trim();
  return texto.length ? texto : null;
}

function classeStatusIndicador(status: string) {
  if (status === "ALERTA") {
    return "border-amber-300 bg-amber-50";
  }
  if (status === "SAUDAVEL" || status === "OPERACIONAL") {
    return "border-rose-200 bg-rose-50";
  }
  return "border bg-background";
}

function classeStatusPacote(status: string) {
  const classes: Record<string, string> = {
    ATIVO: "border-emerald-200 bg-emerald-50 text-emerald-800",
    CONCLUIDO: "border-sky-200 bg-sky-50 text-sky-800",
    PAUSADO: "border-amber-200 bg-amber-50 text-amber-900",
    CANCELADO: "border-slate-200 bg-slate-50 text-slate-700"
  };
  return classes[status] ?? "border bg-background text-muted-foreground";
}

function classeStatusAgenda(status: string) {
  const classes: Record<string, string> = {
    AGENDADO: "border-sky-200 bg-sky-50 text-sky-800",
    CONFIRMADO: "border-emerald-200 bg-emerald-50 text-emerald-800",
    REALIZADO: "border-indigo-200 bg-indigo-50 text-indigo-800",
    REMARCADO: "border-amber-200 bg-amber-50 text-amber-900",
    FALTOU: "border-red-200 bg-red-50 text-red-800",
    CANCELADO: "border-slate-200 bg-slate-50 text-slate-700"
  };
  return classes[status] ?? "border bg-background text-muted-foreground";
}

function classeStatusMargem(status: string) {
  const classes: Record<string, string> = {
    SAUDAVEL: "border-emerald-200 bg-emerald-50 text-emerald-800",
    EQUILIBRIO: "border-sky-200 bg-sky-50 text-sky-800",
    MARGEM_BAIXA: "border-amber-200 bg-amber-50 text-amber-900",
    PREJUIZO: "border-red-200 bg-red-50 text-red-800"
  };
  return classes[status] ?? "border bg-background text-muted-foreground";
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

function rotuloStatusFicha(status: string) {
  const rotulos: Record<string, string> = {
    DISPONIVEL: "Disponível",
    PENDENTE: "Pendente"
  };
  return rotulos[status] ?? status;
}

function rotuloStatusContraindicacao(status: string) {
  const rotulos: Record<string, string> = {
    ALERTA: "Com alerta",
    SEM_ALERTA: "Sem alerta"
  };
  return rotulos[status] ?? status;
}

function formatarNumero(valor: number) {
  return new Intl.NumberFormat("pt-BR").format(valor);
}

function formatarMoeda(valor: number) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL"
  }).format(valor);
}

function formatarDataHora(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(valor));
}

function formatarDataCurta(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric"
  }).format(new Date(`${valor}T00:00:00`));
}

function filtrarAgendaBeauty(agenda: AgendaBeautyPro[], termo: string) {
  if (!termo) {
    return agenda;
  }
  return agenda.filter((compromisso) =>
    [compromisso.clienteNome, compromisso.profissionalNome, compromisso.sala, compromisso.statusRotulo, compromisso.observacoes]
      .filter(Boolean)
      .some((valor) => valor?.toLowerCase().includes(termo))
  );
}

function filtrarServicosBeauty(servicos: ServicoBeautyPro[], termo: string) {
  if (!termo) {
    return servicos;
  }
  return servicos.filter((servico) =>
    [servico.nome, servico.descricao, servico.area]
      .filter(Boolean)
      .some((valor) => valor?.toLowerCase().includes(termo))
  );
}

function filtrarSimulacoesBeauty(simulacoes: SimulacaoBeautyPro[], termo: string, filtroMargem: "TODAS" | "ALERTA") {
  return simulacoes.filter((simulacao) => {
    const passaMargem = filtroMargem === "TODAS" || simulacao.alerta;
    const passaBusca = !termo || [simulacao.nomeProcedimento, simulacao.statusRotulo].some((valor) => valor.toLowerCase().includes(termo));
    return passaMargem && passaBusca;
  });
}
