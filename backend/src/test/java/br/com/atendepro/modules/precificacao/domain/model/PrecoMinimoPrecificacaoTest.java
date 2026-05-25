package br.com.atendepro.modules.precificacao.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class PrecoMinimoPrecificacaoTest {

    @Test
    void deveCalcularPrecoMinimoIgualAoCustoRealTotal() {
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                UUID.randomUUID(),
                null,
                "Procedimento",
                60,
                new BigDecimal("30.00"),
                new BigDecimal("40.00"),
                new BigDecimal("80.00"),
                new BigDecimal("10.00"),
                new BigDecimal("5.00"),
                new BigDecimal("3.00"),
                Instant.parse("2026-05-25T00:00:00Z")
        );

        PrecoMinimoPrecificacao precoMinimo = PrecoMinimoPrecificacao.calcular(custoReal);

        assertThat(precoMinimo.precoMinimo()).isEqualByComparingTo("168.00");
        assertThat(precoMinimo.precoMinimo()).isEqualByComparingTo(custoReal.custoTotal());
    }

    @Test
    void naoDeveCalcularSemCustoReal() {
        assertThatThrownBy(() -> PrecoMinimoPrecificacao.calcular(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("custo real da precificacao e obrigatorio");
    }
}
