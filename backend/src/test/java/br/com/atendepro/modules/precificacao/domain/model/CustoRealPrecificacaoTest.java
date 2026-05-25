package br.com.atendepro.modules.precificacao.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CustoRealPrecificacaoTest {

    @Test
    void deveCalcularCustoRealPorDuracaoEComponentes() {
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Consulta Nutricional",
                90,
                new BigDecimal("45.00"),
                new BigDecimal("60.00"),
                new BigDecimal("120.00"),
                new BigDecimal("25.00"),
                new BigDecimal("15.00"),
                new BigDecimal("10.00"),
                Instant.parse("2026-05-25T00:00:00Z")
        );

        assertThat(custoReal.custoSala()).isEqualByComparingTo("90.00");
        assertThat(custoReal.custoTempoProfissional()).isEqualByComparingTo("180.00");
        assertThat(custoReal.custoTotal()).isEqualByComparingTo("365.00");
        assertThat(custoReal.calculoBase().itensCusto()).hasSize(6);
    }

    @Test
    void naoDeveCalcularComDuracaoInvalida() {
        assertThatThrownBy(() -> CustoRealPrecificacao.calcular(
                UUID.randomUUID(),
                null,
                "Procedimento",
                0,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.parse("2026-05-25T00:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("duracao do procedimento deve ser positiva");
    }

    @Test
    void naoDeveAceitarTaxasNegativas() {
        assertThatThrownBy(() -> CustoRealPrecificacao.calcular(
                UUID.randomUUID(),
                null,
                "Procedimento",
                60,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("-0.01"),
                Instant.parse("2026-05-25T00:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("taxas nao pode ser negativo");
    }
}
