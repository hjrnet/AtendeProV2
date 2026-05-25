package br.com.atendepro.modules.precificacao.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CalculoPrecificacao(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        List<ItemCustoPrecificacao> itensCusto,
        BigDecimal custoTotal,
        Instant calculadoEm
) {

    public CalculoPrecificacao {
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa da precificacao e obrigatoria");
        }
        if (nomeProcedimento == null || nomeProcedimento.isBlank()) {
            throw new IllegalArgumentException("nome do procedimento e obrigatorio");
        }
        if (itensCusto == null || itensCusto.isEmpty()) {
            throw new IllegalArgumentException("itens de custo da precificacao sao obrigatorios");
        }
        if (calculadoEm == null) {
            throw new IllegalArgumentException("data do calculo de precificacao e obrigatoria");
        }
        nomeProcedimento = nomeProcedimento.trim();
        itensCusto = List.copyOf(itensCusto);
        custoTotal = calcularTotal(itensCusto);
    }

    public static CalculoPrecificacao calcular(
            UUID empresaId,
            UUID servicoProcedimentoId,
            String nomeProcedimento,
            List<ItemCustoPrecificacao> itensCusto,
            Instant calculadoEm
    ) {
        return new CalculoPrecificacao(
                empresaId,
                servicoProcedimentoId,
                nomeProcedimento,
                itensCusto,
                BigDecimal.ZERO,
                calculadoEm
        );
    }

    private static BigDecimal calcularTotal(List<ItemCustoPrecificacao> itensCusto) {
        return itensCusto.stream()
                .map(ItemCustoPrecificacao::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_EVEN);
    }
}
