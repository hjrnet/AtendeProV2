package br.com.atendepro.modules.suporte.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class MensagemChamadoSuporteTest {

    @Test
    void deveRegistrarMensagemComTextoNormalizado() {
        UUID chamadoId = UUID.randomUUID();

        MensagemChamadoSuporte mensagem = MensagemChamadoSuporte.registrar(
                chamadoId,
                UUID.randomUUID(),
                " Suporte Demo ",
                OrigemMensagemChamadoSuporte.SUPORTE,
                " Vamos analisar seu caso. ",
                Instant.parse("2026-05-25T12:00:00Z")
        );

        assertThat(mensagem.chamadoId()).isEqualTo(chamadoId);
        assertThat(mensagem.autorNome()).isEqualTo("Suporte Demo");
        assertThat(mensagem.mensagem()).isEqualTo("Vamos analisar seu caso.");
    }

    @Test
    void naoDeveRegistrarMensagemVazia() {
        assertThatThrownBy(() -> MensagemChamadoSuporte.registrar(
                UUID.randomUUID(),
                null,
                null,
                OrigemMensagemChamadoSuporte.CLIENTE,
                " ",
                Instant.parse("2026-05-25T12:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("mensagem do chamado e obrigatoria");
    }
}
