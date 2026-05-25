package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.MargemLucroPrecificacaoResult;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;

public record MargemLucroPrecificacaoResponse(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        int duracaoMinutos,
        BigDecimal precoBaseServico,
        BigDecimal custoTotal,
        BigDecimal precoMinimo,
        BigDecimal precoVenda,
        BigDecimal lucroEstimado,
        BigDecimal margemRealPercentual,
        StatusMargemPrecificacao status,
        List<AlertaPrecificacaoResponse> alertas,
        List<ItemCustoPrecificacaoResponse> itensCusto,
        Instant calculadoEm
) {

    public static MargemLucroPrecificacaoResponse de(MargemLucroPrecificacaoResult result) {
        return new MargemLucroPrecificacaoResponse(
                result.empresaId(),
                result.servicoProcedimentoId(),
                result.nomeProcedimento(),
                result.duracaoMinutos(),
                result.precoBaseServico(),
                result.custoTotal(),
                result.precoMinimo(),
                result.precoVenda(),
                result.lucroEstimado(),
                result.margemRealPercentual(),
                result.status(),
                result.alertas().stream().map(AlertaPrecificacaoResponse::de).toList(),
                result.itensCusto().stream().map(ItemCustoPrecificacaoResponse::de).toList(),
                result.calculadoEm()
        );
    }
}
