package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.CalculoPrecificacaoBaseResult;

public record CalculoPrecificacaoBaseResponse(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        Integer duracaoMinutosServico,
        BigDecimal precoBaseServico,
        List<ItemCustoPrecificacaoResponse> itensCusto,
        BigDecimal custoTotal,
        Instant calculadoEm
) {

    public static CalculoPrecificacaoBaseResponse de(CalculoPrecificacaoBaseResult result) {
        return new CalculoPrecificacaoBaseResponse(
                result.empresaId(),
                result.servicoProcedimentoId(),
                result.nomeProcedimento(),
                result.duracaoMinutosServico(),
                result.precoBaseServico(),
                result.itensCusto().stream().map(ItemCustoPrecificacaoResponse::de).toList(),
                result.custoTotal(),
                result.calculadoEm()
        );
    }
}
