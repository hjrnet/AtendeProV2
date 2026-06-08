import { StyleSheet, Text, View } from "react-native";
import {
  CapaPagina,
  Cartao,
  CabecalhoSecao,
  ItemLista,
  temaAtendePro
} from "@/components/ui-shell";
import { mensagensPendentesProfissional } from "@/features/mensagens/mensagens";

export default function MensagensProfissionalMobile() {
  return (
    <CapaPagina titulo="Mensagens" subtitulo="Fila de comunicação com pacientes e equipe interna.">
      <CabecalhoSecao titulo="Pendentes" />
      <View style={estilos.lista}>
        {mensagensPendentesProfissional.map((mensagem) => (
          <ItemLista
            key={mensagem.id}
            titulo={mensagem.remetente}
            meta={mensagem.hora}
            descricao={mensagem.texto}
            status={mensagem.naoLida ? "Pendente" : "Lido"}
          />
        ))}
      </View>
      <CabecalhoSecao titulo="Atalhos" />
      <Cartao titulo="Ações rápidas" descricao="Acompanhe e retorne com confirmação para manter fluxo vivo.">
        <Text style={estilos.acao}>Responder</Text>
        <Text style={estilos.acao}>Agendar retorno</Text>
        <Text style={estilos.acao}>Marcar revisão</Text>
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
  }
});
