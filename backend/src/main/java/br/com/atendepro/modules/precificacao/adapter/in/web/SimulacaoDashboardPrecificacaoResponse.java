package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.atendepro.modules.precificacao.application.result.SimulacaoDashboardPrecificacaoResult;

public record SimulacaoDashboardPrecificacaoResponse(
        String nomeProcedimento,
        BigDecimal custoTotal,
        BigDecimal precoRecomendado,
        BigDecimal precoVenda,
        BigDecimal margemRealPercentual,
        Instant atualizadoEm
) {

    public static SimulacaoDashboardPrecificacaoResponse de(SimulacaoDashboardPrecificacaoResult result) {
        return new SimulacaoDashboardPrecificacaoResponse(
                result.nomeProcedimento(),
                result.custoTotal(),
                result.precoRecomendado(),
                result.precoVenda(),
                result.margemRealPercentual(),
                result.atualizadoEm()
        );
    }
}
