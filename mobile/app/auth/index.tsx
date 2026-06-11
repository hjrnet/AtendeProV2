import { useEffect, useState } from "react";
import { Button, StyleSheet, Text, TextInput } from "react-native";
import { useRouter } from "expo-router";

import { CapaPagina, Cartao, EstadoVazio, temaAtendePro } from "@/components/ui-shell";
import { autenticarSessao, carregarSessaoAutenticada, limparSessaoAutenticada, salvarSessaoAutenticada } from "@/lib/auth";
import { consultarPerfilMobile, type LoginRequest } from "@/lib/api/client";

function extrairMensagemErro(erro: unknown) {
  if (erro instanceof Error) {
    return erro.message;
  }

  return "Não foi possível autenticar agora.";
}

function montarInfoSessao(sessao: Awaited<ReturnType<typeof carregarSessaoAutenticada>>) {
  if (!sessao) {
    return "Sem sessão ativa";
  }

  return `Usuário: ${sessao.usuario.nome}\nEmpresa: ${sessao.usuario.empresaId ?? "não informada"}`;
}

function resolverRotaInicial(perfis: string[]) {
  if (perfis.includes("PROFISSIONAL") || perfis.includes("EMPRESA_ADMIN")) {
    return "/profissional" as const;
  }

  return "/cliente" as const;
}

export default function LoginMobile() {
  const [email, setEmail] = useState("karol.nutri@atendepro.local");
  const [senha, setSenha] = useState("AtendePro@123");
  const [erro, setErro] = useState("");
  const [emProgresso, setEmProgresso] = useState(false);
  const [infoSessao, setInfoSessao] = useState("Sem sessão ativa");
  const router = useRouter();

  useEffect(() => {
    carregarSessaoAutenticada().then((sessao) => {
      setInfoSessao(montarInfoSessao(sessao));
    });
  }, []);

  const enviar = async () => {
    setEmProgresso(true);
    setErro("");

    try {
      const resposta = await autenticarSessao({ email: email.trim(), senha } as LoginRequest);
      await salvarSessaoAutenticada(resposta);
      await consultarPerfilMobile();
      setInfoSessao(montarInfoSessao(await carregarSessaoAutenticada()));
      router.replace(resolverRotaInicial(resposta.usuario.perfis));
    } catch (falha) {
      await limparSessaoAutenticada();
      setErro(extrairMensagemErro(falha));
    } finally {
      setEmProgresso(false);
    }
  };

  return (
    <CapaPagina
      titulo="Acesso ao AtendePro"
      subtitulo="Login real contra o backend autenticado."
    >
      <Cartao titulo="Entrar no app" descricao="Utilize credencial real da base (ex.: karol.nutri@atendepro.local).">
        <TextInput
          style={styles.campo}
          value={email}
          onChangeText={setEmail}
          placeholder="E-mail"
          keyboardType="email-address"
          autoCapitalize="none"
          autoCorrect={false}
        />
        <TextInput
          style={styles.campo}
          value={senha}
          onChangeText={setSenha}
          placeholder="Senha"
          secureTextEntry
          autoCapitalize="none"
          autoCorrect={false}
        />
        <Button
          title={emProgresso ? "Entrando..." : "Entrar"}
          onPress={enviar}
          disabled={emProgresso}
        />

        {erro ? <Text style={styles.erro}>{erro}</Text> : null}
      </Cartao>

      <EstadoVazio texto={infoSessao} />
    </CapaPagina>
  );
}

const styles = StyleSheet.create({
  campo: {
    backgroundColor: temaAtendePro.superficie,
    borderWidth: 1,
    borderColor: temaAtendePro.borda,
    borderRadius: 12,
    padding: 12,
    marginBottom: 12,
    color: temaAtendePro.texto
  },
  erro: {
    marginTop: 10,
    color: temaAtendePro.erro,
    lineHeight: 20
  }
});
