package br.com.atendepro.modules.plano.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class PlanoAssinaturaTest {

    @Test
    void deveNormalizarCodigoValorEModulosDoPlano() {
        var plano = new PlanoAssinatura(
                UUID.randomUUID(),
                "nutri-pro",
                " Nutri Pro ",
                " Plano para nutricionistas ",
                new BigDecimal("149.999"),
                3,
                200,
                2,
                true,
                Set.of(ModuloPlano.deCodigo("agenda"), ModuloPlano.deCodigo("NUTRI_PRO")),
                Instant.parse("2026-05-25T10:00:00Z"),
                Instant.parse("2026-05-25T10:00:00Z")
        );

        assertThat(plano.codigo()).isEqualTo("NUTRI_PRO");
        assertThat(plano.nome()).isEqualTo("Nutri Pro");
        assertThat(plano.descricao()).isEqualTo("Plano para nutricionistas");
        assertThat(plano.valorMensal()).isEqualByComparingTo("150.00");
        assertThat(plano.modulos()).containsExactlyInAnyOrder(ModuloPlano.AGENDA, ModuloPlano.NUTRI_PRO);
    }

    @Test
    void naoDeveCriarPlanoSemModulos() {
        assertThatThrownBy(() -> new PlanoAssinatura(
                UUID.randomUUID(),
                "START",
                "Start",
                null,
                BigDecimal.ZERO,
                1,
                20,
                1,
                true,
                Set.of(),
                Instant.parse("2026-05-25T10:00:00Z"),
                Instant.parse("2026-05-25T10:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("plano deve possuir ao menos um modulo");
    }
}
