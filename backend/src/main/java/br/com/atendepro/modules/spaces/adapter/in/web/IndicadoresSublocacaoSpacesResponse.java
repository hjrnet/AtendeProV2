package br.com.atendepro.modules.spaces.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.IndicadoresSublocacaoSpacesResult;

public record IndicadoresSublocacaoSpacesResponse(
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

    static IndicadoresSublocacaoSpacesResponse de(IndicadoresSublocacaoSpacesResult result) {
        return new IndicadoresSublocacaoSpacesResponse(
                result.empresaId(),
                result.periodoInicio(),
                result.periodoFim(),
                result.totalRecursos(),
                result.recursosAtivos(),
                result.pacotesAtivos(),
                result.ocupacoesReservadas(),
                result.ocupacoesConfirmadas(),
                result.ocupacoesCanceladas(),
                result.horasOcupadasMes(),
                result.receitaFixaPrevistaMes(),
                result.taxaOcupacaoMesPercentual()
        );
    }
}
