package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.SugestoesPrecificacaoAssistidaResult;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;

public record SugestoesPrecificacaoAssistidaResponse(
        UUID simulacaoId,
        UUID empresaId,
        String nomeProcedimento,
        StatusMargemPrecificacao statusMargem,
        BigDecimal custoTotal,
        BigDecimal precoMinimo,
        BigDecimal precoRecomendado,
        BigDecimal precoVenda,
        BigDecimal margemRealPercentual,
        String resumo,
        List<SugestaoPrecificacaoAssistidaResponse> sugestoes,
        Instant geradoEm
) {

    public static SugestoesPrecificacaoAssistidaResponse de(SugestoesPrecificacaoAssistidaResult result) {
        return new SugestoesPrecificacaoAssistidaResponse(
                result.simulacaoId(),
                result.empresaId(),
                result.nomeProcedimento(),
                result.statusMargem(),
                result.custoTotal(),
                result.precoMinimo(),
                result.precoRecomendado(),
                result.precoVenda(),
                result.margemRealPercentual(),
                result.resumo(),
                result.sugestoes().stream().map(SugestaoPrecificacaoAssistidaResponse::de).toList(),
                result.geradoEm()
        );
    }
}
