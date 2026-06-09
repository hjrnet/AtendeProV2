"use client";

import { useEffect, useMemo, useState, type FormEvent } from "react";
import { Inbox, LoaderCircle, Search, UserRound, type LucideIcon } from "lucide-react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  listarAgendaPortal,
  listarClientesPortal,
  listarDocumentosPortal,
  type ClientePortal,
  type CompromissoPortal,
  type DocumentoPortal,
  type TipoStatusAgenda,
  type TipoDocumentoPortal
} from "@/features/portal-cliente/api/portal-cliente-client";
import {
  consultarListaComprasNutriPro,
  consultarPlanoPublicadoNutriPro,
  criarMetaNutriPro,
  criarRegistroDiarioNutriPro,
  enviarMensagemNutriPro,
  listarDiarioAlimentarNutriPro,
  listarEvolucaoNutriPro,
  listarLembretesNutriPro,
  listarMensagensNutriPro,
  listarMetasNutriPro,
  type GrupoListaComprasNutriPro,
  type LembreteNutriPro,
  type MensagemNutriPro,
  type MetaNutriPro,
  type PlanoAlimentarNutriPro,
  type RegistroDiarioNutriPro
} from "@/features/nutri-pro/api/nutri-pro-client";

type AbaPortalCliente = "agenda" | "plano" | "compras" | "diario" | "metas" | "mensagens" | "documentos" | "evolucao";

type SecaoPortalClienteProps = {
  empresaId: string;
};

const TAMANHO_PAGINA = 80;
const ABAS: Array<{ id: AbaPortalCliente; label: string; descricao: string }> = [
  { id: "agenda", label: "Agenda", descricao: "Compromissos e próximos atendimentos do cliente." },
  { id: "plano", label: "Plano", descricao: "Plano alimentar publicado para o paciente." },
  { id: "compras", label: "Compras", descricao: "Lista de compras gerada pelo plano ativo." },
  { id: "diario", label: "Diário", descricao: "Registros alimentares e revisão profissional." },
  { id: "metas", label: "Metas", descricao: "Metas, lembretes e rotina combinada." },
  { id: "mensagens", label: "Recados", descricao: "Comunicação assíncrona com a nutricionista." },
  { id: "documentos", label: "Documentos", descricao: "Histórico de documentos, solicitações e prescrições." },
  { id: "evolucao", label: "Evolução", descricao: "Linha do tempo com os últimos movimentos em ordem cronológica." }
];

export function PortalClienteView({ empresaId }: SecaoPortalClienteProps) {
  const queryClient = useQueryClient();
  const [abaAtiva, definirAbaAtiva] = useState<AbaPortalCliente>("agenda");
  const [busca, setBusca] = useState("");
  const [clienteSelecionadoId, definirClienteSelecionadoId] = useState<string>("");
  const [textoDiario, setTextoDiario] = useState("");
  const [textoMensagem, setTextoMensagem] = useState("");

  const clientesQuery = useQuery({
    queryKey: ["portal-cliente-clientes", empresaId, busca],
    queryFn: () =>
      listarClientesPortal({
        empresaId,
        busca: busca.trim() || undefined,
        ativo: true,
        tamanho: TAMANHO_PAGINA
      }),
    enabled: Boolean(empresaId)
  });

  const clientes = clientesQuery.data?.itens ?? [];
  const clienteSelecionado = clientes.find((cliente) => cliente.id === clienteSelecionadoId) ?? null;

  useEffect(() => {
    if (!empresaId) {
      definirClienteSelecionadoId("");
      return;
    }

    if (!clienteSelecionadoId || !clienteSelecionado) {
      definirClienteSelecionadoId(clientes[0]?.id ?? "");
    }
  }, [clientes, clienteSelecionado, clienteSelecionadoId, empresaId]);

  const agendaQuery = useQuery({
    queryKey: ["portal-cliente-agenda", empresaId, clienteSelecionadoId],
    queryFn: () => listarAgendaPortal({ empresaId, clientePacienteId: clienteSelecionadoId }),
    enabled: Boolean(empresaId && clienteSelecionadoId)
  });

  const documentosQuery = useQuery({
    queryKey: ["portal-cliente-documentos", empresaId, clienteSelecionadoId],
    queryFn: () => listarDocumentosPortal({ empresaId, clientePacienteId: clienteSelecionadoId }),
    enabled: Boolean(empresaId && clienteSelecionadoId)
  });

  const agenda = agendaQuery.data?.itens ?? [];
  const documentos = documentosQuery.data?.itens ?? [];
  const clienteNutriSelecionado = clienteSelecionado?.area === "NUTRI";

  const planoPublicadoQuery = useQuery({
    queryKey: ["portal-cliente-plano-nutri", empresaId, clienteSelecionadoId],
    queryFn: () => consultarPlanoPublicadoNutriPro({ empresaId, pacienteId: clienteSelecionadoId }),
    enabled: Boolean(empresaId && clienteSelecionadoId && clienteNutriSelecionado),
    retry: false
  });
  const listaComprasQuery = useQuery({
    queryKey: ["portal-cliente-compras-nutri", empresaId, clienteSelecionadoId],
    queryFn: () => consultarListaComprasNutriPro({ empresaId, pacienteId: clienteSelecionadoId }),
    enabled: Boolean(empresaId && clienteSelecionadoId && clienteNutriSelecionado),
    retry: false
  });
  const diarioQuery = useQuery({
    queryKey: ["portal-cliente-diario-nutri", empresaId, clienteSelecionadoId],
    queryFn: () => listarDiarioAlimentarNutriPro({ empresaId, pacienteId: clienteSelecionadoId }),
    enabled: Boolean(empresaId && clienteSelecionadoId && clienteNutriSelecionado)
  });
  const metasQuery = useQuery({
    queryKey: ["portal-cliente-metas-nutri", empresaId, clienteSelecionadoId],
    queryFn: () => listarMetasNutriPro({ empresaId, pacienteId: clienteSelecionadoId }),
    enabled: Boolean(empresaId && clienteSelecionadoId && clienteNutriSelecionado)
  });
  const lembretesQuery = useQuery({
    queryKey: ["portal-cliente-lembretes-nutri", empresaId, clienteSelecionadoId],
    queryFn: () => listarLembretesNutriPro({ empresaId, pacienteId: clienteSelecionadoId }),
    enabled: Boolean(empresaId && clienteSelecionadoId && clienteNutriSelecionado)
  });
  const mensagensQuery = useQuery({
    queryKey: ["portal-cliente-mensagens-nutri", empresaId, clienteSelecionadoId],
    queryFn: () => listarMensagensNutriPro({ empresaId, pacienteId: clienteSelecionadoId }),
    enabled: Boolean(empresaId && clienteSelecionadoId && clienteNutriSelecionado)
  });
  const evolucaoNutriQuery = useQuery({
    queryKey: ["portal-cliente-evolucao-nutri", empresaId, clienteSelecionadoId],
    queryFn: () => listarEvolucaoNutriPro({ empresaId, pacienteId: clienteSelecionadoId }),
    enabled: Boolean(empresaId && clienteSelecionadoId && clienteNutriSelecionado)
  });

  const criarDiarioMutation = useMutation({
    mutationFn: () => criarRegistroDiarioNutriPro({ empresaId, pacienteId: clienteSelecionadoId, dados: { texto: textoDiario, refeicaoNome: "Registro do paciente" } }),
    onSuccess: () => {
      setTextoDiario("");
      queryClient.invalidateQueries({ queryKey: ["portal-cliente-diario-nutri", empresaId, clienteSelecionadoId] });
      queryClient.invalidateQueries({ queryKey: ["portal-cliente-evolucao-nutri", empresaId, clienteSelecionadoId] });
    }
  });

  const criarMetaMutation = useMutation({
    mutationFn: () =>
      criarMetaNutriPro({
        empresaId,
        pacienteId: clienteSelecionadoId,
        dados: { tipo: "HIDRATACAO", descricao: "Manter hidratação diária combinada com a nutricionista.", valorMeta: 2, unidade: "L", dataAlvo: null }
      }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["portal-cliente-metas-nutri", empresaId, clienteSelecionadoId] })
  });

  const enviarMensagemMutation = useMutation({
    mutationFn: () =>
      enviarMensagemNutriPro({
        empresaId,
        pacienteId: clienteSelecionadoId,
        dados: {
          remetenteTipo: "PACIENTE",
          remetenteNome: clienteSelecionado?.nome ?? "Paciente",
          texto: textoMensagem,
          contexto: "Portal do paciente"
        }
      }),
    onSuccess: () => {
      setTextoMensagem("");
      queryClient.invalidateQueries({ queryKey: ["portal-cliente-mensagens-nutri", empresaId, clienteSelecionadoId] });
      queryClient.invalidateQueries({ queryKey: ["portal-cliente-evolucao-nutri", empresaId, clienteSelecionadoId] });
    }
  });

  const evolucao = useMemo(() => {
    const registrosAgenda = agenda.map((compromisso) => ({
      id: `agenda-${compromisso.id}`,
      tipo: "agenda" as const,
      titulo: rotuloTipoCompromisso(compromisso.tipo),
      descricao: compromisso.observacoes || "Sem observação registrada.",
      detalhe:
        [compromisso.profissionalNome, compromisso.sala]
          .filter(Boolean)
          .map((valor) => String(valor))
          .join(" • ") || "Profissional não informado",
      status: compromisso.status,
      data: compromisso.inicio
    }));

    const registrosDocumentos = documentos.map((documento) => ({
      id: `documento-${documento.id}`,
      tipo: "documento" as const,
      titulo: documento.titulo,
      descricao: documento.conteudo || "Sem conteúdo disponível.",
      detalhe: rotuloTipoDocumento(documento.tipo),
      status: documento.status,
      data: documento.criadoEm
    }));

    const registrosNutri = (evolucaoNutriQuery.data?.itens ?? []).map((item) => ({
      id: `nutri-${item.tipo}-${item.data}`,
      tipo: "nutri" as const,
      titulo: item.titulo,
      descricao: item.descricao,
      detalhe: item.tipo,
      status: item.status,
      data: item.data
    }));

    return [...registrosAgenda, ...registrosDocumentos, ...registrosNutri].sort((a, b) => new Date(b.data).getTime() - new Date(a.data).getTime());
  }, [agenda, documentos, evolucaoNutriQuery.data?.itens]);

  if (!empresaId) {
    return (
      <div className="rounded-lg border bg-card p-6 text-center shadow-sm">
        <UserRound className="mx-auto h-9 w-9 text-muted-foreground" />
        <p className="mt-3 text-lg font-semibold text-card-foreground">Selecione uma empresa</p>
        <p className="mt-1 text-sm text-muted-foreground">Escolha uma empresa no topo para acessar o portal do cliente.</p>
      </div>
    );
  }

  return (
    <section className="grid gap-4 xl:grid-cols-[380px_minmax(0,1fr)] xl:items-start">
      <article className="min-w-0 rounded-lg border bg-card p-4 shadow-sm">
        <label className="relative">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <input
            value={busca}
            onChange={(event) => {
              setBusca(event.target.value);
            }}
            className="h-10 w-full rounded-md border bg-background pl-10 pr-3 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
            placeholder="Buscar cliente"
          />
        </label>

        <div className="mt-3 text-sm font-medium text-muted-foreground">
          {clientesQuery.isLoading ? "Carregando clientes..." : `${clientes.length} clientes encontrados`}
        </div>

        <div className="mt-4 max-h-[580px] overflow-y-auto pr-1">
          {clientesQuery.isLoading ? (
            <EstadoListaVazia icon={LoaderCircle} texto="Carregando clientes" />
          ) : clientes.length === 0 ? (
            <EstadoListaVazia icon={Inbox} texto="Nenhum cliente encontrado para esse filtro" />
          ) : (
            <div className="grid gap-2">
              {clientes.map((cliente) => {
                const ativo = cliente.id === clienteSelecionadoId;

                return (
                  <button
                    key={cliente.id}
                    type="button"
                    onClick={() => definirClienteSelecionadoId(cliente.id)}
                    className={`rounded-lg border p-3 text-left transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring ${
                      ativo ? "border-primary bg-primary/5 shadow-sm" : "bg-background hover:border-primary/50"
                    }`}
                  >
                    <div className="flex items-start justify-between gap-3">
                      <p className="font-semibold text-card-foreground">{cliente.nome}</p>
                      <span className="rounded-full border px-2 py-0.5 text-xs font-semibold text-muted-foreground">
                        {rotuloArea(cliente.area)}
                      </span>
                    </div>
                    <p className="mt-2 text-xs text-muted-foreground">{cliente.email || "E-mail não informado"}</p>
                    <p className="text-xs text-muted-foreground">{cliente.telefone || "Telefone não informado"}</p>
                  </button>
                );
              })}
            </div>
          )}
        </div>
      </article>

      <article className="rounded-lg border bg-card p-4 shadow-sm">
        {clienteSelecionadoId ? (
          <div className="grid gap-4">
            <header>
              <p className="text-sm font-medium text-primary">Portal do cliente</p>
              <h2 className="mt-1 text-2xl font-semibold tracking-normal text-card-foreground">
                {clienteSelecionado?.nome || "Cliente selecionado"}
              </h2>
              {clienteSelecionado ? (
                <p className="mt-1 text-sm text-muted-foreground">
                  {clienteSelecionado.email || "Sem e-mail"} • {clienteSelecionado.telefone || "Sem telefone"} • {rotuloArea(clienteSelecionado.area)}
                </p>
              ) : null}
            </header>

            <nav className="grid gap-2 sm:grid-cols-2 xl:grid-cols-4">
              {ABAS.map((aba) => (
                <button
                  key={aba.id}
                  type="button"
                  onClick={() => definirAbaAtiva(aba.id)}
                  className={`rounded-md border px-3 py-2 text-left text-sm transition ${
                    aba.id === abaAtiva
                      ? "border-primary bg-primary/10 text-primary font-semibold"
                      : "bg-background text-card-foreground hover:border-primary/50"
                  }`}
                >
                  <span className="font-semibold">{aba.label}</span>
                  <p className="mt-0.5 text-xs text-muted-foreground">{aba.descricao}</p>
                </button>
              ))}
            </nav>

            <section className="min-h-[420px] rounded-md border bg-background p-4">
              {abaAtiva === "agenda" ? (
                <ListaAgenda agenda={agenda} carregando={agendaQuery.isLoading} />
              ) : abaAtiva === "plano" ? (
                <PainelPlanoNutri plano={planoPublicadoQuery.data ?? null} carregando={planoPublicadoQuery.isLoading} habilitado={clienteNutriSelecionado} />
              ) : abaAtiva === "compras" ? (
                <PainelComprasNutri grupos={listaComprasQuery.data?.grupos ?? []} carregando={listaComprasQuery.isLoading} habilitado={clienteNutriSelecionado} />
              ) : abaAtiva === "diario" ? (
                <PainelDiarioNutri
                  registros={diarioQuery.data?.itens ?? []}
                  carregando={diarioQuery.isLoading}
                  habilitado={clienteNutriSelecionado}
                  texto={textoDiario}
                  onTexto={setTextoDiario}
                  enviando={criarDiarioMutation.isPending}
                  onEnviar={(event) => {
                    event.preventDefault();
                    if (textoDiario.trim()) {
                      criarDiarioMutation.mutate();
                    }
                  }}
                />
              ) : abaAtiva === "metas" ? (
                <PainelMetasNutri
                  metas={metasQuery.data?.itens ?? []}
                  lembretes={lembretesQuery.data?.itens ?? []}
                  carregando={metasQuery.isLoading || lembretesQuery.isLoading}
                  habilitado={clienteNutriSelecionado}
                  criando={criarMetaMutation.isPending}
                  onCriar={() => criarMetaMutation.mutate()}
                />
              ) : abaAtiva === "mensagens" ? (
                <PainelMensagensNutri
                  mensagens={mensagensQuery.data?.itens ?? []}
                  carregando={mensagensQuery.isLoading}
                  habilitado={clienteNutriSelecionado}
                  texto={textoMensagem}
                  onTexto={setTextoMensagem}
                  enviando={enviarMensagemMutation.isPending}
                  onEnviar={(event) => {
                    event.preventDefault();
                    if (textoMensagem.trim()) {
                      enviarMensagemMutation.mutate();
                    }
                  }}
                />
              ) : abaAtiva === "documentos" ? (
                <ListaDocumentos documentos={documentos} carregando={documentosQuery.isLoading} />
              ) : (
                <ListaEvolucao entradas={evolucao} carregando={agendaQuery.isLoading || documentosQuery.isLoading} />
              )}
            </section>
          </div>
        ) : (
          <div className="rounded-md border border-dashed border-muted-foreground/40 p-10 text-center">
            <UserRound className="mx-auto h-9 w-9 text-muted-foreground" />
            <h3 className="mt-3 font-semibold text-card-foreground">Selecione um cliente</h3>
            <p className="mt-1 text-sm text-muted-foreground">Escolha um cliente à esquerda para visualizar agenda, documentos e evolução.</p>
          </div>
        )}
      </article>
    </section>
  );
}

function PainelPlanoNutri({ plano, carregando, habilitado }: { plano: PlanoAlimentarNutriPro | null; carregando: boolean; habilitado: boolean }) {
  if (!habilitado) {
    return <EstadoListaVazia icon={Inbox} texto="Selecione um paciente de nutrição para visualizar o plano alimentar." />;
  }

  if (carregando) {
    return <EstadoListaVazia icon={LoaderCircle} texto="Carregando plano alimentar" />;
  }

  if (!plano) {
    return <EstadoListaVazia icon={Inbox} texto="Nenhum plano alimentar publicado ainda." />;
  }

  return (
    <div className="grid gap-3">
      <article className="rounded-md border border-emerald-200 bg-emerald-50 p-4">
        <div className="flex flex-wrap items-start justify-between gap-3">
          <div>
            <p className="text-sm font-semibold text-emerald-950">{plano.objetivo}</p>
            <p className="mt-1 text-sm leading-6 text-emerald-950/80">{plano.descricao ?? "Plano alimentar ativo para acompanhamento."}</p>
          </div>
          <span className="rounded-md border border-emerald-300 bg-white px-2 py-1 text-xs font-semibold text-emerald-800">{plano.statusRotulo}</span>
        </div>
      </article>
      {plano.refeicoes.map((refeicao) => (
        <article key={refeicao.id} className="rounded-md border bg-card p-3">
          <p className="font-semibold text-card-foreground">
            {refeicao.nome}
            {refeicao.horario ? <span className="font-medium text-muted-foreground"> • {refeicao.horario}</span> : null}
          </p>
          <div className="mt-2 grid gap-2">
            {refeicao.itens.map((item) => (
              <div key={item.id} className="rounded-md border bg-background p-2 text-sm text-muted-foreground">
                {item.nome} · {item.quantidade} {item.unidadeMedida}
                {item.observacoes ? ` · ${item.observacoes}` : ""}
              </div>
            ))}
          </div>
        </article>
      ))}
    </div>
  );
}

function PainelComprasNutri({ grupos, carregando, habilitado }: { grupos: GrupoListaComprasNutriPro[]; carregando: boolean; habilitado: boolean }) {
  if (!habilitado) {
    return <EstadoListaVazia icon={Inbox} texto="Selecione um paciente de nutrição para visualizar compras." />;
  }

  if (carregando) {
    return <EstadoListaVazia icon={LoaderCircle} texto="Carregando lista de compras" />;
  }

  if (!grupos.length) {
    return <EstadoListaVazia icon={Inbox} texto="Nenhuma lista de compras gerada para o plano ativo." />;
  }

  return (
    <div className="grid gap-3 md:grid-cols-2">
      {grupos.map((grupo) => (
        <article key={grupo.categoria} className="rounded-md border bg-card p-3">
          <p className="font-semibold text-card-foreground">{grupo.categoria}</p>
          <div className="mt-2 grid gap-2">
            {grupo.itens.map((item) => (
              <div key={`${grupo.categoria}-${item.nome}-${item.unidadeMedida}`} className="rounded-md border bg-background p-2 text-sm text-muted-foreground">
                {item.nome} · {item.quantidade} {item.unidadeMedida}
                {item.refeicoes ? ` · ${item.refeicoes}` : ""}
              </div>
            ))}
          </div>
        </article>
      ))}
    </div>
  );
}

function PainelDiarioNutri({
  registros,
  carregando,
  habilitado,
  texto,
  onTexto,
  enviando,
  onEnviar
}: {
  registros: RegistroDiarioNutriPro[];
  carregando: boolean;
  habilitado: boolean;
  texto: string;
  onTexto: (texto: string) => void;
  enviando: boolean;
  onEnviar: (event: FormEvent<HTMLFormElement>) => void;
}) {
  if (!habilitado) {
    return <EstadoListaVazia icon={Inbox} texto="Selecione um paciente de nutrição para usar o diário alimentar." />;
  }

  return (
    <div className="grid gap-4">
      <form onSubmit={onEnviar} className="rounded-md border bg-card p-3">
        <label className="grid gap-2 text-sm font-medium text-card-foreground">
          Novo registro alimentar
          <textarea
            value={texto}
            onChange={(event) => onTexto(event.target.value)}
            className="min-h-24 rounded-md border bg-background px-3 py-2 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
            placeholder="Descreva refeição, sintomas, fome/saciedade ou dúvida do dia"
          />
        </label>
        <button type="submit" disabled={!texto.trim() || enviando} className="mt-3 inline-flex h-10 items-center justify-center rounded-md bg-primary px-4 text-sm font-semibold text-primary-foreground disabled:opacity-60">
          {enviando ? "Enviando" : "Enviar registro"}
        </button>
      </form>
      {carregando ? <EstadoListaVazia icon={LoaderCircle} texto="Carregando diário" /> : null}
      <div className="grid gap-2">
        {registros.length ? registros.map((registro) => <LinhaDiarioPortal key={registro.id} registro={registro} />) : !carregando ? <EstadoListaVazia icon={Inbox} texto="Nenhum registro no diário ainda." /> : null}
      </div>
    </div>
  );
}

function PainelMetasNutri({
  metas,
  lembretes,
  carregando,
  habilitado,
  criando,
  onCriar
}: {
  metas: MetaNutriPro[];
  lembretes: LembreteNutriPro[];
  carregando: boolean;
  habilitado: boolean;
  criando: boolean;
  onCriar: () => void;
}) {
  if (!habilitado) {
    return <EstadoListaVazia icon={Inbox} texto="Selecione um paciente de nutrição para visualizar metas." />;
  }

  if (carregando) {
    return <EstadoListaVazia icon={LoaderCircle} texto="Carregando metas e lembretes" />;
  }

  return (
    <div className="grid gap-3">
      <button type="button" onClick={onCriar} disabled={criando} className="w-fit rounded-md border bg-card px-3 py-2 text-sm font-semibold text-card-foreground hover:border-primary/50 disabled:opacity-60">
        {criando ? "Criando meta" : "Criar meta de hidratação"}
      </button>
      {metas.map((meta) => (
        <article key={meta.id} className="rounded-md border bg-card p-3">
          <p className="font-semibold text-card-foreground">{meta.tipo}</p>
          <p className="mt-1 text-sm text-muted-foreground">{meta.descricao} · {meta.valorMeta} {meta.unidade ?? ""}</p>
        </article>
      ))}
      {lembretes.map((lembrete) => (
        <article key={lembrete.id} className="rounded-md border bg-card p-3">
          <p className="font-semibold text-card-foreground">{lembrete.titulo}</p>
          <p className="mt-1 text-sm text-muted-foreground">{lembrete.horario ?? "Sem horário"} · {lembrete.frequencia}</p>
        </article>
      ))}
      {!metas.length && !lembretes.length ? <EstadoListaVazia icon={Inbox} texto="Nenhuma meta ou lembrete configurado ainda." /> : null}
    </div>
  );
}

function PainelMensagensNutri({
  mensagens,
  carregando,
  habilitado,
  texto,
  onTexto,
  enviando,
  onEnviar
}: {
  mensagens: MensagemNutriPro[];
  carregando: boolean;
  habilitado: boolean;
  texto: string;
  onTexto: (texto: string) => void;
  enviando: boolean;
  onEnviar: (event: FormEvent<HTMLFormElement>) => void;
}) {
  if (!habilitado) {
    return <EstadoListaVazia icon={Inbox} texto="Selecione um paciente de nutrição para trocar recados." />;
  }

  return (
    <div className="grid gap-4">
      <form onSubmit={onEnviar} className="rounded-md border bg-card p-3">
        <label className="grid gap-2 text-sm font-medium text-card-foreground">
          Novo recado
          <textarea
            value={texto}
            onChange={(event) => onTexto(event.target.value)}
            className="min-h-20 rounded-md border bg-background px-3 py-2 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-ring"
            placeholder="Envie uma dúvida ou atualização para a nutricionista"
          />
        </label>
        <button type="submit" disabled={!texto.trim() || enviando} className="mt-3 inline-flex h-10 items-center justify-center rounded-md bg-primary px-4 text-sm font-semibold text-primary-foreground disabled:opacity-60">
          {enviando ? "Enviando" : "Enviar recado"}
        </button>
      </form>
      {carregando ? <EstadoListaVazia icon={LoaderCircle} texto="Carregando mensagens" /> : null}
      <div className="grid gap-2">
        {mensagens.length ? mensagens.map((mensagem) => <LinhaMensagemPortal key={mensagem.id} mensagem={mensagem} />) : !carregando ? <EstadoListaVazia icon={Inbox} texto="Nenhum recado trocado ainda." /> : null}
      </div>
    </div>
  );
}

function ListaAgenda({ agenda, carregando }: { agenda: CompromissoPortal[]; carregando: boolean }) {
  if (carregando) {
    return <EstadoListaVazia icon={LoaderCircle} texto="Carregando agenda" />;
  }

  if (!agenda.length) {
    return <EstadoListaVazia icon={Inbox} texto="Nenhum compromisso encontrado." />;
  }

  return (
    <div className="grid gap-2">
      {agenda.map((compromisso) => (
        <article key={compromisso.id} className="rounded-md border bg-card p-3">
          <div className="flex flex-wrap items-start justify-between gap-3">
            <div className="min-w-0">
              <p className="font-semibold text-card-foreground">{rotuloTipoCompromisso(compromisso.tipo)}</p>
              <p className="mt-1 text-xs text-muted-foreground">
                {formatarDataHora(compromisso.inicio)} • {formatarDataHora(compromisso.fim)}
              </p>
            </div>
            <span className={`rounded-md border px-2 py-1 text-xs font-semibold ${classeStatusAgenda(compromisso.status)}`}>
              {rotuloStatusAgenda(compromisso.status)}
            </span>
          </div>
          <div className="mt-3 grid gap-1 text-sm">
            <p className="text-card-foreground">Profissional: {compromisso.profissionalNome || "Não informado"}</p>
            {compromisso.sala ? <p className="text-muted-foreground">Sala: {compromisso.sala}</p> : null}
            {compromisso.observacoes ? <p className="text-muted-foreground">{compromisso.observacoes}</p> : null}
          </div>
        </article>
      ))}
    </div>
  );
}

function ListaDocumentos({ documentos, carregando }: { documentos: DocumentoPortal[]; carregando: boolean }) {
  if (carregando) {
    return <EstadoListaVazia icon={LoaderCircle} texto="Carregando documentos" />;
  }

  if (!documentos.length) {
    return <EstadoListaVazia icon={Inbox} texto="Nenhum documento encontrado." />;
  }

  return (
    <div className="grid gap-2">
      {documentos.map((documento) => (
        <article key={documento.id} className="rounded-md border bg-card p-3">
          <div className="flex flex-wrap items-start justify-between gap-2">
            <p className="font-semibold text-card-foreground">{documento.titulo}</p>
            <span className={`rounded-md border px-2 py-1 text-xs font-semibold ${classeStatusDocumento(documento.status)}`}>
              {rotuloStatusDocumento(documento.status)}
            </span>
          </div>
          <p className="mt-2 text-xs font-medium text-muted-foreground">{rotuloTipoDocumento(documento.tipo)} • {formatarDataHora(documento.criadoEm)}</p>
          <p className="mt-2 line-clamp-3 text-sm text-muted-foreground">{documento.conteudo}</p>
        </article>
      ))}
    </div>
  );
}

function ListaEvolucao({
  entradas,
  carregando
}: {
  entradas: Array<{
    id: string;
    tipo: "agenda" | "documento" | "nutri";
    titulo: string;
    descricao: string;
    detalhe: string;
    status: string;
    data: string;
  }>;
  carregando: boolean;
}) {
  if (carregando) {
    return <EstadoListaVazia icon={LoaderCircle} texto="Carregando evolução" />;
  }

  if (!entradas.length) {
    return <EstadoListaVazia icon={Inbox} texto="Ainda não há itens de evolução para esse cliente." />;
  }

  return (
    <div className="grid gap-2">
      {entradas.map((entrada) => (
        <article key={entrada.id} className="rounded-md border bg-card p-3">
          <div className="flex flex-wrap items-start justify-between gap-2">
            <div className="min-w-0">
              <p className="font-semibold text-card-foreground">{entrada.titulo}</p>
              <p className="mt-1 text-xs text-muted-foreground">{entrada.detalhe}</p>
            </div>
            <span
              className={`rounded-md border px-2 py-1 text-xs font-semibold ${
                entrada.tipo === "agenda" ? classeStatusAgenda(entrada.status as TipoStatusAgenda) : entrada.tipo === "documento" ? classeStatusDocumento(entrada.status as TipoDocumentoPortal) : "border-emerald-300 bg-emerald-50 text-emerald-800"
              }`}
            >
              {entrada.status}
            </span>
          </div>
          <p className="mt-2 text-xs text-muted-foreground">{entrada.descricao}</p>
          <p className="mt-1 text-xs font-medium text-muted-foreground">Registrado em {formatarDataHora(entrada.data)}</p>
        </article>
      ))}
    </div>
  );
}

function LinhaDiarioPortal({ registro }: { registro: RegistroDiarioNutriPro }) {
  return (
    <article className="rounded-md border bg-card p-3">
      <div className="flex flex-wrap items-start justify-between gap-2">
        <p className="font-semibold text-card-foreground">{registro.refeicaoNome ?? "Registro alimentar"}</p>
        <span className={`rounded-md border px-2 py-1 text-xs font-semibold ${registro.statusRevisao === "REVISADO" ? "border-emerald-300 bg-emerald-50 text-emerald-800" : "border-amber-300 bg-amber-50 text-amber-800"}`}>
          {registro.statusRevisao === "REVISADO" ? "Revisado" : "Pendente"}
        </span>
      </div>
      <p className="mt-2 text-sm text-muted-foreground">{registro.texto}</p>
      {registro.parecerProfissional ? <p className="mt-2 rounded-md border bg-background p-2 text-sm text-emerald-900">{registro.parecerProfissional}</p> : null}
      <p className="mt-2 text-xs text-muted-foreground">{formatarDataHora(registro.registradoEm)}</p>
    </article>
  );
}

function LinhaMensagemPortal({ mensagem }: { mensagem: MensagemNutriPro }) {
  return (
    <article className="rounded-md border bg-card p-3">
      <div className="flex flex-wrap items-start justify-between gap-2">
        <p className="font-semibold text-card-foreground">{mensagem.remetenteNome}</p>
        <span className="rounded-md border bg-background px-2 py-1 text-xs font-semibold text-muted-foreground">{mensagem.remetenteTipo}</span>
      </div>
      <p className="mt-2 text-sm text-muted-foreground">{mensagem.texto}</p>
      <p className="mt-2 text-xs text-muted-foreground">{mensagem.contexto ?? "Acompanhamento"} • {formatarDataHora(mensagem.enviadaEm)}</p>
    </article>
  );
}

function EstadoListaVazia({
  icon: Icon,
  texto
}: {
  icon: LucideIcon;
  texto: string;
}) {
  return (
    <div className="rounded-md border border-dashed border-muted-foreground/40 p-6 text-center">
      <Icon className="mx-auto h-9 w-9 text-muted-foreground" />
      <p className="mt-2 text-sm text-muted-foreground">{texto}</p>
    </div>
  );
}

function rotuloArea(area: ClientePortal["area"]) {
  switch (area) {
    case "NUTRI":
      return "Nutrição";
    case "BEAUTY":
      return "Beleza";
    case "BIOMED":
      return "Biomed";
    case "FISIO":
      return "Fisioterapia";
    case "SPACES":
      return "Spaces";
    case "PSICO":
      return "Psicologia";
    case "FONO":
      return "Fonoaudiologia";
    case "FARMACIA_CLINICA":
      return "Farmácia clínica";
    case "ODONTO":
      return "Odonto";
    case "TERAPIAS_INTEGRATIVAS":
      return "Terapias integrativas";
    case "GERAL":
    default:
      return "Geral";
  }
}

function rotuloTipoCompromisso(tipo: CompromissoPortal["tipo"]) {
  switch (tipo) {
    case "ATENDIMENTO":
      return "Atendimento";
    case "RETORNO":
      return "Retorno";
    case "AVALIACAO":
      return "Avaliação";
    case "BLOQUEIO":
      return "Bloqueio";
    case "OUTRO":
    default:
      return "Outro";
  }
}

function rotuloTipoDocumento(tipo: TipoDocumentoPortal) {
  switch (tipo) {
    case "DECLARACAO":
      return "Declaração";
    case "RELATORIO":
      return "Relatório";
    case "TERMO":
      return "Termo";
    case "ORIENTACAO":
      return "Orientação";
    case "RECIBO":
      return "Recibo";
    case "SOLICITACAO_EXAMES":
      return "Solicitação de exames";
    case "PRESCRICAO":
      return "Prescrição";
    case "PLANO_ALIMENTAR":
      return "Plano alimentar";
    case "OUTRO":
      return "Outro";
  }
}

function rotuloStatusAgenda(status: TipoStatusAgenda) {
  switch (status) {
    case "AGENDADO":
      return "Agendado";
    case "CONFIRMADO":
      return "Confirmado";
    case "REALIZADO":
      return "Realizado";
    case "CANCELADO":
      return "Cancelado";
    case "FALTOU":
      return "Faltou";
    case "REMARCADO":
      return "Remarcado";
    default:
      return status;
  }
}

function rotuloStatusDocumento(status: string) {
  switch (status) {
    case "RASCUNHO":
      return "Rascunho";
    case "EMITIDO":
      return "Emitido";
    case "CANCELADO":
      return "Cancelado";
    default:
      return status;
  }
}

function classeStatusAgenda(status: TipoStatusAgenda) {
  switch (status) {
    case "AGENDADO":
      return "border-sky-300 bg-sky-50 text-sky-800";
    case "CONFIRMADO":
      return "border-emerald-300 bg-emerald-50 text-emerald-800";
    case "REALIZADO":
      return "border-indigo-300 bg-indigo-50 text-indigo-800";
    case "CANCELADO":
      return "border-rose-300 bg-rose-50 text-rose-800";
    case "FALTOU":
      return "border-orange-300 bg-orange-50 text-orange-800";
    case "REMARCADO":
      return "border-amber-300 bg-amber-50 text-amber-800";
    default:
      return "border-muted text-muted-foreground";
  }
}

function classeStatusDocumento(status: string) {
  switch (status) {
    case "EMITIDO":
      return "border-emerald-300 bg-emerald-50 text-emerald-800";
    case "CANCELADO":
      return "border-rose-300 bg-rose-50 text-rose-800";
    case "RASCUNHO":
    default:
      return "border-muted bg-background text-muted-foreground";
  }
}

function formatarDataHora(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(valor));
}
