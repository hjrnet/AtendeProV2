package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.PrecoMinimoPrecificacaoResult;

public record PrecoMinimoPrecificacaoResponse(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        int duracaoMinutos,
        BigDecimal precoBaseServico,
        BigDecimal custoTotal,
        BigDecimal precoMinimo,
        List<ItemCustoPrecificacaoResponse> itensCusto,
        Instant calculadoEm
) {

    public static PrecoMinimoPrecificacaoResponse de(PrecoMinimoPrecificacaoResult result) {
        return new PrecoMinimoPrecificacaoResponse(
                result.empresaId(),
                result.servicoProcedimentoId(),
                result.nomeProcedimento(),
                result.duracaoMinutos(),
                result.precoBaseServico(),
                result.custoTotal(),
                result.precoMinimo(),
                result.itensCusto().stream().map(ItemCustoPrecificacaoResponse::de).toList(),
                result.calculadoEm()
        );
    }
}
