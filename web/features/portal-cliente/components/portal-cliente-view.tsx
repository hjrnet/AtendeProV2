"use client";

import { useEffect, useMemo, useState } from "react";
import { Inbox, LoaderCircle, Search, UserRound, type LucideIcon } from "lucide-react";
import { useQuery } from "@tanstack/react-query";

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

type AbaPortalCliente = "agenda" | "documentos" | "evolucao";

type SecaoPortalClienteProps = {
  empresaId: string;
};

const TAMANHO_PAGINA = 80;
const ABAS: Array<{ id: AbaPortalCliente; label: string; descricao: string }> = [
  { id: "agenda", label: "Agenda", descricao: "Compromissos e próximos atendimentos do cliente." },
  { id: "documentos", label: "Documentos", descricao: "Histórico de documentos, solicitações e prescrições." },
  { id: "evolucao", label: "Evolução", descricao: "Linha do tempo com os últimos movimentos em ordem cronológica." }
];

export function PortalClienteView({ empresaId }: SecaoPortalClienteProps) {
  const [abaAtiva, definirAbaAtiva] = useState<AbaPortalCliente>("agenda");
  const [busca, setBusca] = useState("");
  const [clienteSelecionadoId, definirClienteSelecionadoId] = useState<string>("");

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

    return [...registrosAgenda, ...registrosDocumentos].sort((a, b) => new Date(b.data).getTime() - new Date(a.data).getTime());
  }, [agenda, documentos]);

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

            <nav className="grid gap-2 sm:grid-cols-3">
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
    tipo: "agenda" | "documento";
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
                entrada.tipo === "agenda" ? classeStatusAgenda(entrada.status as TipoStatusAgenda) : classeStatusDocumento(entrada.status as TipoDocumentoPortal)
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
