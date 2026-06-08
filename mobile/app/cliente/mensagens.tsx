import { View, Text, StyleSheet } from "react-native";
import {
  CapaPagina,
  CabecalhoSecao,
  ItemLista,
  temaAtendePro
} from "@/components/ui-shell";
import { mensagensCliente } from "@/features/mensagens/mensagens";

const origem = (tipo: "cliente" | "profissional" | "sistema") =>
  tipo === "cliente" ? "Cliente" : tipo === "profissional" ? "Profissional" : "Sistema";

export default function MensagensClienteMobile() {
  return (
    <CapaPagina titulo="Mensagens do cliente" subtitulo="Conversa com profissionais e atualizações do sistema.">
      <CabecalhoSecao titulo="Conversa recente" />
      <View style={estilos.lista}>
        {mensagensCliente.map((mensagem) => (
          <ItemLista
            key={mensagem.id}
            titulo={`${origem(mensagem.origem)}: ${mensagem.remetente}`}
            meta={mensagem.hora}
            descricao={mensagem.texto}
            status={mensagem.naoLida ? "Não lida" : "Lida"}
          />
        ))}
      </View>
      <ItemLista
        titulo="Dica de uso"
        descricao="Mensagens críticas entram no topo com prioridade visual."
      />
      <View style={estilos.alerta}>
        <Text style={estilos.alertaTexto}>A comunicação continua em versão inicial sem envio de mensagens ainda.</Text>
      </View>
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  lista: {
    gap: 12
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
  }
});
