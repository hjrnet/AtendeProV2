package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;

public record SimulacaoPrecificacaoResult(
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

    public static SimulacaoPrecificacaoResult de(SimulacaoPrecificacao simulacao) {
        return new SimulacaoPrecificacaoResult(
                simulacao.id(),
                simulacao.empresaId(),
                simulacao.servicoProcedimentoId(),
                simulacao.nomeProcedimento(),
                simulacao.duracaoMinutos(),
                simulacao.custoInsumos(),
                simulacao.custoSalaPorHora(),
                simulacao.valorHoraProfissional(),
                simulacao.custoDeslocamento(),
                simulacao.custoAlimentacao(),
                simulacao.taxas(),
                simulacao.margemDesejadaPercentual(),
                simulacao.precoVenda(),
                simulacao.custoTotal(),
                simulacao.precoMinimo(),
                simulacao.precoRecomendado(),
                simulacao.lucroEstimado(),
                simulacao.margemRealPercentual(),
                simulacao.statusMargem(),
                simulacao.ativo(),
                simulacao.criadoEm(),
                simulacao.atualizadoEm()
        );
    }
}
