package br.com.atendepro.modules.beauty.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.SimulacaoBeautyProResult;

public record SimulacaoBeautyProResponse(
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
    public static SimulacaoBeautyProResponse de(SimulacaoBeautyProResult result) {
        return new SimulacaoBeautyProResponse(
                result.id(),
                result.servicoProcedimentoId(),
                result.nomeProcedimento(),
                result.duracaoMinutos(),
                result.precoVenda(),
                result.custoTotal(),
                result.precoRecomendado(),
                result.lucroEstimado(),
                result.margemRealPercentual(),
                result.statusMargem(),
                result.statusRotulo(),
                result.alerta(),
                result.atualizadoEm()
        );
    }
}
