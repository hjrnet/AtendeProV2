package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.CustoRealPrecificacaoResult;

public record CustoRealPrecificacaoResponse(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        int duracaoMinutos,
        BigDecimal precoBaseServico,
        BigDecimal custoInsumos,
        BigDecimal custoSala,
        BigDecimal custoTempoProfissional,
        BigDecimal custoDeslocamento,
        BigDecimal custoAlimentacao,
        BigDecimal taxas,
        List<ItemCustoPrecificacaoResponse> itensCusto,
        BigDecimal custoTotal,
        Instant calculadoEm
) {

    public static CustoRealPrecificacaoResponse de(CustoRealPrecificacaoResult result) {
        return new CustoRealPrecificacaoResponse(
                result.empresaId(),
                result.servicoProcedimentoId(),
                result.nomeProcedimento(),
                result.duracaoMinutos(),
                result.precoBaseServico(),
                result.custoInsumos(),
                result.custoSala(),
                result.custoTempoProfissional(),
                result.custoDeslocamento(),
                result.custoAlimentacao(),
                result.taxas(),
                result.itensCusto().stream().map(ItemCustoPrecificacaoResponse::de).toList(),
                result.custoTotal(),
                result.calculadoEm()
        );
    }
}
