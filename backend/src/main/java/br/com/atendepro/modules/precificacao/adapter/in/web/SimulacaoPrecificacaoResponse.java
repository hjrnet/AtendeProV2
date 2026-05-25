package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.SimulacaoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;

public record SimulacaoPrecificacaoResponse(
        UUID id,
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        int duracaoMinutos,
        BigDecimal custoInsumos,
        BigDecimal custoSalaPorHora,
        BigDecimal valorHoraProfissional,
        BigDecimal custoDeslocamento,
        BigDecimal custoAlimentacao,
        BigDecimal taxas,
        BigDecimal margemDesejadaPercentual,
        BigDecimal precoVenda,
        BigDecimal custoTotal,
        BigDecimal precoMinimo,
        BigDecimal precoRecomendado,
        BigDecimal lucroEstimado,
        BigDecimal margemRealPercentual,
        StatusMargemPrecificacao statusMargem,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static SimulacaoPrecificacaoResponse de(SimulacaoPrecificacaoResult result) {
        return new SimulacaoPrecificacaoResponse(
                result.id(),
                result.empresaId(),
                result.servicoProcedimentoId(),
                result.nomeProcedimento(),
                result.duracaoMinutos(),
                result.custoInsumos(),
                result.custoSalaPorHora(),
                result.valorHoraProfissional(),
                result.custoDeslocamento(),
                result.custoAlimentacao(),
                result.taxas(),
                result.margemDesejadaPercentual(),
                result.precoVenda(),
                result.custoTotal(),
                result.precoMinimo(),
                result.precoRecomendado(),
                result.lucroEstimado(),
                result.margemRealPercentual(),
                result.statusMargem(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
