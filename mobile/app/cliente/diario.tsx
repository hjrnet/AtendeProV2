import { useState } from "react";
import { Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { CapaPagina, Cartao, CabecalhoSecao, ItemLista, temaAtendePro } from "@/components/ui-shell";
import { RegistroDiario, registros as registrosIniciais, TipoRegistro } from "@/features/diario/diario";

export default function DiarioClienteMobile() {
  const [textoNovoRegistro, setTextoNovoRegistro] = useState("");
  const [registros, setRegistros] = useState<RegistroDiario[]>(registrosIniciais);

  const adicionarRegistro = () => {
    if (!textoNovoRegistro.trim()) {
      return;
    }

    setRegistros((estadoAnterior) => [
      {
        id: `reg-${estadoAnterior.length + 10}`,
        titulo: "Novo registro",
        autor: "Cliente",
        data: "Hoje",
        hora: "Agora",
        tipo: "recado" as TipoRegistro,
        mensagem: textoNovoRegistro.trim()
      },
      ...estadoAnterior
    ]);
    setTextoNovoRegistro("");
  };

  return (
    <CapaPagina titulo="Diário e mensagens" subtitulo="Registro contínuo entre equipe e paciente.">
      <CabecalhoSecao titulo="Composição rápida" />
      <Cartao titulo="Adicionar recado" descricao="Anote evolução, dúvidas e observações.">
        <TextInput
          style={estilos.input}
          value={textoNovoRegistro}
          onChangeText={setTextoNovoRegistro}
          placeholder="Digite uma observação"
          placeholderTextColor={temaAtendePro.textoSecundario}
        />
        <Pressable style={estilos.botao} onPress={adicionarRegistro}>
          <Text style={estilos.textoBotao}>Adicionar no diário</Text>
        </Pressable>
      </Cartao>

      <CabecalhoSecao titulo="Últimos registros" />
      <View style={estilos.registros}>
        {registros.map((registro) => (
          <ItemLista
            key={registro.id}
            titulo={`${registro.titulo} · ${registro.tipo}`}
            meta={`${registro.autor} • ${registro.data} ${registro.hora}`}
            descricao={registro.mensagem}
          />
        ))}
      </View>
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  registros: {
    gap: 12
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
  }
});
