package br.com.atendepro.shared.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PercentualTest {

    @Test
    void deveAplicarPercentualSobreDinheiro() {
        Money resultado = Percentual.de("12.5").aplicarEm(Money.de("200.00"));

        assertThat(resultado).isEqualTo(Money.de("25.00"));
    }
}
