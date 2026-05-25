package br.com.atendepro.modules.spaces.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record IndicadoresSublocacaoSpacesResult(
        UUID empresaId,
        Instant periodoInicio,
        Instant periodoFim,
        long totalRecursos,
        long recursosAtivos,
        long pacotesAtivos,
        long ocupacoesReservadas,
        long ocupacoesConfirmadas,
        long ocupacoesCanceladas,
        BigDecimal horasOcupadasMes,
        BigDecimal receitaFixaPrevistaMes,
        BigDecimal taxaOcupacaoMesPercentual
) {
}
