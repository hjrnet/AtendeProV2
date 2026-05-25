package br.com.atendepro.modules.nutri.application.result;

import java.time.Instant;

public record ResumoProntuarioNutriProResult(
        long documentos,
        long consultasFuturas,
        long simulacoesPrecificacao,
        long planosAlimentaresAtivos,
        String statusPlanoAlimentar,
        String statusAnamnese,
        String statusAvaliacaoAntropometrica,
        String statusGastoEnergetico,
        String statusExamesLaboratoriais,
        Instant ultimaConsultaEm
) {
}
