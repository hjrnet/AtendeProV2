package br.com.atendepro.shared.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Percentual(BigDecimal valor) {

    private static final int ESCALA_PERCENTUAL = 4;

    public Percentual {
        Objects.requireNonNull(valor, "valor percentual e obrigatorio");
        valor = valor.setScale(ESCALA_PERCENTUAL, RoundingMode.HALF_EVEN);
    }

    public static Percentual de(BigDecimal valor) {
        return new Percentual(valor);
    }

    public static Percentual de(String valor) {
        return new Percentual(new BigDecimal(valor));
    }

    public Money aplicarEm(Money base) {
        BigDecimal fator = valor.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_EVEN);
        return base.multiplicar(fator);
    }
}
