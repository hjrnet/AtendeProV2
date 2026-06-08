import { StyleSheet, View } from "react-native";
import { CapaPagina, CabecalhoSecao, ItemLista, temaAtendePro } from "@/components/ui-shell";
import { totalNaoLidas, notificacoes } from "@/features/notificacoes/notificacoes";

export default function NotificacoesMobile() {
  return (
    <CapaPagina titulo="Notificações" subtitulo="Lembretes do dia e eventos importantes do paciente.">
      <CabecalhoSecao titulo={`${totalNaoLidas} não lidas`} />
      <View style={estilos.lista}>
        {notificacoes.map((notificacao) => (
          <ItemLista
            key={notificacao.id}
            titulo={`${notificacao.titulo} • ${notificacao.categoria.toUpperCase()}`}
            meta={notificacao.dataHora}
            descricao={notificacao.descricao}
            status={notificacao.urgente ? "Urgente" : notificacao.lida ? "Lida" : "Pendente"}
          />
        ))}
      </View>

      <ItemLista
        titulo="Canal de eventos"
        descricao="Notificações push ainda estão em modo local para testes de UX nesta release."
        meta="Rascunho operacional"
      />
      <View style={estilos.rodape}>
        <View style={estilos.ponto} />
        <ItemLista
          titulo="Próximo estágio"
          descricao="Integração com Expo Notifications e backend de eventos."
          meta="Implementação da TASK-0805"
        />
      </View>
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  lista: {
    gap: 12
  },
  rodape: {
    marginTop: 12,
    gap: 10
  },
  ponto: {
    borderRadius: 999,
    width: 8,
    height: 8,
    backgroundColor: temaAtendePro.destaque,
    alignSelf: "center"
  }
});
