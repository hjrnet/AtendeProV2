package br.com.atendepro.modules.spaces.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class OcupacaoSpacesTest {

    private static final UUID EMPRESA_ID = UUID.fromString("7fa4fb4c-9919-4631-9a29-41af4a528e79");
    private static final UUID RECURSO_ID = UUID.fromString("d4f9b4d8-f4f0-4a9e-a7dc-afd7b3847bf9");
    private static final Instant AGORA = Instant.parse("2026-05-25T00:00:00Z");

    @Test
    void deveAgendarOcupacaoReservadaPorPadrao() {
        OcupacaoSpaces ocupacao = OcupacaoSpaces.agendar(
                EMPRESA_ID,
                RECURSO_ID,
                null,
                "Dra. Marina",
                Instant.parse("2026-05-26T12:00:00Z"),
                Instant.parse("2026-05-26T14:00:00Z"),
                null,
                "periodo de teste",
                AGORA
        );

        assertThat(ocupacao.status()).isEqualTo(StatusOcupacaoSpaces.RESERVADA);
        assertThat(ocupacao.nomeParceiro()).isEqualTo("Dra. Marina");
        assertThat(ocupacao.observacao()).isEqualTo("periodo de teste");
    }

    @Test
    void naoDeveAgendarPeriodoInvalido() {
        assertThatThrownBy(() -> OcupacaoSpaces.agendar(
                EMPRESA_ID,
                RECURSO_ID,
                null,
                "Dra. Marina",
                Instant.parse("2026-05-26T14:00:00Z"),
                Instant.parse("2026-05-26T12:00:00Z"),
                StatusOcupacaoSpaces.RESERVADA,
                null,
                AGORA
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("fim da ocupacao spaces deve ser posterior ao inicio");
    }

    @Test
    void naoDeveNascerCancelada() {
        assertThatThrownBy(() -> OcupacaoSpaces.agendar(
                EMPRESA_ID,
                RECURSO_ID,
                null,
                "Dra. Marina",
                Instant.parse("2026-05-26T12:00:00Z"),
                Instant.parse("2026-05-26T14:00:00Z"),
                StatusOcupacaoSpaces.CANCELADA,
                null,
                AGORA
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ocupacao spaces nao pode nascer cancelada");
    }
}
