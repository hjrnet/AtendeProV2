package br.com.atendepro.modules.beauty.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SimulacaoBeautyProResult(
        UUID id,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        int duracaoMinutos,
        BigDecimal precoVenda,
        BigDecimal custoTotal,
        BigDecimal precoRecomendado,
        BigDecimal lucroEstimado,
        BigDecimal margemRealPercentual,
        String statusMargem,
        String statusRotulo,
        boolean alerta,
        Instant atualizadoEm
) {
}
