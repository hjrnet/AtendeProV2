package br.com.atendepro.shared.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

class PeriodoTest {

    @Test
    void deveValidarIntervaloEContemData() {
        Periodo periodo = new Periodo(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 3));

        assertThat(periodo.contem(LocalDate.of(2026, 5, 2))).isTrue();
        assertThat(periodo.totalDiasInclusivo()).isEqualTo(3);
    }

    @Test
    void naoDevePermitirDataFinalAnterior() {
        assertThatThrownBy(() -> new Periodo(LocalDate.of(2026, 5, 3), LocalDate.of(2026, 5, 1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
