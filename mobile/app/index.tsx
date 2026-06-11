import { Link } from "expo-router";
import { useEffect, useState } from "react";
import { Button, StyleSheet, Text, View } from "react-native";
import {
  CapaPagina,
  CabecalhoSecao,
  Cartao,
  EstadoVazio,
  GridCards,
  temaAtendePro
} from "@/components/ui-shell";
import { carregarSessaoAutenticada, limparSessaoAutenticada, type SessaoAutenticada } from "@/lib/auth";

const secoesIniciais = [
  {
    titulo: "Entrar",
    rota: "/auth",
    descricao: "Acesso real com JWT, refresh token e contexto de empresa."
  },
  {
    titulo: "Área do Cliente",
    rota: "/cliente",
    descricao: "Agenda, documentos, diário, plano Nutri e mensagens reais."
  },
  {
    titulo: "Área do Profissional",
    rota: "/profissional",
    descricao: "Agenda do dia, mensagens e carteira ativa conectadas ao backend."
  },
  {
    titulo: "Notificações",
    rota: "/notificacoes",
    descricao: "Lembretes locais e preparação para push/eventos."
  }
];

export default function TelaInicialMobile() {
  const [sessao, setSessao] = useState<SessaoAutenticada | null>(null);

  useEffect(() => {
    carregarSessaoAutenticada().then(setSessao);
  }, []);

  const sair = async () => {
    await limparSessaoAutenticada();
    setSessao(null);
  };

  return (
    <CapaPagina
      titulo="AtendePro"
      subtitulo="Aplicativo profissional conectado ao backend real para agenda, comunicação e documentos."
    >
      <CabecalhoSecao titulo="Sessão" acao={sessao ? "Autenticada" : "Entrar"} />
      <Cartao
        titulo={sessao ? sessao.usuario.nome : "Sem sessão ativa"}
        descricao={sessao ? `Empresa: ${sessao.usuario.empresaId ?? "sem contexto"}` : "Faça login para carregar dados reais do tenant."}
      >
        {sessao ? <Button title="Sair da sessão" onPress={sair} /> : <EstadoVazio texto="Use a entrada rápida de login para autenticar no backend." />}
      </Cartao>

      <CabecalhoSecao titulo="Entradas rápidas" acao="Abrir app" />
      <GridCards>
        {secoesIniciais.map((secao) => (
          <Link key={secao.rota} href={secao.rota}>
            <Cartao
              titulo={secao.titulo}
              descricao={secao.descricao}
              destaque
            />
          </Link>
        ))}
      </GridCards>
      <Cartao titulo="Status da release" descricao="R14 consolida governança, autenticação mobile real e fluxos iniciais conectados.">
        <EstadoVazio texto="Fluxos principais: login, clientes, agenda, documentos, diário e mensagens Nutri usando APIs reais." />
      </Cartao>
      <View style={estilos.chamada}>
        <Text style={estilos.chamadaTexto}>Ajuste R14: mobile deixa de ser apenas demo e passa a consumir o backend local/autenticado.</Text>
      </View>
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  chamada: {
    backgroundColor: temaAtendePro.superficieSecundaria,
    borderColor: temaAtendePro.borda,
    borderWidth: 1,
    borderRadius: 12,
    padding: 12
  },
  chamadaTexto: {
    color: temaAtendePro.texto,
    textAlign: "center"
  }
});
