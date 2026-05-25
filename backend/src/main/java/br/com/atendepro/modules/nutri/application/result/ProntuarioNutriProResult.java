package br.com.atendepro.modules.nutri.application.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProntuarioNutriProResult(
        UUID empresaId,
        PacienteProntuarioNutriProResult paciente,
        ResumoProntuarioNutriProResult resumo,
        List<AcaoProntuarioNutriProResult> acoesRapidas,
        Instant atualizadoEm
) {
}
