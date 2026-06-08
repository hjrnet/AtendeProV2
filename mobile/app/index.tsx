import { Link } from "expo-router";
import { StyleSheet, Text, View } from "react-native";
import {
  CapaPagina,
  CabecalhoSecao,
  Cartao,
  EstadoVazio,
  GridCards,
  temaAtendePro
} from "@/components/ui-shell";

const secoesIniciais = [
  {
    titulo: "Entrar",
    rota: "/auth",
    descricao: "Acesso inicial para perfis cliente e profissional."
  },
  {
    titulo: "Área do Cliente",
    rota: "/cliente",
    descricao: "Agenda, documentos, diário, fotos e mensagens."
  },
  {
    titulo: "Área do Profissional",
    rota: "/profissional",
    descricao: "Agenda do dia, mensagens e acompanhamentos."
  },
  {
    titulo: "Notificações",
    rota: "/notificacoes",
    descricao: "Lembretes e eventos da rotina."
  }
];

export default function TelaInicialMobile() {
  return (
    <CapaPagina
      titulo="AtendePro"
      subtitulo="Aplicativo premium para rotina clínica: agenda, comunicação e documentos em um só lugar."
    >
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
      <Cartao titulo="Status da release" descricao="R8 com foco mobile está em fase de experiência premium e funcionalidades de ponta-a-ponta.">
        <EstadoVazio texto="Próximo passo: validar fluxo com usuários antes de integrar o backend real." />
      </Cartao>
      <View style={estilos.chamada}>
        <Text style={estilos.chamadaTexto}>Ajuste feito para experiência mobile de alto acabamento (TASK-0806).</Text>
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
