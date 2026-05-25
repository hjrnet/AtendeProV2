package br.com.atendepro.modules.precificacao.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class PrecoRecomendadoPrecificacaoTest {

    @Test
    void deveCalcularPrecoRecomendadoPorMargemDesejada() {
        PrecoRecomendadoPrecificacao precoRecomendado = PrecoRecomendadoPrecificacao.calcular(
                precoMinimo("168.00"),
                new BigDecimal("30.00")
        );

        assertThat(precoRecomendado.precoMinimo().precoMinimo()).isEqualByComparingTo("168.00");
        assertThat(precoRecomendado.margemDesejadaPercentual()).isEqualByComparingTo("30.00");
        assertThat(precoRecomendado.precoRecomendado()).isEqualByComparingTo("240.00");
    }

    @Test
    void naoDeveCalcularComMargemMaiorOuIgualACem() {
        assertThatThrownBy(() -> PrecoRecomendadoPrecificacao.calcular(
                precoMinimo("100.00"),
                new BigDecimal("100.00")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("margem desejada deve ficar entre 0 e 99.99");
    }

    private PrecoMinimoPrecificacao precoMinimo(String custo) {
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                UUID.randomUUID(),
                null,
                "Procedimento",
                60,
                new BigDecimal(custo),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.parse("2026-05-25T00:00:00Z")
        );
        return PrecoMinimoPrecificacao.calcular(custoReal);
    }
}
