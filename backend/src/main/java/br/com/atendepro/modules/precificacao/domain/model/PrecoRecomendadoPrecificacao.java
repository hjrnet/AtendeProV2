package br.com.atendepro.modules.precificacao.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record PrecoRecomendadoPrecificacao(
        PrecoMinimoPrecificacao precoMinimo,
        BigDecimal margemDesejadaPercentual,
        BigDecimal precoRecomendado
) {

    public PrecoRecomendadoPrecificacao {
        if (precoMinimo == null) {
            throw new IllegalArgumentException("preco minimo da precificacao e obrigatorio");
        }
        margemDesejadaPercentual = validarMargem(margemDesejadaPercentual);
        precoRecomendado = calcularPreco(precoMinimo.precoMinimo(), margemDesejadaPercentual);
    }

    public static PrecoRecomendadoPrecificacao calcular(
            PrecoMinimoPrecificacao precoMinimo,
            BigDecimal margemDesejadaPercentual
    ) {
        return new PrecoRecomendadoPrecificacao(precoMinimo, margemDesejadaPercentual, BigDecimal.ZERO);
    }

    private static BigDecimal calcularPreco(BigDecimal custoTotal, BigDecimal margemDesejadaPercentual) {
        BigDecimal fatorMargem = BigDecimal.ONE.subtract(
                margemDesejadaPercentual.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_EVEN)
        );
        return custoTotal.divide(fatorMargem, 2, RoundingMode.CEILING);
    }

    private static BigDecimal validarMargem(BigDecimal margemDesejadaPercentual) {
        if (margemDesejadaPercentual == null
                || margemDesejadaPercentual.signum() < 0
                || margemDesejadaPercentual.compareTo(BigDecimal.valueOf(100)) >= 0) {
            throw new IllegalArgumentException("margem desejada deve ficar entre 0 e 99.99");
        }
        return margemDesejadaPercentual.setScale(2, RoundingMode.HALF_EVEN);
    }
}
