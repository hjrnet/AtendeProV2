import { StyleSheet, View } from "react-native";
import {
  CapaPagina,
  Cartao,
  CabecalhoSecao,
  ItemLista
} from "@/components/ui-shell";
import { pacientesEmAcompanhamento } from "@/features/evolucao/evolucao";

const textoRisco = (risco: "bom" | "estavel" | "atenção") => {
  if (risco === "bom") return "Acompanhamento estável";
  if (risco === "atenção") return "Requer ação assistida";
  return "Sob monitoramento";
};

export default function EvolucaoProfissionalMobile() {
  return (
    <CapaPagina titulo="Acompanhamento" subtitulo="Painel de evolução clínica dos pacientes ativos.">
      <CabecalhoSecao titulo="Pacientes em acompanhamento" />
      <View style={estilos.lista}>
        {pacientesEmAcompanhamento.map((paciente) => (
          <ItemLista
            key={paciente.id}
            titulo={paciente.nomePaciente}
            meta={paciente.area}
            descricao={textoRisco(paciente.risco)}
            status={paciente.resumo}
          />
        ))}
      </View>

      <CabecalhoSecao titulo="Observações de gestão" />
      <Cartao titulo="Indicador clínico" descricao="Priorize atendimentos com risco de atenção para resposta no mesmo dia.">
        <ItemLista
          titulo="Pendências altas"
          descricao="3 prontuários aguardam atualização de evolução semanal."
          meta="Revisão recomendada hoje"
        />
        <ItemLista
          titulo="Indicador de qualidade"
          descricao="Concluir retorno dentro de 24h quando houver recados críticos."
          meta="Objetivo da release mobile premium"
        />
      </Cartao>
    </CapaPagina>
  );
}

const estilos = StyleSheet.create({
  lista: {
    gap: 12
  }
});
