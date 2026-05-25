package br.com.atendepro.modules.precificacao.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record PrecoMinimoPrecificacao(
        CustoRealPrecificacao custoReal,
        BigDecimal precoMinimo
) {

    public PrecoMinimoPrecificacao {
        if (custoReal == null) {
            throw new IllegalArgumentException("custo real da precificacao e obrigatorio");
        }
        precoMinimo = custoReal.custoTotal().setScale(2, RoundingMode.HALF_EVEN);
    }

    public static PrecoMinimoPrecificacao calcular(CustoRealPrecificacao custoReal) {
        return new PrecoMinimoPrecificacao(custoReal, BigDecimal.ZERO);
    }
}
