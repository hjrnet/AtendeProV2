package br.com.atendepro.modules.precificacao.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record SalvarSimulacaoPrecificacaoCommand(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        Integer duracaoMinutos,
        BigDecimal custoInsumos,
        BigDecimal custoSalaPorHora,
        BigDecimal valorHoraProfissional,
        BigDecimal custoDeslocamento,
        BigDecimal custoAlimentacao,
        BigDecimal taxas,
        BigDecimal margemDesejadaPercentual,
        BigDecimal precoVenda
) {
}
