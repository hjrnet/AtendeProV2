"use client";

import { useEffect, useMemo, useState, type FormEvent } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  AlertTriangle,
  CalendarDays,
  CheckCircle2,
  ChevronRight,
  ClipboardList,
  FileText,
  Gauge,
  LoaderCircle,
  PackageCheck,
  Save,
  Scissors,
  Search,
  Sparkles,
  UserRoundCheck,
  Wrench
} from "lucide-react";

import {
  atualizarFichaEsteticaBeautyPro,
  cadastrarProdutoEstoqueBeauty,
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
  listarProdutosEstoqueBeauty,
  listarProtocolosBeautyPro,
  registrarSessaoProtocoloBeautyPro,
  vincularProdutoBeautyPro,
  type AtalhoBeautyPro,
  type CadastrarProdutoEstoqueBeautyInput,
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
  type ProdutoEstoqueBeautyOperacional,
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

type BeautyProOperacionalViewProps = {
  empresaId: string;
  focoWorkspace?: FocoWorkspaceBeautyPro;
};

type FocoWorkspaceBeautyPro =
  | "beauty-inicio"
  | "beauty-agenda"
  | "beauty-clientes"
  | "beauty-ficha"
  | "beauty-protocolos"
  | "beauty-estoque"
  | "beauty-termos";

type Icone = typeof Scissors;

type AbaFichaBeauty = "resumo" | "anamnese" | "contraindicacoes" | "procedimentos" | "historico" | "alertas";

type AbaProtocolosBeauty = "visao" | "ativos" | "novo" | "sessao" | "evolucao" | "historico";

type AbaSegurancaBeauty = "termos" | "evidencias" | "produtos" | "historico" | "seguranca";

type FiltroEstoqueBeauty = "TODOS" | "VENCIDOS" | "VENCE_7" | "VENCE_30" | "BAIXO";

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

type FormularioEstoqueBeauty = {
  nome: string;
  categoria: string;
  lote: string;
  validade: string;
  fornecedorNome: string;
  fornecedorDocumento: string;
  numeroPedidoCompra: string;
  dataCompra: string;
  statusCompra: string;
  unidade: string;
  quantidadeAtual: string;
  custoUnitario: string;
  estoqueMinimo: string;
};

type FormularioBaixaEstoqueBeauty = {
  clienteId: string;
  produtoEstoqueId: string;
  quantidade: string;
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

const estoqueBeautyVazio: FormularioEstoqueBeauty = {
  nome: "",
  categoria: "Cosméticos Beauty",
  lote: "",
  validade: "",
  fornecedorNome: "",
  fornecedorDocumento: "",
  numeroPedidoCompra: "",
  dataCompra: "",
  statusCompra: "RECEBIDO",
  unidade: "UN",
  quantidadeAtual: "1",
  custoUnitario: "0",
  estoqueMinimo: "1"
};

const baixaEstoqueBeautyVazia: FormularioBaixaEstoqueBeauty = {
  clienteId: "",
  produtoEstoqueId: "",
  quantidade: "1",
  observacoes: "Baixa operacional Beauty registrada pelo workspace de estoque."
};

const kitsInsumosBeauty = [
  {
    procedimento: "Limpeza de pele premium",
    produtos: ["Sérum facial", "Máscara calmante", "Espátula descartável"],
    consumo: "1 un de máscara + 0,25 un de sérum por sessão",
    alerta: "Bom kit inicial para controlar validade e margem de facial."
  },
  {
    procedimento: "Peeling químico leve",
    produtos: ["Ácido mandélico", "Neutralizante", "Protetor pós-procedimento"],
    consumo: "0,20 un de ácido por aplicação",
    alerta: "Exige validade curta, termo assinado e estoque mínimo revisado."
  },
  {
    procedimento: "Design de sobrancelhas",
    produtos: ["Henna", "Algodão", "Palito descartável"],
    consumo: "1 kit descartável por atendimento",
    alerta: "Ajuda a precificar descartáveis que costumam sumir da margem."
  }
];

type SugestaoReposicaoBeauty = {
  produto: ProdutoEstoqueBeautyOperacional;
  motivo: string;
  prioridade: "ALTA" | "MEDIA" | "BAIXA";
  quantidadeSugerida: number;
  custoEstimado: number;
};

type FornecedorBeautyResumo = {
  nome: string;
  documento: string | null;
  lotes: number;
  custoTotal: number;
  menorCusto: number;
  maiorCusto: number;
  pedidos: string[];
};

type MargemProcedimentoBeauty = {
  procedimento: string;
  custoKitEstimado: number;
  precoVenda: number;
  margemSimulada: number | null;
  lucroDepoisKit: number;
  alerta: boolean;
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

const abasFichaBeauty: Array<{ id: AbaFichaBeauty; label: string }> = [
  { id: "resumo", label: "Resumo" },
  { id: "anamnese", label: "Anamnese" },
  { id: "contraindicacoes", label: "Contraindicações" },
  { id: "procedimentos", label: "Procedimentos" },
  { id: "historico", label: "Histórico" },
  { id: "alertas", label: "Alertas" }
];

const abasProtocolosBeauty: Array<{ id: AbaProtocolosBeauty; label: string }> = [
  { id: "visao", label: "Visão geral" },
  { id: "ativos", label: "Protocolos ativos" },
  { id: "novo", label: "Novo protocolo" },
  { id: "sessao", label: "Registrar sessão" },
  { id: "evolucao", label: "Evolução" },
  { id: "historico", label: "Histórico" }
];

const abasSegurancaBeauty: Array<{ id: AbaSegurancaBeauty; label: string }> = [
  { id: "termos", label: "Termos" },
  { id: "evidencias", label: "Evidências" },
  { id: "produtos", label: "Produtos e lotes" },
  { id: "historico", label: "Histórico" },
  { id: "seguranca", label: "Segurança" }
];

export function BeautyProOperacionalView({ empresaId, focoWorkspace = "beauty-inicio" }: BeautyProOperacionalViewProps) {
  const [buscaCliente, setBuscaCliente] = useState("");
  const [clienteSelecionadoId, setClienteSelecionadoId] = useState<string | null>(null);

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

  const integracoesQuery = useQuery({
    queryKey: ["beauty-pro-integracoes", empresaId],
    queryFn: () => consultarIntegracoesOperacionaisBeautyPro(empresaId),
    enabled: Boolean(empresaId && focoWorkspace === "beauty-inicio")
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

  if (focoWorkspace === "beauty-agenda") {
    return <IntegracoesOperacionaisBeautyPainel empresaId={empresaId} />;
  }

  if (focoWorkspace === "beauty-clientes") {
    return (
      <TelaClientesBeautyPro
        buscaCliente={buscaCliente}
        clientes={clientes}
        clienteSelecionadoId={clienteSelecionadoId}
        carregando={clientesQuery.isLoading}
        onBuscar={setBuscaCliente}
        onSelecionar={setClienteSelecionadoId}
      />
    );
  }

  if (focoWorkspace === "beauty-ficha") {
    return <FichaEsteticaBeautyPainel empresaId={empresaId} clienteId={clienteSelecionadoId} />;
  }

  if (focoWorkspace === "beauty-protocolos") {
    return <ProtocolosBeautyPainel empresaId={empresaId} clienteId={clienteSelecionadoId} />;
  }

  if (focoWorkspace === "beauty-estoque") {
    return <EstoqueBeautyPainel empresaId={empresaId} />;
  }

  if (focoWorkspace === "beauty-termos") {
    return <SegurancaOperacionalBeautyPainel empresaId={empresaId} clienteId={clienteSelecionadoId} />;
  }

  return (
    <TelaInicioBeautyPro
      visao={visao}
      indicadoresPrincipais={indicadoresPrincipais}
      indicadoresApoio={indicadoresApoio}
      clientes={clientes}
      clienteSelecionadoId={clienteSelecionadoId}
      agenda={integracoesQuery.data?.agenda ?? []}
      agendaCarregando={integracoesQuery.isLoading}
      onSelecionarCliente={setClienteSelecionadoId}
    />
  );
}

function TelaInicioBeautyPro({
  visao,
  indicadoresPrincipais,
  indicadoresApoio,
  clientes,
  clienteSelecionadoId,
  agenda,
  agendaCarregando,
  onSelecionarCliente
}: {
  visao: {
    empresaNome: string;
    mensagemStatus: string;
    statusOperacional: string;
    statusOperacionalRotulo: string;
    atalhosPrioritarios: AtalhoBeautyPro[];
    proximasEvolucoes: AtalhoBeautyPro[];
  };
  indicadoresPrincipais: IndicadorBeautyPro[];
  indicadoresApoio: IndicadorBeautyPro[];
  clientes: ClienteBeautyResumo[];
  clienteSelecionadoId: string | null;
  agenda: AgendaBeautyPro[];
  agendaCarregando: boolean;
  onSelecionarCliente: (clienteId: string) => void;
}) {
  return (
    <section className="grid min-w-0 gap-4">
      <div className="rounded-lg border border-slate-200 bg-white p-4 shadow-sm">
        <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
          <div className="min-w-0">
            <p className="text-sm font-semibold text-rose-900">Workspace Beauty Pro</p>
            <h4 className="mt-1 text-xl font-semibold text-card-foreground">{visao.empresaNome}</h4>
            <p className="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">{visao.mensagemStatus}</p>
          </div>
          <span className={cn("inline-flex w-fit items-center gap-2 rounded-md border px-3 py-2 text-xs font-semibold", visao.statusOperacional === "OPERACIONAL" ? "border-rose-200 bg-rose-50 text-rose-900" : "border-amber-200 bg-amber-50 text-amber-800")}>
            <Sparkles className="h-4 w-4" />
            {visao.statusOperacionalRotulo}
          </span>
        </div>
      </div>

      <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-4">
        {indicadoresPrincipais.slice(0, 4).map((indicador) => (
          <CardIndicadorBeauty key={indicador.codigo} indicador={indicador} compacto />
        ))}
      </div>

      <div className="grid min-w-0 gap-4 xl:grid-cols-[minmax(0,1fr)_360px]">
        <section className="grid min-w-0 gap-4">
          <div className="rounded-lg border bg-white p-4 shadow-sm">
            <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Ações prioritárias Beauty</p>
                <p className="text-sm leading-6 text-muted-foreground">Atalhos compactos para ficha, protocolos, termos e rastreabilidade.</p>
              </div>
              <span className="w-fit rounded-md border border-rose-200 bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">Menu Beauty</span>
            </div>
            <div className="mt-4 grid gap-3 md:grid-cols-3">
              {visao.atalhosPrioritarios.slice(0, 3).map((atalho) => (
                <CardAtalhoBeauty key={atalho.codigo} atalho={atalho} principal />
              ))}
            </div>
          </div>

          <div className="grid gap-3 lg:grid-cols-2">
            <GrupoBeauty titulo="Indicadores de apoio" itens={indicadoresApoio.slice(0, 6)} />
            <div className="rounded-lg border bg-white p-4 shadow-sm">
              <p className="text-sm font-semibold text-card-foreground">Próximas evoluções</p>
              <div className="mt-3 grid gap-2">
                {visao.proximasEvolucoes.slice(0, 3).map((atalho) => (
                  <CardAtalhoBeauty key={atalho.codigo} atalho={atalho} />
                ))}
              </div>
            </div>
          </div>
        </section>

        <aside className="grid gap-4">
          <section className="rounded-lg border border-rose-100 bg-rose-50/35 p-4 shadow-sm">
            <div className="mb-3 flex items-start justify-between gap-3 border-b border-rose-100 pb-3">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Agenda resumida</p>
                <p className="text-xs text-muted-foreground">Próximos atendimentos Beauty.</p>
              </div>
              <CalendarDays className="h-5 w-5 text-rose-900" />
            </div>
            <ListaAgendaBeautyCompacta agenda={agenda.slice(0, 4)} carregando={agendaCarregando} />
          </section>

          <section className="rounded-lg border bg-white p-4 shadow-sm">
            <div className="mb-3 flex items-start justify-between gap-3 border-b pb-3">
              <div>
                <p className="text-sm font-semibold text-card-foreground">Clientes recentes</p>
                <p className="text-xs text-muted-foreground">Selecione para manter o contexto do cliente.</p>
              </div>
              <UserRoundCheck className="h-5 w-5 text-rose-900" />
            </div>
            <div className="grid gap-2">
              {clientes.slice(0, 4).map((cliente) => (
                <LinhaClienteBeauty key={cliente.id} cliente={cliente} selecionado={cliente.id === clienteSelecionadoId} onSelecionar={() => onSelecionarCliente(cliente.id)} />
              ))}
              {!clientes.length ? <div className="rounded-md border bg-background p-3 text-sm text-muted-foreground">Nenhum cliente Beauty encontrado.</div> : null}
            </div>
          </section>
        </aside>
      </div>
    </section>
  );
}

function TelaClientesBeautyPro({
  buscaCliente,
  clientes,
  clienteSelecionadoId,
  carregando,
  onBuscar,
  onSelecionar
}: {
  buscaCliente: string;
  clientes: ClienteBeautyResumo[];
  clienteSelecionadoId: string | null;
  carregando: boolean;
  onBuscar: (valor: string) => void;
  onSelecionar: (clienteId: string) => void;
}) {
  const clienteSelecionado = clientes.find((cliente) => cliente.id === clienteSelecionadoId) ?? clientes[0] ?? null;

  return (
    <section className="grid min-w-0 gap-4">
      <div className="rounded-lg border bg-white p-4 shadow-sm">
        <div className="flex flex-col gap-3 lg:flex-row lg:items-end lg:justify-between">
          <div>
            <p className="text-sm font-semibold text-rose-900">Clientes Beauty</p>
            <h4 className="mt-1 text-xl font-semibold text-card-foreground">Lista de clientes</h4>
            <p className="mt-2 max-w-2xl text-sm leading-6 text-muted-foreground">Busque, selecione e abra o contexto de atendimento sem carregar a ficha completa automaticamente.</p>
          </div>
          <label className="grid w-full gap-1 text-sm font-medium text-card-foreground lg:max-w-sm">
            Busca
            <span className="relative">
              <Search className="pointer-events-none absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
              <input value={buscaCliente} onChange={(event) => onBuscar(event.target.value)} className="h-10 w-full rounded-md border bg-background pl-9 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" placeholder="Nome, email ou telefone" />
            </span>
          </label>
        </div>
      </div>

      <div className="grid min-w-0 gap-4 xl:grid-cols-[minmax(0,1fr)_360px]">
        <section className="rounded-lg border bg-white p-4 shadow-sm">
          <div className="grid max-h-[640px] gap-2 overflow-y-auto pr-1">
            {carregando ? (
              <div className="flex min-h-32 items-center justify-center rounded-md border bg-background text-sm text-muted-foreground">
                <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
                Carregando clientes
              </div>
            ) : clientes.length ? (
              clientes.map((cliente) => <LinhaClienteBeauty key={cliente.id} cliente={cliente} selecionado={cliente.id === clienteSelecionadoId} onSelecionar={() => onSelecionar(cliente.id)} />)
            ) : (
              <div className="rounded-md border bg-background p-4 text-sm text-muted-foreground">Nenhum cliente Beauty encontrado nesta empresa.</div>
            )}
          </div>
        </section>

        <aside className="rounded-lg border border-rose-100 bg-rose-50/35 p-4 shadow-sm">
          <p className="text-sm font-semibold text-card-foreground">Cliente selecionado</p>
          {clienteSelecionado ? (
            <div className="mt-3 rounded-lg border bg-white p-4">
              <p className="text-base font-semibold text-card-foreground">{clienteSelecionado.nome}</p>
              <p className="mt-1 text-sm text-muted-foreground">{clienteSelecionado.telefone ?? "Sem telefone cadastrado"}</p>
              {clienteSelecionado.observacoes ? <p className="mt-3 text-sm leading-6 text-muted-foreground">{clienteSelecionado.observacoes}</p> : null}
              <div className="mt-4 grid gap-2">
                <span className={cn("w-fit rounded-md border px-2 py-1 text-xs font-semibold", clienteSelecionado.ativo ? "bg-emerald-50 text-emerald-800" : "bg-slate-50 text-slate-700")}>{clienteSelecionado.ativo ? "Ativo" : "Inativo"}</span>
                <p className="text-xs text-muted-foreground">Use o menu lateral para abrir ficha, protocolos ou termos deste cliente.</p>
              </div>
            </div>
          ) : (
            <div className="mt-3 rounded-md border bg-white p-4 text-sm text-muted-foreground">Selecione um cliente na lista.</div>
          )}
        </aside>
      </div>
    </section>
  );
}

function FichaEsteticaBeautyPainel({ empresaId, clienteId }: { empresaId: string; clienteId: string | null }) {
  const queryClient = useQueryClient();
  const [submenuFichaAtivo, setSubmenuFichaAtivo] = useState<AbaFichaBeauty>("resumo");
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
    <section className="grid max-w-full min-w-0 gap-4 overflow-hidden rounded-lg border bg-white p-4 shadow-sm">
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

      <AbasBeauty abas={abasFichaBeauty} abaAtiva={submenuFichaAtivo} onChange={setSubmenuFichaAtivo} ariaLabel="Submenu da ficha estética" />

      {submenuFichaAtivo === "resumo" ? (
        <div className="grid gap-3 lg:grid-cols-3">
          <ResumoFichaBeauty rotulo="Fichas" valor={prontuario.resumo.fichasEsteticas} />
          <ResumoFichaBeauty rotulo="Consultas futuras" valor={prontuario.resumo.consultasFuturas} />
          <ResumoFichaBeauty rotulo="Documentos" valor={prontuario.resumo.documentos} />
          <div className="rounded-lg border bg-background p-4 lg:col-span-3">
            <p className="text-sm font-semibold text-card-foreground">Resumo do cliente</p>
            <p className="mt-2 text-sm leading-6 text-muted-foreground">
              {fichaAtual
                ? `${fichaAtual.objetivoRotulo}: ${fichaAtual.queixaPrincipal}`
                : "Nenhuma ficha estética registrada. Abra Anamnese para criar a primeira avaliação do cliente."}
            </p>
          </div>
        </div>
      ) : null}

      {submenuFichaAtivo === "contraindicacoes" ? (
        <PainelAlertaFichaBeauty ficha={fichaAtual} />
      ) : null}

      {submenuFichaAtivo === "procedimentos" ? (
        <div className="rounded-lg border bg-background p-4">
          <p className="text-sm font-semibold text-card-foreground">Procedimentos recentes</p>
          <p className="mt-2 text-sm leading-6 text-muted-foreground">{fichaAtual?.procedimentosRecentes ?? "Nenhum procedimento recente registrado na ficha atual."}</p>
          {fichaAtual?.observacoes ? <p className="mt-3 rounded-md border bg-white p-3 text-sm leading-6 text-muted-foreground">{fichaAtual.observacoes}</p> : null}
        </div>
      ) : null}

      {submenuFichaAtivo === "alertas" ? (
        <PainelAlertaFichaBeauty ficha={fichaAtual} detalhado />
      ) : null}

      {submenuFichaAtivo === "anamnese" ? (
        <>
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
        </>
      ) : null}

      {submenuFichaAtivo === "historico" ? (
      <div className="rounded-lg border bg-background p-4">
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
      ) : null}
    </section>
  );
}

function ProtocolosBeautyPainel({ empresaId, clienteId }: { empresaId: string; clienteId: string | null }) {
  const queryClient = useQueryClient();
  const [submenuProtocolosAtivo, setSubmenuProtocolosAtivo] = useState<AbaProtocolosBeauty>("visao");
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
    <section className="grid min-w-0 gap-4 rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-rose-900">Protocolos, sessões e evolução</p>
          <p className="text-sm leading-6 text-muted-foreground">Crie pacotes por cliente, registre execução e acompanhe evolução por sessão.</p>
        </div>
        <span className="w-fit rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">Operacional</span>
      </div>

      <AbasBeauty abas={abasProtocolosBeauty} abaAtiva={submenuProtocolosAtivo} onChange={setSubmenuProtocolosAtivo} ariaLabel="Submenu de protocolos Beauty" />

      {submenuProtocolosAtivo === "visao" ? (
        <div className="grid gap-3 lg:grid-cols-3">
          <ResumoFichaBeauty rotulo="Protocolos" valor={protocolos.length} />
          <ResumoFichaBeauty rotulo="Sessões realizadas" valor={protocolos.reduce((total, protocolo) => total + protocolo.sessoesRealizadas, 0)} />
          <ResumoFichaBeauty rotulo="Em andamento" valor={protocolos.filter((protocolo) => protocolo.status === "ATIVO").length} />
          <div className="rounded-lg border bg-background p-4 lg:col-span-3">
            <p className="text-sm font-semibold text-card-foreground">Fluxo recomendado</p>
            <p className="mt-2 text-sm leading-6 text-muted-foreground">Acompanhe protocolos ativos, registre sessões e consulte evolução sem misturar a ficha estética ou os formulários de termo na mesma tela.</p>
          </div>
        </div>
      ) : null}

      {submenuProtocolosAtivo === "ativos" || submenuProtocolosAtivo === "historico" ? (
        <div className="grid gap-2">
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
      ) : null}

      {submenuProtocolosAtivo === "novo" ? (
        <form className="grid gap-3 rounded-lg border bg-background p-4" onSubmit={criarProtocolo}>
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
      ) : null}

      {submenuProtocolosAtivo === "sessao" ? (
        protocoloSelecionado ? (
            <form className="grid gap-3 rounded-lg border bg-background p-4" onSubmit={registrarSessao}>
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
        ) : (
          <EstadoBeautyPro titulo="Selecione um protocolo" descricao="Abra Protocolos ativos e escolha um pacote para registrar a próxima sessão." />
        )
      ) : null}

      {submenuProtocolosAtivo === "evolucao" ? (
        <div className="grid gap-3">
          {protocoloSelecionado ? (
            <>
              <div className="rounded-lg border bg-background p-4">
                <p className="text-sm font-semibold text-card-foreground">{protocoloSelecionado.nome}</p>
                <p className="mt-2 text-sm leading-6 text-muted-foreground">{protocoloSelecionado.objetivo}</p>
              </div>
              {protocoloSelecionado.sessoes.length ? (
                protocoloSelecionado.sessoes.map((sessao) => (
                  <article key={sessao.id} className="rounded-lg border bg-background p-4">
                    <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                      <p className="text-sm font-semibold text-card-foreground">Sessão {sessao.numeroSessao}</p>
                      <span className="w-fit rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{formatarDataHora(sessao.realizadaEm)}</span>
                    </div>
                    <p className="mt-2 text-sm leading-6 text-muted-foreground">{sessao.descricaoExecucao}</p>
                    {sessao.evolucaoCliente ? <p className="mt-2 rounded-md border bg-white p-3 text-sm leading-6 text-muted-foreground">{sessao.evolucaoCliente}</p> : null}
                  </article>
                ))
              ) : (
                <div className="rounded-md border bg-background p-4 text-sm text-muted-foreground">Nenhuma sessão registrada para o protocolo selecionado.</div>
              )}
            </>
          ) : (
            <EstadoBeautyPro titulo="Selecione um protocolo" descricao="Abra Protocolos ativos e escolha um pacote para ver a evolução." />
          )}
        </div>
      ) : null}

      <div className="min-h-5 text-sm">
            {mensagem ? <span className="font-medium text-emerald-700">{mensagem}</span> : null}
            {criarProtocoloMutation.isError || registrarSessaoMutation.isError ? <span className="font-medium text-destructive">Não foi possível salvar protocolo ou sessão.</span> : null}
      </div>
    </section>
  );
}

function EstoqueBeautyPainel({ empresaId }: { empresaId: string }) {
  const queryClient = useQueryClient();
  const [busca, setBusca] = useState("");
  const [filtro, setFiltro] = useState<FiltroEstoqueBeauty>("TODOS");
  const [formulario, setFormulario] = useState<FormularioEstoqueBeauty>(estoqueBeautyVazio);
  const [formularioBaixa, setFormularioBaixa] = useState<FormularioBaixaEstoqueBeauty>(baixaEstoqueBeautyVazia);
  const [mensagem, setMensagem] = useState<string | null>(null);

  const produtosQuery = useQuery({
    queryKey: ["beauty-estoque", empresaId, busca],
    queryFn: () => listarProdutosEstoqueBeauty({ empresaId, busca, tamanho: 100 }),
    enabled: Boolean(empresaId)
  });

  const clientesQuery = useQuery({
    queryKey: ["beauty-pro-clientes", empresaId, "estoque-baixa"],
    queryFn: () => listarClientesBeautyPro({ empresaId }),
    enabled: Boolean(empresaId)
  });

  const integracoesQuery = useQuery({
    queryKey: ["beauty-pro-integracoes", empresaId, "estoque-r21"],
    queryFn: () => consultarIntegracoesOperacionaisBeautyPro(empresaId),
    enabled: Boolean(empresaId)
  });

  const produtosBase = produtosQuery.data?.itens ?? [];
  const clientes = clientesQuery.data?.itens ?? [];
  const simulacoes = integracoesQuery.data?.simulacoes ?? [];
  const produtos = useMemo(() => produtosBase.filter((produto) => produtoPassaFiltroEstoqueBeauty(produto, filtro)), [produtosBase, filtro]);
  const produtoSelecionadoParaBaixa = produtosBase.find((produto) => produto.id === formularioBaixa.produtoEstoqueId) ?? null;
  const produtosVencidos = produtosBase.filter((produto) => diasAteValidadeBeauty(produto.validade) !== null && (diasAteValidadeBeauty(produto.validade) ?? 0) < 0);
  const produtosVence7 = produtosBase.filter((produto) => {
    const dias = diasAteValidadeBeauty(produto.validade);
    return dias !== null && dias >= 0 && dias <= 7;
  });
  const produtosVence30 = produtosBase.filter((produto) => {
    const dias = diasAteValidadeBeauty(produto.validade);
    return dias !== null && dias >= 0 && dias <= 30;
  });
  const produtosEstoqueBaixo = produtosBase.filter((produto) => produto.quantidadeAtual <= produto.estoqueMinimo);
  const valorEmEstoque = produtosBase.reduce((total, produto) => total + produto.quantidadeAtual * produto.custoUnitario, 0);
  const sugestoesReposicao = useMemo(() => calcularSugestoesReposicaoBeauty(produtosBase), [produtosBase]);
  const fornecedoresResumo = useMemo(() => resumirFornecedoresBeauty(produtosBase), [produtosBase]);
  const margemProcedimentos = useMemo(() => calcularMargemProcedimentosBeauty(produtosBase, simulacoes), [produtosBase, simulacoes]);

  const cadastrarProdutoMutation = useMutation({
    mutationFn: (dados: CadastrarProdutoEstoqueBeautyInput) => cadastrarProdutoEstoqueBeauty(dados),
    onSuccess: async () => {
      setMensagem("Produto/lote cadastrado no estoque Beauty.");
      setFormulario(estoqueBeautyVazio);
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["beauty-estoque", empresaId] }),
        queryClient.invalidateQueries({ queryKey: ["beauty-pro-visao", empresaId] })
      ]);
    }
  });

  const registrarBaixaMutation = useMutation({
    mutationFn: () => {
      if (!formularioBaixa.clienteId) {
        throw new Error("Selecione um cliente Beauty.");
      }
      if (!produtoSelecionadoParaBaixa) {
        throw new Error("Selecione um produto do estoque.");
      }
      return vincularProdutoBeautyPro({
        empresaId,
        clienteId: formularioBaixa.clienteId,
        dados: {
          produtoEstoqueId: produtoSelecionadoParaBaixa.id,
          quantidade: numeroFormulario(formularioBaixa.quantidade),
          unidade: produtoSelecionadoParaBaixa.unidade,
          observacoes: textoOuNull(formularioBaixa.observacoes)
        }
      });
    },
    onSuccess: async () => {
      setMensagem("Baixa operacional registrada e vinculada ao histórico Beauty.");
      setFormularioBaixa(baixaEstoqueBeautyVazia);
      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["beauty-estoque", empresaId] }),
        queryClient.invalidateQueries({ queryKey: ["beauty-pro-seguranca", empresaId] }),
        queryClient.invalidateQueries({ queryKey: ["beauty-pro-visao", empresaId] })
      ]);
    }
  });

  function cadastrarProduto(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMensagem(null);
    cadastrarProdutoMutation.mutate({
      empresaId,
      nome: formulario.nome.trim(),
      categoria: textoOuNull(formulario.categoria),
      lote: textoOuNull(formulario.lote),
      validade: textoOuNull(formulario.validade),
      fornecedorNome: textoOuNull(formulario.fornecedorNome),
      fornecedorDocumento: textoOuNull(formulario.fornecedorDocumento),
      numeroPedidoCompra: textoOuNull(formulario.numeroPedidoCompra),
      dataCompra: textoOuNull(formulario.dataCompra),
      statusCompra: textoOuNull(formulario.statusCompra),
      unidade: formulario.unidade.trim().toUpperCase(),
      quantidadeAtual: numeroFormulario(formulario.quantidadeAtual),
      custoUnitario: numeroFormulario(formulario.custoUnitario),
      estoqueMinimo: numeroFormulario(formulario.estoqueMinimo)
    });
  }

  function registrarBaixa(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setMensagem(null);
    registrarBaixaMutation.mutate();
  }

  return (
    <section className="grid min-w-0 gap-4">
      <div className="rounded-lg border border-rose-200 bg-gradient-to-br from-white via-rose-50/40 to-amber-50/45 p-4 shadow-sm">
        <div className="flex flex-col gap-3 lg:flex-row lg:items-start lg:justify-between">
          <div>
            <p className="text-sm font-semibold text-rose-900">Estoque Beauty</p>
            <h4 className="mt-1 text-xl font-semibold text-card-foreground">Produtos, validade e margem operacional</h4>
            <p className="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
              Cadastre produtos e lotes, acompanhe vencimento, ruptura, custo parado e registre baixas por cliente para manter rastreabilidade no pós-procedimento.
            </p>
          </div>
          <span className="w-fit rounded-md border border-rose-200 bg-white px-3 py-2 text-xs font-semibold text-rose-900">R21 compras e margem real</span>
        </div>
      </div>

      <div className="grid gap-3 md:grid-cols-5">
        <ResumoFichaBeauty rotulo="Produtos" valor={produtosBase.length} />
        <ResumoFichaBeauty rotulo="Vencidos" valor={produtosVencidos.length} destaque={produtosVencidos.length > 0} />
        <ResumoFichaBeauty rotulo="Vence 7 dias" valor={produtosVence7.length} destaque={produtosVence7.length > 0} />
        <ResumoFichaBeauty rotulo="Estoque baixo" valor={produtosEstoqueBaixo.length} destaque={produtosEstoqueBaixo.length > 0} />
        <ResumoFichaBeauty rotulo="Custo parado" valor={formatarMoeda(valorEmEstoque)} texto />
      </div>

      <div className="grid gap-4 xl:grid-cols-[minmax(0,1fr)_420px]">
        <section className="rounded-lg border bg-white p-4 shadow-sm">
          <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
            <div>
              <p className="text-sm font-semibold text-card-foreground">Reposição e pedidos de compra</p>
              <p className="mt-1 text-sm leading-6 text-muted-foreground">Sugestões automáticas por estoque mínimo, validade e custo estimado de reposição.</p>
            </div>
            <span className="rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">TASK-R21-001</span>
          </div>
          <div className="mt-4 grid gap-3 lg:grid-cols-2">
            {sugestoesReposicao.length ? (
              sugestoesReposicao.slice(0, 6).map((sugestao) => <CardReposicaoBeauty key={sugestao.produto.id} sugestao={sugestao} />)
            ) : (
              <EstadoBeautyPro titulo="Sem reposição crítica" descricao="Nenhum lote abaixo do mínimo ou próximo de vencer no momento." />
            )}
          </div>
        </section>

        <section className="rounded-lg border bg-white p-4 shadow-sm">
          <div className="flex items-start justify-between gap-3">
            <div>
              <p className="text-sm font-semibold text-card-foreground">Fornecedores e custo por lote</p>
              <p className="mt-1 text-sm leading-6 text-muted-foreground">Histórico derivado dos lotes cadastrados e rastreabilidade de compra.</p>
            </div>
            <span className="rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">TASK-R21-002</span>
          </div>
          <div className="mt-4 grid max-h-80 gap-3 overflow-y-auto pr-1">
            {fornecedoresResumo.length ? (
              fornecedoresResumo.map((fornecedor) => <CardFornecedorBeauty key={fornecedor.nome} fornecedor={fornecedor} />)
            ) : (
              <EstadoBeautyPro titulo="Sem fornecedor registrado" descricao="Preencha fornecedor e pedido ao cadastrar novos lotes Beauty." />
            )}
          </div>
        </section>
      </div>

      <div className="grid gap-4 xl:grid-cols-[420px_minmax(0,1fr)]">
        <section className="rounded-lg border bg-white p-4 shadow-sm">
          <div className="flex items-start justify-between gap-3">
            <div>
              <p className="text-sm font-semibold text-card-foreground">Rotina operacional de validade</p>
              <p className="mt-1 text-sm leading-6 text-muted-foreground">Checklist semanal para bloquear uso, separar descarte e priorizar consumo seguro.</p>
            </div>
            <span className="rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">TASK-R21-003</span>
          </div>
          <div className="mt-4 grid gap-2">
            <ChecklistValidadeBeauty titulo="Bloquear uso" produtos={produtosVencidos} acao="Mover para descarte e impedir vínculo em atendimento." />
            <ChecklistValidadeBeauty titulo="Consumir ou revisar em 7 dias" produtos={produtosVence7} acao="Validar integridade, abrir campanha interna e evitar compra duplicada." />
            <ChecklistValidadeBeauty titulo="Planejar 30 dias" produtos={produtosVence30} acao="Revisar demanda futura, kits e agenda antes de nova compra." />
            <ChecklistValidadeBeauty titulo="Repor mínimo" produtos={produtosEstoqueBaixo} acao="Gerar pedido com fornecedor e quantidade sugerida." />
          </div>
        </section>

        <section className="rounded-lg border bg-white p-4 shadow-sm">
          <div className="flex items-start justify-between gap-3">
            <div>
              <p className="text-sm font-semibold text-card-foreground">Margem real por procedimento Beauty</p>
              <p className="mt-1 text-sm leading-6 text-muted-foreground">Cruza kits sugeridos, custo dos lotes atuais, preço de venda e margem simulada.</p>
            </div>
            <span className="rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">TASK-R21-004</span>
          </div>
          <div className="mt-4 grid gap-3 lg:grid-cols-3">
            {margemProcedimentos.map((item) => <CardMargemProcedimentoBeauty key={item.procedimento} item={item} />)}
          </div>
        </section>
      </div>

      <div className="grid min-w-0 gap-4 xl:grid-cols-[minmax(0,1fr)_390px]">
        <section className="grid min-w-0 gap-4">
          <div className="rounded-lg border bg-white p-4 shadow-sm">
            <div className="grid gap-3 lg:grid-cols-[minmax(0,1fr)_220px]">
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Buscar produto, lote ou categoria
                <span className="relative">
                  <Search className="pointer-events-none absolute left-3 top-3 h-4 w-4 text-muted-foreground" />
                  <input
                    value={busca}
                    onChange={(event) => setBusca(event.target.value)}
                    className="h-10 w-full rounded-md border bg-background pl-9 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
                    placeholder="Ex.: sérum, ácido, DEMO-BEAUTY"
                  />
                </span>
              </label>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Filtro crítico
                <select value={filtro} onChange={(event) => setFiltro(event.target.value as FiltroEstoqueBeauty)} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring">
                  <option value="TODOS">Todos</option>
                  <option value="VENCIDOS">Vencidos</option>
                  <option value="VENCE_7">Vence em 7 dias</option>
                  <option value="VENCE_30">Vence em 30 dias</option>
                  <option value="BAIXO">Estoque baixo</option>
                </select>
              </label>
            </div>

            <div className="mt-4 grid max-h-[620px] gap-3 overflow-y-auto pr-1">
              {produtosQuery.isLoading ? (
                <div className="flex min-h-32 items-center justify-center rounded-lg border bg-background text-sm text-muted-foreground">
                  <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
                  Carregando estoque Beauty
                </div>
              ) : produtos.length ? (
                produtos.map((produto) => <LinhaProdutoEstoqueBeauty key={produto.id} produto={produto} />)
              ) : (
                <EstadoBeautyPro titulo="Nenhum produto encontrado" descricao="Cadastre o primeiro produto Beauty ou ajuste os filtros de busca e validade." />
              )}
            </div>
          </div>

          <div className="rounded-lg border bg-white p-4 shadow-sm">
            <p className="text-sm font-semibold text-card-foreground">Kits de insumos por procedimento</p>
            <p className="mt-1 text-sm leading-6 text-muted-foreground">Sugestões iniciais para estimar custo, consumo e divergências antes da baixa automática.</p>
            <div className="mt-4 grid gap-3 lg:grid-cols-3">
              {kitsInsumosBeauty.map((kit) => (
                <article key={kit.procedimento} className="rounded-lg border bg-background p-4">
                  <p className="text-sm font-semibold text-card-foreground">{kit.procedimento}</p>
                  <p className="mt-2 text-xs leading-5 text-muted-foreground">{kit.consumo}</p>
                  <div className="mt-3 grid gap-1">
                    {kit.produtos.map((produtoKit) => {
                      const disponivel = produtosBase.some((produto) => produto.nome.toLowerCase().includes(produtoKit.toLowerCase().split(" ")[0] ?? produtoKit.toLowerCase()));
                      return (
                        <span key={produtoKit} className={cn("rounded-md border px-2 py-1 text-xs font-semibold", disponivel ? "bg-emerald-50 text-emerald-800" : "bg-amber-50 text-amber-900")}>
                          {produtoKit} {disponivel ? "disponível" : "a cadastrar"}
                        </span>
                      );
                    })}
                  </div>
                  <p className="mt-3 rounded-md border bg-white p-2 text-xs leading-5 text-muted-foreground">{kit.alerta}</p>
                </article>
              ))}
            </div>
          </div>
        </section>

        <aside className="grid gap-4">
          <form className="grid gap-3 rounded-lg border bg-white p-4 shadow-sm" onSubmit={cadastrarProduto}>
            <div>
              <p className="text-sm font-semibold text-card-foreground">Cadastrar produto/lote</p>
              <p className="mt-1 text-xs leading-5 text-muted-foreground">Use para entrada de estoque, lote novo ou reposição Beauty.</p>
            </div>
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Nome
              <input value={formulario.nome} onChange={(event) => setFormulario((atual) => ({ ...atual, nome: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" placeholder="Ex.: Máscara calmante" required />
            </label>
            <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-1">
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Categoria
                <input value={formulario.categoria} onChange={(event) => setFormulario((atual) => ({ ...atual, categoria: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" />
              </label>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Lote
                <input value={formulario.lote} onChange={(event) => setFormulario((atual) => ({ ...atual, lote: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" />
              </label>
            </div>
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Validade
              <input type="date" value={formulario.validade} onChange={(event) => setFormulario((atual) => ({ ...atual, validade: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" />
            </label>
            <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-1">
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Fornecedor
                <input value={formulario.fornecedorNome} onChange={(event) => setFormulario((atual) => ({ ...atual, fornecedorNome: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" placeholder="Ex.: Dermocosméticos Brasil" />
              </label>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Documento fornecedor
                <input value={formulario.fornecedorDocumento} onChange={(event) => setFormulario((atual) => ({ ...atual, fornecedorDocumento: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" placeholder="CNPJ ou contato" />
              </label>
            </div>
            <div className="grid gap-3 sm:grid-cols-3 xl:grid-cols-1">
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Pedido compra
                <input value={formulario.numeroPedidoCompra} onChange={(event) => setFormulario((atual) => ({ ...atual, numeroPedidoCompra: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" placeholder="PC-2026-001" />
              </label>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Data compra
                <input type="date" value={formulario.dataCompra} onChange={(event) => setFormulario((atual) => ({ ...atual, dataCompra: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" />
              </label>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Status compra
                <select value={formulario.statusCompra} onChange={(event) => setFormulario((atual) => ({ ...atual, statusCompra: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring">
                  <option value="RASCUNHO">Rascunho</option>
                  <option value="ENVIADO">Enviado</option>
                  <option value="RECEBIDO_PARCIAL">Recebido parcial</option>
                  <option value="RECEBIDO">Recebido</option>
                  <option value="CANCELADO">Cancelado</option>
                </select>
              </label>
            </div>
            <div className="grid gap-3 sm:grid-cols-3 xl:grid-cols-1">
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Quantidade
                <input type="number" min="0" step="0.001" value={formulario.quantidadeAtual} onChange={(event) => setFormulario((atual) => ({ ...atual, quantidadeAtual: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" required />
              </label>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Unidade
                <input value={formulario.unidade} onChange={(event) => setFormulario((atual) => ({ ...atual, unidade: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm uppercase outline-none focus:border-primary focus:ring-2 focus:ring-ring" required />
              </label>
              <label className="grid gap-1 text-sm font-medium text-card-foreground">
                Mínimo
                <input type="number" min="0" step="0.001" value={formulario.estoqueMinimo} onChange={(event) => setFormulario((atual) => ({ ...atual, estoqueMinimo: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" required />
              </label>
            </div>
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Custo unitário
              <input type="number" min="0" step="0.01" value={formulario.custoUnitario} onChange={(event) => setFormulario((atual) => ({ ...atual, custoUnitario: event.target.value }))} className="h-10 rounded-md border bg-background px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" required />
            </label>
            <button type="submit" disabled={cadastrarProdutoMutation.isPending} className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-rose-900 px-4 text-sm font-semibold text-white transition hover:bg-rose-950 disabled:opacity-70">
              {cadastrarProdutoMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <Save className="h-4 w-4" />}
              Salvar produto
            </button>
          </form>

          <form className="grid gap-3 rounded-lg border border-rose-100 bg-rose-50/35 p-4 shadow-sm" onSubmit={registrarBaixa}>
            <div>
              <p className="text-sm font-semibold text-card-foreground">Baixa operacional</p>
              <p className="mt-1 text-xs leading-5 text-muted-foreground">Registra saída manual e vincula o produto ao histórico do cliente Beauty.</p>
            </div>
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Cliente
              <select value={formularioBaixa.clienteId} onChange={(event) => setFormularioBaixa((atual) => ({ ...atual, clienteId: event.target.value }))} className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" required>
                <option value="">Selecione</option>
                {clientes.map((cliente) => (
                  <option key={cliente.id} value={cliente.id}>
                    {cliente.nome}
                  </option>
                ))}
              </select>
            </label>
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Produto
              <select value={formularioBaixa.produtoEstoqueId} onChange={(event) => setFormularioBaixa((atual) => ({ ...atual, produtoEstoqueId: event.target.value }))} className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" required>
                <option value="">Selecione</option>
                {produtosBase.map((produto) => (
                  <option key={produto.id} value={produto.id}>
                    {produto.nome} {produto.lote ? `- ${produto.lote}` : ""}
                  </option>
                ))}
              </select>
            </label>
            <label className="grid gap-1 text-sm font-medium text-card-foreground">
              Quantidade
              <input type="number" min="0.001" step="0.001" value={formularioBaixa.quantidade} onChange={(event) => setFormularioBaixa((atual) => ({ ...atual, quantidade: event.target.value }))} className="h-10 rounded-md border bg-white px-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring" required />
            </label>
            <CampoTextoBeauty label="Observação da baixa" value={formularioBaixa.observacoes} onChange={(value) => setFormularioBaixa((atual) => ({ ...atual, observacoes: value }))} placeholder="Ex.: sessão facial, ajuste de cabine, consumo de kit." />
            {produtoSelecionadoParaBaixa ? (
              <div className="rounded-md border bg-white p-3 text-xs leading-5 text-muted-foreground">
                Saldo atual: <strong>{formatarQuantidade(produtoSelecionadoParaBaixa.quantidadeAtual)} {produtoSelecionadoParaBaixa.unidade}</strong>. Estoque mínimo: {formatarQuantidade(produtoSelecionadoParaBaixa.estoqueMinimo)} {produtoSelecionadoParaBaixa.unidade}.
              </div>
            ) : null}
            <button type="submit" disabled={registrarBaixaMutation.isPending} className="inline-flex h-10 items-center justify-center gap-2 rounded-md bg-rose-900 px-4 text-sm font-semibold text-white transition hover:bg-rose-950 disabled:opacity-70">
              {registrarBaixaMutation.isPending ? <LoaderCircle className="h-4 w-4 animate-spin" /> : <PackageCheck className="h-4 w-4" />}
              Registrar baixa
            </button>
          </form>

          <div className="min-h-5 text-sm">
            {mensagem ? <span className="font-medium text-emerald-700">{mensagem}</span> : null}
            {produtosQuery.isError || cadastrarProdutoMutation.isError || registrarBaixaMutation.isError ? (
              <span className="font-medium text-destructive">Não foi possível concluir a operação de estoque.</span>
            ) : null}
          </div>
        </aside>
      </div>
    </section>
  );
}

function SegurancaOperacionalBeautyPainel({ empresaId, clienteId }: { empresaId: string; clienteId: string | null }) {
  const queryClient = useQueryClient();
  const [submenuSegurancaAtivo, setSubmenuSegurancaAtivo] = useState<AbaSegurancaBeauty>("termos");
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
    <section className="grid min-w-0 gap-4 rounded-lg border bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-2 border-b pb-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-rose-900">Termos, evidências e produtos</p>
          <p className="text-sm leading-6 text-muted-foreground">Registre consentimentos, evolução visual segura e produtos/lotes vinculados ao atendimento.</p>
        </div>
        <span className="w-fit rounded-md border bg-rose-50 px-2 py-1 text-xs font-semibold text-rose-900">Segurança operacional</span>
      </div>

      <AbasBeauty abas={abasSegurancaBeauty} abaAtiva={submenuSegurancaAtivo} onChange={setSubmenuSegurancaAtivo} ariaLabel="Submenu de termos e produtos Beauty" />

      {segurancaQuery.isLoading ? (
        <div className="flex min-h-32 items-center justify-center rounded-lg border bg-background text-sm text-muted-foreground">
          <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
          Carregando segurança operacional
        </div>
      ) : (
        <div className="grid gap-4">
          <div className="grid gap-3 md:grid-cols-3">
            <ResumoFichaBeauty rotulo="Termos" valor={seguranca?.termos.length ?? 0} />
            <ResumoFichaBeauty rotulo="Evidências" valor={seguranca?.evidencias.length ?? 0} />
            <ResumoFichaBeauty rotulo="Produtos" valor={seguranca?.produtosUtilizados.length ?? 0} destaque={(seguranca?.produtosUtilizados ?? []).some((produto) => produto.alertaValidade || produto.alertaEstoqueBaixo)} />
          </div>

          <div className="grid gap-4">
            {submenuSegurancaAtivo === "termos" ? (
            <form className="grid gap-3 rounded-lg border bg-background p-4" onSubmit={criarTermo}>
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
            ) : null}

            {submenuSegurancaAtivo === "evidencias" ? (
            <form className="grid gap-3 rounded-lg border bg-background p-4" onSubmit={criarEvidencia}>
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
            ) : null}

            {submenuSegurancaAtivo === "produtos" ? (
            <form className="grid gap-3 rounded-lg border bg-background p-4" onSubmit={vincularProduto}>
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
            ) : null}
          </div>

          {submenuSegurancaAtivo === "historico" ? (
          <div className="grid gap-3 lg:grid-cols-3">
            <HistoricoTermosBeauty termos={seguranca?.termos ?? []} />
            <HistoricoEvidenciasBeauty evidencias={seguranca?.evidencias ?? []} />
            <HistoricoProdutosBeauty produtos={seguranca?.produtosUtilizados ?? []} estoque={seguranca?.produtosEstoque ?? []} />
          </div>
          ) : null}

          {submenuSegurancaAtivo === "seguranca" ? (
            <div className="rounded-lg border bg-background p-4">
              <p className="text-sm font-semibold text-card-foreground">Segurança operacional</p>
              <p className="mt-2 text-sm leading-6 text-muted-foreground">Use termos, evidências seguras e rastreabilidade de produtos para manter histórico claro sem armazenar imagem real sem consentimento formal.</p>
              {(seguranca?.produtosEstoque ?? []).some((produto) => produto.estoqueBaixo || produto.validadeEmAlerta) ? (
                <div className="mt-3 rounded-md border border-amber-300 bg-amber-50 p-3 text-sm leading-6 text-amber-900">Existem produtos do estoque com validade próxima ou estoque baixo. Revise antes de vincular ao atendimento.</div>
              ) : (
                <div className="mt-3 rounded-md border border-emerald-200 bg-emerald-50 p-3 text-sm leading-6 text-emerald-900">Nenhum alerta crítico de produto no momento.</div>
              )}
            </div>
          ) : null}

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

function AbasBeauty<T extends string>({
  abas,
  abaAtiva,
  onChange,
  ariaLabel
}: {
  abas: Array<{ id: T; label: string }>;
  abaAtiva: T;
  onChange: (aba: T) => void;
  ariaLabel: string;
}) {
  return (
    <nav className="w-full max-w-full min-w-0 overflow-x-auto pb-1" aria-label={ariaLabel}>
      <div className="flex min-w-max gap-2">
        {abas.map((aba) => {
          const ativo = aba.id === abaAtiva;
          return (
            <button
              key={aba.id}
              type="button"
              onClick={() => onChange(aba.id)}
              className={cn(
                "inline-flex min-h-10 shrink-0 items-center rounded-md border px-3 text-sm font-semibold transition focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring",
                ativo ? "border-rose-700 bg-rose-900 text-white shadow-sm" : "bg-background text-muted-foreground hover:border-rose-200 hover:bg-rose-50 hover:text-rose-900"
              )}
            >
              {aba.label}
            </button>
          );
        })}
      </div>
    </nav>
  );
}

function PainelAlertaFichaBeauty({ ficha, detalhado = false }: { ficha: FichaEsteticaBeautyPro | null; detalhado?: boolean }) {
  if (!ficha) {
    return (
      <div className="rounded-lg border bg-background p-4">
        <p className="text-sm font-semibold text-card-foreground">Sem ficha estética</p>
        <p className="mt-2 text-sm leading-6 text-muted-foreground">Crie a anamnese para registrar contraindicações, histórico estético e cuidados obrigatórios.</p>
      </div>
    );
  }

  if (!ficha.possuiAlertaContraindicacao) {
    return (
      <div className="rounded-lg border border-emerald-200 bg-emerald-50 p-4 text-sm text-emerald-900">
        <div className="flex items-start gap-2">
          <CheckCircle2 className="mt-0.5 h-4 w-4 shrink-0" />
          <div>
            <p className="font-semibold">Sem contraindicações registradas</p>
            <p className="mt-1 leading-6">A ficha atual não possui alerta crítico. Mantenha a avaliação profissional antes de cada procedimento.</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="rounded-lg border border-amber-300 bg-amber-50 p-4 text-sm text-amber-900">
      <div className="flex items-start gap-2">
        <AlertTriangle className="mt-0.5 h-4 w-4 shrink-0" />
        <div>
          <p className="font-semibold">Contraindicações e alertas registrados</p>
          <p className="mt-1 leading-6">{ficha.alertaContraindicacoes}</p>
          {detalhado ? (
            <div className="mt-3 grid gap-2 sm:grid-cols-2">
              <ResumoFichaBeauty rotulo="Gestante" valor={ficha.gestante ? "Sim" : "Não"} texto destaque={ficha.gestante} />
              <ResumoFichaBeauty rotulo="Lactante" valor={ficha.lactante ? "Sim" : "Não"} texto destaque={ficha.lactante} />
              <ResumoFichaBeauty rotulo="Pele sensível" valor={ficha.sensibilidadePele ? "Sim" : "Não"} texto destaque={ficha.sensibilidadePele} />
              <ResumoFichaBeauty rotulo="Usa ácidos" valor={ficha.usaAcidos ? "Sim" : "Não"} texto destaque={ficha.usaAcidos} />
            </div>
          ) : null}
        </div>
      </div>
    </div>
  );
}

function ListaAgendaBeautyCompacta({ agenda, carregando }: { agenda: AgendaBeautyPro[]; carregando: boolean }) {
  if (carregando) {
    return (
      <div className="flex min-h-24 items-center justify-center rounded-md border bg-white text-sm text-muted-foreground">
        <LoaderCircle className="mr-2 h-4 w-4 animate-spin" />
        Carregando agenda
      </div>
    );
  }

  if (!agenda.length) {
    return <div className="rounded-md border bg-white p-3 text-sm text-muted-foreground">Nenhum atendimento Beauty nos próximos dias.</div>;
  }

  return (
    <div className="grid gap-2">
      {agenda.map((compromisso) => (
        <article key={compromisso.id} className="rounded-md border bg-white p-3">
          <div className="flex items-start justify-between gap-2">
            <div className="min-w-0">
              <p className="truncate text-sm font-semibold text-card-foreground">{compromisso.clienteNome ?? "Cliente não informado"}</p>
              <p className="mt-1 text-xs text-muted-foreground">{formatarDataHora(compromisso.inicio)}</p>
            </div>
            <span className={cn("shrink-0 rounded-md border px-2 py-1 text-xs font-semibold", classeStatusAgenda(compromisso.status))}>{compromisso.statusRotulo}</span>
          </div>
        </article>
      ))}
    </div>
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

function LinhaProdutoEstoqueBeauty({ produto }: { produto: ProdutoEstoqueBeautyOperacional }) {
  const status = statusEstoqueBeauty(produto);
  const dias = diasAteValidadeBeauty(produto.validade);

  return (
    <article className={cn("rounded-lg border bg-background p-4", classeStatusEstoqueBeauty(status))}>
      <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-card-foreground">{produto.nome}</p>
          <p className="mt-1 text-xs leading-5 text-muted-foreground">
            {produto.categoria ?? "Sem categoria"} {produto.lote ? `• lote ${produto.lote}` : ""} {produto.validade ? `• validade ${formatarDataCurta(produto.validade)}` : ""}
          </p>
        </div>
        <span className={cn("w-fit shrink-0 rounded-md border px-2 py-1 text-xs font-semibold", classeStatusEstoqueBeauty(status, true))}>{status}</span>
      </div>
      <div className="mt-3 grid gap-2 sm:grid-cols-4">
        <ResumoFichaBeauty rotulo="Saldo" valor={`${formatarQuantidade(produto.quantidadeAtual)} ${produto.unidade}`} texto destaque={produto.quantidadeAtual <= produto.estoqueMinimo} />
        <ResumoFichaBeauty rotulo="Mínimo" valor={`${formatarQuantidade(produto.estoqueMinimo)} ${produto.unidade}`} texto />
        <ResumoFichaBeauty rotulo="Custo un." valor={formatarMoeda(produto.custoUnitario)} texto />
        <ResumoFichaBeauty rotulo="Custo lote" valor={formatarMoeda(produto.quantidadeAtual * produto.custoUnitario)} texto />
      </div>
      {dias !== null ? (
        <p className="mt-3 text-xs font-medium text-muted-foreground">
          {dias < 0 ? `Vencido há ${Math.abs(dias)} dia(s).` : dias === 0 ? "Vence hoje." : `Vence em ${dias} dia(s).`}
        </p>
      ) : (
        <p className="mt-3 text-xs font-medium text-muted-foreground">Sem validade cadastrada. Revise se o produto exigir controle sanitário.</p>
      )}
      <div className="mt-3 grid gap-2 rounded-md border bg-white p-3 text-xs leading-5 text-muted-foreground sm:grid-cols-2">
        <span>Fornecedor: <strong>{produto.fornecedorNome ?? "não informado"}</strong></span>
        <span>Pedido: <strong>{produto.numeroPedidoCompra ?? "sem pedido"}</strong></span>
        <span>Compra: <strong>{produto.dataCompra ? formatarDataCurta(produto.dataCompra) : "sem data"}</strong></span>
        <span>Status: <strong>{rotuloStatusCompraBeauty(produto.statusCompra)}</strong></span>
      </div>
    </article>
  );
}

function CardReposicaoBeauty({ sugestao }: { sugestao: SugestaoReposicaoBeauty }) {
  return (
    <article className="rounded-lg border bg-background p-4">
      <div className="flex items-start justify-between gap-3">
        <div className="min-w-0">
          <p className="truncate text-sm font-semibold text-card-foreground">{sugestao.produto.nome}</p>
          <p className="mt-1 text-xs leading-5 text-muted-foreground">
            {sugestao.produto.fornecedorNome ?? "Fornecedor não informado"} {sugestao.produto.lote ? `• lote ${sugestao.produto.lote}` : ""}
          </p>
        </div>
        <span className={cn("rounded-md border px-2 py-1 text-xs font-semibold", classePrioridadeReposicaoBeauty(sugestao.prioridade))}>{sugestao.prioridade}</span>
      </div>
      <p className="mt-3 text-xs leading-5 text-muted-foreground">{sugestao.motivo}</p>
      <div className="mt-3 grid gap-2 sm:grid-cols-3">
        <ResumoFichaBeauty rotulo="Sugerido" valor={`${formatarQuantidade(sugestao.quantidadeSugerida)} ${sugestao.produto.unidade}`} texto />
        <ResumoFichaBeauty rotulo="Custo" valor={formatarMoeda(sugestao.custoEstimado)} texto />
        <ResumoFichaBeauty rotulo="Pedido" valor={rotuloStatusCompraBeauty(sugestao.produto.statusCompra)} texto />
      </div>
    </article>
  );
}

function CardFornecedorBeauty({ fornecedor }: { fornecedor: FornecedorBeautyResumo }) {
  return (
    <article className="rounded-lg border bg-background p-4">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-semibold text-card-foreground">{fornecedor.nome}</p>
          <p className="mt-1 text-xs text-muted-foreground">{fornecedor.documento ?? "Documento não informado"}</p>
        </div>
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{fornecedor.lotes} lote(s)</span>
      </div>
      <div className="mt-3 grid gap-2 sm:grid-cols-3">
        <ResumoFichaBeauty rotulo="Total comprado" valor={formatarMoeda(fornecedor.custoTotal)} texto />
        <ResumoFichaBeauty rotulo="Menor custo" valor={formatarMoeda(fornecedor.menorCusto)} texto />
        <ResumoFichaBeauty rotulo="Maior custo" valor={formatarMoeda(fornecedor.maiorCusto)} texto />
      </div>
      <p className="mt-3 text-xs leading-5 text-muted-foreground">
        Pedidos: {fornecedor.pedidos.length ? fornecedor.pedidos.join(", ") : "sem rastreio de pedido"}
      </p>
    </article>
  );
}

function ChecklistValidadeBeauty({ titulo, produtos, acao }: { titulo: string; produtos: ProdutoEstoqueBeautyOperacional[]; acao: string }) {
  return (
    <article className="rounded-md border bg-background p-3">
      <div className="flex items-start justify-between gap-3">
        <div>
          <p className="text-sm font-semibold text-card-foreground">{titulo}</p>
          <p className="mt-1 text-xs leading-5 text-muted-foreground">{acao}</p>
        </div>
        <span className={cn("rounded-md border px-2 py-1 text-xs font-semibold", produtos.length ? "bg-amber-50 text-amber-900" : "bg-emerald-50 text-emerald-800")}>{produtos.length}</span>
      </div>
      {produtos.length ? (
        <div className="mt-2 flex flex-wrap gap-2">
          {produtos.slice(0, 4).map((produto) => (
            <span key={produto.id} className="rounded-full border bg-white px-2 py-1 text-[11px] font-semibold text-muted-foreground">
              {produto.nome}
            </span>
          ))}
        </div>
      ) : null}
    </article>
  );
}

function CardMargemProcedimentoBeauty({ item }: { item: MargemProcedimentoBeauty }) {
  return (
    <article className={cn("rounded-lg border p-4", item.alerta ? "border-amber-300 bg-amber-50/70" : "bg-background")}>
      <div className="flex items-start justify-between gap-3">
        <p className="text-sm font-semibold text-card-foreground">{item.procedimento}</p>
        <span className={cn("rounded-md border px-2 py-1 text-xs font-semibold", item.alerta ? "bg-amber-100 text-amber-900" : "bg-emerald-50 text-emerald-800")}>
          {item.alerta ? "Revisar" : "Saudável"}
        </span>
      </div>
      <div className="mt-3 grid gap-2">
        <ResumoFichaBeauty rotulo="Custo kit" valor={formatarMoeda(item.custoKitEstimado)} texto />
        <ResumoFichaBeauty rotulo="Preço venda" valor={formatarMoeda(item.precoVenda)} texto />
        <ResumoFichaBeauty rotulo="Lucro após kit" valor={formatarMoeda(item.lucroDepoisKit)} texto destaque={item.lucroDepoisKit < 0} />
        <ResumoFichaBeauty rotulo="Margem simulada" valor={item.margemSimulada === null ? "sem simulação" : `${formatarNumero(item.margemSimulada)}%`} texto destaque={item.alerta} />
      </div>
    </article>
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

function CardIndicadorBeauty({ indicador, compacto = false }: { indicador: IndicadorBeautyPro; compacto?: boolean }) {
  const Icon = iconesIndicadores[indicador.codigo] ?? Sparkles;

  return (
    <article className={cn("rounded-lg border p-4 shadow-sm", classeStatusIndicador(indicador.status))}>
      <div className="flex items-start justify-between gap-3">
        <span className="flex h-10 w-10 items-center justify-center rounded-md bg-white text-rose-900">
          <Icon className="h-5 w-5" />
        </span>
        <span className="rounded-md border bg-white px-2 py-1 text-xs font-semibold text-muted-foreground">{rotuloStatus(indicador.status)}</span>
      </div>
      <p className={cn("text-sm font-medium text-muted-foreground", compacto ? "mt-3" : "mt-4")}>{indicador.titulo}</p>
      <p className="mt-1 text-2xl font-semibold text-card-foreground">{formatarNumero(indicador.valor)}</p>
      <p className={cn("mt-2 text-xs leading-5 text-muted-foreground", compacto ? "line-clamp-2" : "")}>{indicador.descricao}</p>
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
        <span className="inline-flex items-center gap-1 rounded-md border bg-white px-2 py-1 text-xs font-semibold text-rose-900">
          {rotuloStatusAtalho(atalho.status)}
          <ChevronRight className="h-3 w-3" />
        </span>
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

function calcularSugestoesReposicaoBeauty(produtos: ProdutoEstoqueBeautyOperacional[]): SugestaoReposicaoBeauty[] {
  return produtos
    .map((produto) => {
      const dias = diasAteValidadeBeauty(produto.validade);
      const estoqueBaixo = produto.quantidadeAtual <= produto.estoqueMinimo;
      const vencido = dias !== null && dias < 0;
      const venceEm30 = dias !== null && dias >= 0 && dias <= 30;

      if (!estoqueBaixo && !vencido && !venceEm30) {
        return null;
      }

      const quantidadeSugerida = Math.max(produto.estoqueMinimo * 2 - produto.quantidadeAtual, produto.estoqueMinimo, 1);
      const prioridade: SugestaoReposicaoBeauty["prioridade"] = vencido || estoqueBaixo ? "ALTA" : dias !== null && dias <= 7 ? "MEDIA" : "BAIXA";
      const motivo = vencido
        ? "Produto vencido: bloquear uso e repor se ainda fizer parte dos protocolos."
        : estoqueBaixo
          ? "Saldo abaixo do mínimo: gerar pedido para evitar ruptura em agenda Beauty."
          : "Validade próxima: planejar consumo, promoção interna ou reposição com lote novo.";

      return {
        produto,
        motivo,
        prioridade,
        quantidadeSugerida,
        custoEstimado: quantidadeSugerida * produto.custoUnitario
      };
    })
    .filter((item): item is SugestaoReposicaoBeauty => item !== null)
    .sort((a, b) => prioridadeOrdemBeauty(a.prioridade) - prioridadeOrdemBeauty(b.prioridade));
}

function resumirFornecedoresBeauty(produtos: ProdutoEstoqueBeautyOperacional[]): FornecedorBeautyResumo[] {
  const mapa = new Map<string, FornecedorBeautyResumo>();

  produtos.forEach((produto) => {
    const nome = produto.fornecedorNome?.trim() || "Fornecedor não informado";
    const atual = mapa.get(nome) ?? {
      nome,
      documento: produto.fornecedorDocumento,
      lotes: 0,
      custoTotal: 0,
      menorCusto: produto.custoUnitario,
      maiorCusto: produto.custoUnitario,
      pedidos: []
    };

    atual.lotes += 1;
    atual.custoTotal += produto.quantidadeAtual * produto.custoUnitario;
    atual.menorCusto = Math.min(atual.menorCusto, produto.custoUnitario);
    atual.maiorCusto = Math.max(atual.maiorCusto, produto.custoUnitario);
    if (produto.numeroPedidoCompra && !atual.pedidos.includes(produto.numeroPedidoCompra)) {
      atual.pedidos.push(produto.numeroPedidoCompra);
    }
    mapa.set(nome, atual);
  });

  return Array.from(mapa.values()).sort((a, b) => b.custoTotal - a.custoTotal);
}

function calcularMargemProcedimentosBeauty(produtos: ProdutoEstoqueBeautyOperacional[], simulacoes: SimulacaoBeautyPro[]): MargemProcedimentoBeauty[] {
  return kitsInsumosBeauty.map((kit) => {
    const custoKitEstimado = kit.produtos.reduce((total, produtoKit) => total + estimarCustoProdutoKitBeauty(produtoKit, produtos), 0);
    const simulacao = simulacoes.find((item) => item.nomeProcedimento.toLowerCase().includes(kit.procedimento.toLowerCase().split(" ")[0] ?? kit.procedimento.toLowerCase()));
    const precoVenda = simulacao?.precoVenda ?? custoKitEstimado * 3;
    const lucroDepoisKit = precoVenda - custoKitEstimado;
    const margemCalculada = precoVenda > 0 ? (lucroDepoisKit / precoVenda) * 100 : 0;
    const margemSimulada = simulacao?.margemRealPercentual ?? null;

    return {
      procedimento: kit.procedimento,
      custoKitEstimado,
      precoVenda,
      margemSimulada,
      lucroDepoisKit,
      alerta: lucroDepoisKit <= 0 || margemCalculada < 25 || (margemSimulada !== null && margemSimulada < 25)
    };
  });
}

function estimarCustoProdutoKitBeauty(nomeProduto: string, produtos: ProdutoEstoqueBeautyOperacional[]) {
  const termoPrincipal = nomeProduto.toLowerCase().split(" ")[0] ?? nomeProduto.toLowerCase();
  const produto = produtos.find((item) => item.nome.toLowerCase().includes(termoPrincipal));
  if (!produto) {
    return 0;
  }
  return produto.custoUnitario;
}

function prioridadeOrdemBeauty(prioridade: SugestaoReposicaoBeauty["prioridade"]) {
  if (prioridade === "ALTA") {
    return 1;
  }
  if (prioridade === "MEDIA") {
    return 2;
  }
  return 3;
}

function classePrioridadeReposicaoBeauty(prioridade: SugestaoReposicaoBeauty["prioridade"]) {
  if (prioridade === "ALTA") {
    return "border-red-200 bg-red-50 text-red-800";
  }
  if (prioridade === "MEDIA") {
    return "border-amber-200 bg-amber-50 text-amber-900";
  }
  return "border-sky-200 bg-sky-50 text-sky-800";
}

function rotuloStatusCompraBeauty(status: string | null) {
  const rotulos: Record<string, string> = {
    RASCUNHO: "Rascunho",
    ENVIADO: "Enviado",
    RECEBIDO_PARCIAL: "Recebido parcial",
    RECEBIDO: "Recebido",
    CANCELADO: "Cancelado"
  };
  return status ? rotulos[status] ?? status : "Não informado";
}

function numeroFormulario(valor: string) {
  return Number(valor.replace(",", "."));
}

function formatarQuantidade(valor: number) {
  return new Intl.NumberFormat("pt-BR", {
    maximumFractionDigits: 3
  }).format(valor);
}

function diasAteValidadeBeauty(validade: string | null) {
  if (!validade) {
    return null;
  }
  const hoje = new Date();
  hoje.setHours(0, 0, 0, 0);
  const dataValidade = new Date(`${validade}T00:00:00`);
  return Math.ceil((dataValidade.getTime() - hoje.getTime()) / 86_400_000);
}

function produtoPassaFiltroEstoqueBeauty(produto: ProdutoEstoqueBeautyOperacional, filtro: FiltroEstoqueBeauty) {
  const dias = diasAteValidadeBeauty(produto.validade);
  if (filtro === "VENCIDOS") {
    return dias !== null && dias < 0;
  }
  if (filtro === "VENCE_7") {
    return dias !== null && dias >= 0 && dias <= 7;
  }
  if (filtro === "VENCE_30") {
    return dias !== null && dias >= 0 && dias <= 30;
  }
  if (filtro === "BAIXO") {
    return produto.quantidadeAtual <= produto.estoqueMinimo;
  }
  return true;
}

function statusEstoqueBeauty(produto: ProdutoEstoqueBeautyOperacional) {
  const dias = diasAteValidadeBeauty(produto.validade);
  if (dias !== null && dias < 0) {
    return "Vencido";
  }
  if (produto.quantidadeAtual <= produto.estoqueMinimo) {
    return "Estoque baixo";
  }
  if (dias !== null && dias <= 7) {
    return "Vence em 7 dias";
  }
  if (dias !== null && dias <= 30) {
    return "Vence em 30 dias";
  }
  return "Saudável";
}

function classeStatusEstoqueBeauty(status: string, badge = false) {
  const classes: Record<string, string> = {
    Vencido: badge ? "border-red-200 bg-red-50 text-red-800" : "border-red-200 bg-red-50/60",
    "Estoque baixo": badge ? "border-amber-200 bg-amber-50 text-amber-900" : "border-amber-200 bg-amber-50/60",
    "Vence em 7 dias": badge ? "border-orange-200 bg-orange-50 text-orange-900" : "border-orange-200 bg-orange-50/50",
    "Vence em 30 dias": badge ? "border-sky-200 bg-sky-50 text-sky-800" : "border-sky-200 bg-sky-50/40",
    Saudável: badge ? "border-emerald-200 bg-emerald-50 text-emerald-800" : "border-emerald-100 bg-white"
  };
  return classes[status] ?? (badge ? "border bg-white text-muted-foreground" : "border bg-background");
}

function classeStatusIndicador(status: string) {
  if (status === "ALERTA") {
    return "border-amber-300 bg-amber-50";
  }
  if (status === "SAUDAVEL") {
    return "border-emerald-200 bg-emerald-50";
  }
  if (status === "OPERACIONAL") {
    return "border-slate-200 bg-white";
  }
  if (status === "PREPARADO") {
    return "border-sky-200 bg-sky-50";
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
