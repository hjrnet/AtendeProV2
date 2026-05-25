package br.com.atendepro.modules.nutri.application.result;

public record DadosProntuarioNutriProResult(
        PacienteProntuarioNutriProResult paciente,
        ResumoProntuarioNutriProResult resumo
) {
}
