package br.com.atendepro.modules.spaces.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CustoHoraSpacesTest {

    @Test
    void deveCalcularCustoPorHoraRateandoCustoFixoPelaDisponibilidade() {
        CustoHoraSpaces custoHora = CustoHoraSpaces.calcular(
                UUID.fromString("e19448a7-e853-4402-9e05-7f3477a2da4d"),
                "Sala premium",
                TipoRecursoSpaces.SALA,
                new BigDecimal("2400.00"),
                20,
                new BigDecimal("6.00")
        );

        assertThat(custoHora.horasDisponiveisMes()).isEqualByComparingTo("120.00");
        assertThat(custoHora.custoHora()).isEqualByComparingTo("20.00");
    }

    @Test
    void naoDeveCalcularComDisponibilidadeZerada() {
        assertThatThrownBy(() -> CustoHoraSpaces.calcular(
                null,
                null,
                null,
                new BigDecimal("2400.00"),
                0,
                new BigDecimal("6.00")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("dias disponiveis do espaco devem ser positivos");
    }
}
