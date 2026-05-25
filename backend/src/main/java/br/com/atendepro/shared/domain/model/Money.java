package br.com.atendepro.shared.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money implements Comparable<Money> {

    private static final int ESCALA_MONETARIA = 2;
    private static final RoundingMode ARREDONDAMENTO_PADRAO = RoundingMode.HALF_EVEN;

    private final BigDecimal valor;

    private Money(BigDecimal valor) {
        this.valor = Objects.requireNonNull(valor, "valor monetario e obrigatorio")
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    public static Money zero() {
        return new Money(BigDecimal.ZERO);
    }

    public static Money de(BigDecimal valor) {
        return new Money(valor);
    }

    public static Money de(String valor) {
        return new Money(new BigDecimal(valor));
    }

    public Money somar(Money outro) {
        return new Money(valor.add(outro.valor));
    }

    public Money subtrair(Money outro) {
        return new Money(valor.subtract(outro.valor));
    }

    public Money multiplicar(BigDecimal fator) {
        return new Money(valor.multiply(Objects.requireNonNull(fator, "fator e obrigatorio")));
    }

    public boolean ehNegativo() {
        return valor.signum() < 0;
    }

    public boolean ehZero() {
        return valor.signum() == 0;
    }

    public BigDecimal valor() {
        return valor;
    }

    @Override
    public int compareTo(Money outro) {
        return valor.compareTo(outro.valor);
    }

    @Override
    public boolean equals(Object objeto) {
        if (this == objeto) {
            return true;
        }
        if (!(objeto instanceof Money money)) {
            return false;
        }
        return valor.compareTo(money.valor) == 0;
    }

    @Override
    public int hashCode() {
        return valor.stripTrailingZeros().hashCode();
    }

    @Override
    public String toString() {
        return valor.toPlainString();
    }
}
