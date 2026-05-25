package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.PrecoRecomendadoPrecificacaoResult;

public record PrecoRecomendadoPrecificacaoResponse(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        int duracaoMinutos,
        BigDecimal precoBaseServico,
        BigDecimal custoTotal,
        BigDecimal precoMinimo,
        BigDecimal margemDesejadaPercentual,
        BigDecimal precoRecomendado,
        List<ItemCustoPrecificacaoResponse> itensCusto,
        Instant calculadoEm
) {

    public static PrecoRecomendadoPrecificacaoResponse de(PrecoRecomendadoPrecificacaoResult result) {
        return new PrecoRecomendadoPrecificacaoResponse(
                result.empresaId(),
                result.servicoProcedimentoId(),
                result.nomeProcedimento(),
                result.duracaoMinutos(),
                result.precoBaseServico(),
                result.custoTotal(),
                result.precoMinimo(),
                result.margemDesejadaPercentual(),
                result.precoRecomendado(),
                result.itensCusto().stream().map(ItemCustoPrecificacaoResponse::de).toList(),
                result.calculadoEm()
        );
    }
}
