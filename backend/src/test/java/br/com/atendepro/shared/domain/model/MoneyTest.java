package br.com.atendepro.shared.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void deveSomarESubtrairValoresMonetarios() {
        Money total = Money.de("10.125").somar(Money.de("2.335"));

        assertThat(total).isEqualTo(Money.de("12.46"));
        assertThat(total.subtrair(Money.de("0.46"))).isEqualTo(Money.de("12.00"));
    }

    @Test
    void deveMultiplicarComArredondamentoMonetario() {
        Money resultado = Money.de("100.00").multiplicar(new BigDecimal("0.155"));

        assertThat(resultado).isEqualTo(Money.de("15.50"));
    }
}
