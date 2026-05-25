package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.time.Instant;

public record SimulacaoDashboardPrecificacaoResult(
        String nomeProcedimento,
        BigDecimal custoTotal,
        BigDecimal precoRecomendado,
        BigDecimal precoVenda,
        BigDecimal margemRealPercentual,
        Instant atualizadoEm
) {
}
