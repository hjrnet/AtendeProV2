import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import {
  CapaPagina,
  Cartao,
  CabecalhoSecao,
  EstadoVazio,
  ItemLista,
  temaAtendePro
} from "@/components/ui-shell";
import { carregarSessaoAutenticada } from "@/lib/auth";
import {
  enviarMensagemNutri,
  listarMensagensNutri,
  resolverPrimeiroPacienteNutri,
  type MensagemNutriApi
} from "@/lib/api/client";

export default function MensagensProfissionalMobile() {
  const [mensagens, setMensagens] = useState<MensagemNutriApi[]>([]);
  const [texto, setTexto] = useState("");
  const [empresaId, setEmpresaId] = useState<string | null>(null);
  const [pacienteId, setPacienteId] = useState<string | null>(null);
  const [profissionalNome, setProfissionalNome] = useState("Profissional");
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  const carregar = async () => {
    setCarregando(true);
    setErro("");

    try {
      const sessao = await carregarSessaoAutenticada();
      const contextoEmpresa = sessao?.usuario.empresaId ?? null;
      setEmpresaId(contextoEmpresa);
      setProfissionalNome(sessao?.usuario.nome ?? "Profissional");

      const paciente = await resolverPrimeiroPacienteNutri(contextoEmpresa);
      setPacienteId(paciente?.id ?? null);
      if (!paciente) {
        setMensagens([]);
        return;
      }

      const resposta = await listarMensagensNutri({ empresaId: contextoEmpresa, pacienteId: paciente.id });
      setMensagens(resposta.itens ?? []);
    } catch (falha) {
      setErro(falha instanceof Error ? falha.message : "Não foi possível carregar mensagens.");
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    void carregar();
  }, []);

  const responder = async () => {
    if (!texto.trim() || !pacienteId) {
      return;
    }

    try {
      const nova = await enviarMensagemNutri({
        empresaId,
        pacienteId,
        remetenteTipo: "PROFISSIONAL",
        remetenteNome: profissionalNome,
        texto: texto.trim(),
        contexto: "App profissional"
      });
      setMensagens((estadoAtual) => [nova, ...estadoAtual]);
      setTexto("");
    } catch (falha) {
      setErro(falha instanceof Error ? falha.message : "Não foi possível responder.");
    }
  };

  return (
    <CapaPagina titulo="Mensagens" subtitulo="Fila de comunicação com pacientes e equipe interna.">
      {carregando ? (
        <View style={estilos.estadoCarregamento}>
          <ActivityIndicator size="small" color={temaAtendePro.destaque} />
          <Text style={estilos.textoCarregamento}>Carregando recados...</Text>
        </View>
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : null}

      <CabecalhoSecao titulo="Pendentes" />
      <View style={estilos.lista}>
        {mensagens.length === 0 ? <EstadoVazio texto="Nenhuma mensagem do acompanhamento Nutri ainda." /> : null}
        {mensagens.map((mensagem) => (
          <ItemLista
            key={mensagem.id}
            titulo={`${mensagem.remetenteNome} · ${mensagem.remetenteTipo}`}
            meta={formatarData(mensagem.enviadaEm)}
            descricao={mensagem.texto}
            status={mensagem.lidaPeloProfissional ? "Lido" : "Pendente"}
          />
        ))}
      </View>
      <CabecalhoSecao titulo="Atalhos" />
      <Cartao titulo="Ações rápidas" descricao="Acompanhe e retorne com confirmação para manter fluxo vivo.">
        <TextInput
          style={estilos.input}
          value={texto}
          onChangeText={setTexto}
          placeholder="Responder paciente"
          placeholderTextColor={temaAtendePro.textoSecundario}
        />
        <Pressable style={estilos.botao} onPress={responder}>
          <Text style={estilos.textoBotao}>Responder</Text>
        </Pressable>
      </Cartao>
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  lista: {
    gap: 12
  },
  acao: {
    color: temaAtendePro.destaque,
    fontWeight: "700",
    paddingBottom: 10,
    borderBottomWidth: 1,
    borderBottomColor: temaAtendePro.borda,
    marginTop: 12
  },
  input: {
    borderWidth: 1,
    borderColor: temaAtendePro.borda,
    borderRadius: 12,
    padding: 12,
    backgroundColor: temaAtendePro.superficie,
    color: temaAtendePro.texto
  },
  botao: {
    marginTop: 12,
    borderRadius: 12,
    paddingVertical: 12,
    alignItems: "center",
    backgroundColor: temaAtendePro.destaque
  },
  textoBotao: {
    color: temaAtendePro.superficie,
    fontWeight: "700"
  },
  estadoCarregamento: {
    backgroundColor: temaAtendePro.superficieSecundaria,
    borderColor: temaAtendePro.borda,
    borderWidth: 1,
    borderRadius: 12,
    padding: 12,
    gap: 10
  },
  textoCarregamento: {
    color: temaAtendePro.textoSecundario,
    textAlign: "center"
  }
});

function formatarData(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(valor));
}
