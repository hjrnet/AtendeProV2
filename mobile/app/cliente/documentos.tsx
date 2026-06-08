import React, { useEffect, useState } from "react";
import { ActivityIndicator, StyleSheet, Text, View } from "react-native";

import {
  CapaPagina,
  Cartao,
  CabecalhoSecao,
  EstadoVazio,
  ItemLista,
  temaAtendePro
} from "@/components/ui-shell";
import { carregarSessaoAutenticada } from "@/lib/auth";
import { listarDocumentosPortal, type DocumentoProfissionalApi } from "@/lib/api/client";

type SelecaoDeStatus = {
  pendentes: DocumentoProfissionalApi[];
  cancelados: DocumentoProfissionalApi[];
};

function extrairErro(mensagem: unknown) {
  if (mensagem instanceof Error) {
    return mensagem.message;
  }

  return "Não foi possível carregar documentos.";
}

function formatarData(dataIso: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric"
  }).format(new Date(dataIso));
}

function textoStatus(status: DocumentoProfissionalApi["status"]) {
  const mapa: Record<DocumentoProfissionalApi["status"], string> = {
    RASCUNHO: "Rascunho",
    EMITIDO: "Emitido",
    CANCELADO: "Cancelado"
  };

  return mapa[status];
}

function classificarPorStatus(documentos: DocumentoProfissionalApi[]): SelecaoDeStatus {
  return documentos.reduce<SelecaoDeStatus>(
    (acumulador, documento) => {
      if (documento.status === "RASCUNHO" || documento.status === "EMITIDO") {
        acumulador.pendentes.push(documento);
        return acumulador;
      }

      acumulador.cancelados.push(documento);
      return acumulador;
    },
    { pendentes: [], cancelados: [] }
  );
}

export default function DocumentosClienteMobile() {
  const [documentos, setDocumentos] = useState<DocumentoProfissionalApi[]>([]);
  const [erro, setErro] = useState("");
  const [carregando, setCarregando] = useState(true);
  const [empresaId, setEmpresaId] = useState<string | null>(null);

  useEffect(() => {
    let ativo = true;
    const carregar = async () => {
      setCarregando(true);
      setErro("");

      try {
        const sessao = await carregarSessaoAutenticada();
        const contextoEmpresa = sessao?.usuario.empresaId ?? null;
        setEmpresaId(contextoEmpresa);

        const resposta = await listarDocumentosPortal({
          empresaId: contextoEmpresa,
          pagina: 0,
          tamanho: 50,
          ativo: true
        });

        if (!ativo) {
          return;
        }

        setDocumentos(resposta?.itens ?? []);
      } catch (falha) {
        if (ativo) {
          setErro(extrairErro(falha));
        }
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    };

    void carregar();
    return () => {
      ativo = false;
    };
  }, []);

  const secoes = classificarPorStatus(documentos);

  return (
    <CapaPagina titulo="Documentos do cliente" subtitulo={`Empresa: ${empresaId ?? "sem contexto."}`}>
      {carregando ? (
        <View style={estilos.estadoCarregamento}>
          <ActivityIndicator size="small" color={temaAtendePro.destaque} />
          <Text style={estilos.textoCarregamento}>Buscando documentos...</Text>
        </View>
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : (
        <>
          <CabecalhoSecao titulo="Documentos ativos" />
          <Cartao titulo={`Rascunho e emitidos (${secoes.pendentes.length})`} descricao="Documentos recentes para conferência.">
            <View style={estilos.lista}>
              {secoes.pendentes.length === 0 ? (
                <EstadoVazio texto="Sem documentos ativos no período." />
              ) : (
                secoes.pendentes.map((documento) => (
                  <ItemLista
                    key={documento.id}
                    titulo={documento.titulo}
                    meta={`${documento.tipo} • ${formatarData(documento.criadoEm)}`}
                    descricao={`${documento.profissionalNome || "Profissional não identificado"} • ${textoStatus(documento.status)}`}
                    status={textoStatus(documento.status)}
                  />
                ))
              )}
            </View>
          </Cartao>

          <CabecalhoSecao titulo="Cancelamentos" />
          <Cartao titulo={`Cancelados (${secoes.cancelados.length})`} descricao="Documentos com fluxo encerrado.">
            {secoes.cancelados.length === 0 ? (
              <EstadoVazio texto="Sem documentos cancelados recentes." />
            ) : (
              <View style={estilos.lista}>
                {secoes.cancelados.map((documento) => (
                  <ItemLista
                    key={documento.id}
                    titulo={documento.titulo}
                    meta={`${documento.tipo} • ${formatarData(documento.criadoEm)}`}
                    descricao={`${documento.profissionalNome || "Profissional não identificado"} • ${textoStatus(documento.status)}`}
                    status={textoStatus(documento.status)}
                  />
                ))}
              </View>
            )}
          </Cartao>
        </>
      )}
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  lista: {
    gap: 12
  },
  estadoCarregamento: {
    backgroundColor: temaAtendePro.superficieSecundaria,
    borderColor: temaAtendePro.borda,
    borderWidth: 1,
    borderRadius: 12,
    padding: 12,
    gap: 10,
    marginBottom: 14
  },
  textoCarregamento: {
    color: temaAtendePro.textoSecundario,
    textAlign: "center"
  }
});
