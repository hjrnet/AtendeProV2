package br.com.atendepro.modules.agenda.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CompromissoAgendaTest {

    @Test
    void deveAgendarCompromissoPorProfissionalESala() {
        CompromissoAgenda compromisso = CompromissoAgenda.agendar(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Dra Ana",
                "Sala 1",
                TipoCompromisso.ATENDIMENTO,
                Instant.parse("2026-05-25T12:00:00Z"),
                Instant.parse("2026-05-25T13:00:00Z"),
                "observacao",
                Instant.parse("2026-05-25T00:00:00Z")
        );

        assertThat(compromisso.status()).isEqualTo(AgendaStatus.AGENDADO);
        assertThat(compromisso.sala()).isEqualTo("Sala 1");
    }

    @Test
    void naoDeveAgendarComFimAntesDoInicio() {
        assertThatThrownBy(() -> CompromissoAgenda.agendar(
                UUID.randomUUID(),
                null,
                UUID.randomUUID(),
                "Dra Ana",
                null,
                TipoCompromisso.ATENDIMENTO,
                Instant.parse("2026-05-25T13:00:00Z"),
                Instant.parse("2026-05-25T12:00:00Z"),
                null,
                Instant.parse("2026-05-25T00:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("fim do compromisso deve ser posterior ao inicio");
    }
}
