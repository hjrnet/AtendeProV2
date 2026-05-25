package br.com.atendepro.modules.nutri.application.result;

import java.util.List;

public record MetricasNutriProResult(
        String empresaNome,
        long pacientesAtivos,
        long agendaHoje,
        long agendaProximos7Dias,
        long servicosNutriAtivos,
        long documentosNutri,
        long simulacoesPrecificacao,
        long simulacoesEmAlerta,
        long planosAlimentaresAtivos,
        List<PacienteNutriResumoResult> pacientesRecentes
) {
}
