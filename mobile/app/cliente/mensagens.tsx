import { useEffect, useState } from "react";
import { ActivityIndicator, Pressable, Text, TextInput, View, StyleSheet } from "react-native";
import {
  CapaPagina,
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

const origem = (tipo: "cliente" | "profissional" | "sistema") =>
  tipo === "cliente" ? "Cliente" : tipo === "profissional" ? "Profissional" : "Sistema";

export default function MensagensClienteMobile() {
  const [mensagens, setMensagens] = useState<MensagemNutriApi[]>([]);
  const [texto, setTexto] = useState("");
  const [empresaId, setEmpresaId] = useState<string | null>(null);
  const [pacienteId, setPacienteId] = useState<string | null>(null);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState("");

  const carregar = async () => {
    setCarregando(true);
    setErro("");

    try {
      const sessao = await carregarSessaoAutenticada();
      const contextoEmpresa = sessao?.usuario.empresaId ?? null;
      setEmpresaId(contextoEmpresa);

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

  const enviar = async () => {
    if (!texto.trim() || !pacienteId) {
      return;
    }

    try {
      const nova = await enviarMensagemNutri({
        empresaId,
        pacienteId,
        remetenteTipo: "PACIENTE",
        remetenteNome: "Paciente",
        texto: texto.trim(),
        contexto: "App do paciente"
      });
      setMensagens((estadoAtual) => [nova, ...estadoAtual]);
      setTexto("");
    } catch (falha) {
      setErro(falha instanceof Error ? falha.message : "Não foi possível enviar mensagem.");
    }
  };

  return (
    <CapaPagina titulo="Mensagens do cliente" subtitulo="Conversa com profissionais e atualizações do sistema.">
      {carregando ? (
        <View style={estilos.estadoCarregamento}>
          <ActivityIndicator size="small" color={temaAtendePro.destaque} />
          <Text style={estilos.alertaTexto}>Carregando mensagens...</Text>
        </View>
      ) : erro ? (
        <EstadoVazio texto={erro} />
      ) : null}

      <CabecalhoSecao titulo="Novo recado" />
      <View style={estilos.compositor}>
        <TextInput
          style={estilos.input}
          value={texto}
          onChangeText={setTexto}
          placeholder="Digite uma dúvida ou atualização"
          placeholderTextColor={temaAtendePro.textoSecundario}
        />
        <Pressable style={estilos.botao} onPress={enviar}>
          <Text style={estilos.textoBotao}>Enviar</Text>
        </Pressable>
      </View>

      <CabecalhoSecao titulo="Conversa recente" />
      <View style={estilos.lista}>
        {mensagens.length === 0 ? <EstadoVazio texto="Nenhum recado trocado ainda." /> : null}
        {mensagens.map((mensagem) => (
          <ItemLista
            key={mensagem.id}
            titulo={`${origem(tipoOrigem(mensagem.remetenteTipo))}: ${mensagem.remetenteNome}`}
            meta={formatarData(mensagem.enviadaEm)}
            descricao={mensagem.texto}
            status={mensagem.lidaPeloPaciente ? "Lida" : "Não lida"}
          />
        ))}
      </View>
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  lista: {
    gap: 12
  },
  compositor: {
    borderRadius: 12,
    borderWidth: 1,
    borderColor: temaAtendePro.borda,
    backgroundColor: temaAtendePro.superficie,
    padding: 12,
    gap: 10
  },
  input: {
    borderWidth: 1,
    borderColor: temaAtendePro.borda,
    borderRadius: 12,
    padding: 12,
    backgroundColor: temaAtendePro.superficieSecundaria,
    color: temaAtendePro.texto
  },
  botao: {
    borderRadius: 12,
    paddingVertical: 12,
    alignItems: "center",
    backgroundColor: temaAtendePro.destaque
  },
  textoBotao: {
    color: temaAtendePro.superficie,
    fontWeight: "700"
  },
  alerta: {
    marginTop: 12,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: temaAtendePro.aviso,
    backgroundColor: "#fff7ed",
    padding: 12
  },
  alertaTexto: {
    color: temaAtendePro.texto
  },
  estadoCarregamento: {
    borderRadius: 12,
    borderWidth: 1,
    borderColor: temaAtendePro.borda,
    backgroundColor: temaAtendePro.superficieSecundaria,
    padding: 12,
    gap: 8
  }
});

function tipoOrigem(tipo: MensagemNutriApi["remetenteTipo"]) {
  if (tipo === "PACIENTE") {
    return "cliente";
  }
  if (tipo === "PROFISSIONAL") {
    return "profissional";
  }
  return "sistema";
}

function formatarData(valor: string) {
  return new Intl.DateTimeFormat("pt-BR", {
    dateStyle: "short",
    timeStyle: "short"
  }).format(new Date(valor));
}
