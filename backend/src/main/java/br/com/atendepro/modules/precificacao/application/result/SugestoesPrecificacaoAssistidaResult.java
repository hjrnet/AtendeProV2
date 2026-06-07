package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;

public record SugestoesPrecificacaoAssistidaResult(
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
        List<SugestaoPrecificacaoAssistidaResult> sugestoes,
        Instant geradoEm
) {
}
