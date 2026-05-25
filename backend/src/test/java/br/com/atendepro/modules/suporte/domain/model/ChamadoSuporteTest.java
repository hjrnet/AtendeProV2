package br.com.atendepro.modules.suporte.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ChamadoSuporteTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b5a5e7ce-a4de-4565-a830-17a608f9903d");
    private static final Instant AGORA = Instant.parse("2026-05-25T12:00:00Z");

    @Test
    void deveAbrirChamadoComStatusInicialEPrioridadeMediaQuandoNaoInformada() {
        ChamadoSuporte chamado = ChamadoSuporte.abrir(
                EMPRESA_ID,
                UUID.randomUUID(),
                " Karol Demo ",
                " karol@atendepro.local ",
                " Preciso de ajuda ",
                " Dashboard nao atualizou ",
                null,
                " dashboard ",
                AGORA
        );

        assertThat(chamado.status()).isEqualTo(StatusChamadoSuporte.ABERTO);
        assertThat(chamado.prioridade()).isEqualTo(PrioridadeChamadoSuporte.MEDIA);
        assertThat(chamado.titulo()).isEqualTo("Preciso de ajuda");
        assertThat(chamado.categoria()).isEqualTo("dashboard");
    }

    @Test
    void deveAlterarStatusEPrioridadeDaTriagem() {
        Instant atualizadoEm = Instant.parse("2026-05-25T13:00:00Z");
        ChamadoSuporte chamado = ChamadoSuporte.abrir(
                EMPRESA_ID,
                null,
                "Karol",
                "karol@atendepro.local",
                "Duvida",
                "Preciso de ajuda.",
                PrioridadeChamadoSuporte.MEDIA,
                null,
                AGORA
        );

        ChamadoSuporte atualizado = chamado.alterarTriagem(
                StatusChamadoSuporte.EM_ATENDIMENTO,
                PrioridadeChamadoSuporte.CRITICA,
                atualizadoEm
        );

        assertThat(atualizado.status()).isEqualTo(StatusChamadoSuporte.EM_ATENDIMENTO);
        assertThat(atualizado.prioridade()).isEqualTo(PrioridadeChamadoSuporte.CRITICA);
        assertThat(atualizado.atualizadoEm()).isEqualTo(atualizadoEm);
        assertThat(atualizado.criadoEm()).isEqualTo(AGORA);
    }

    @Test
    void naoDeveAbrirChamadoSemTitulo() {
        assertThatThrownBy(() -> ChamadoSuporte.abrir(
                EMPRESA_ID,
                null,
                null,
                null,
                " ",
                "Descricao",
                PrioridadeChamadoSuporte.ALTA,
                null,
                AGORA
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("titulo do chamado e obrigatorio");
    }
}
