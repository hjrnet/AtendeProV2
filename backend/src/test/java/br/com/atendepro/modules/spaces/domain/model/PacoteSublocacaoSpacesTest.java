package br.com.atendepro.modules.spaces.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class PacoteSublocacaoSpacesTest {

    private static final UUID EMPRESA_ID = UUID.fromString("f1cf1b1b-3cf0-4a1d-8e32-c451408bdbd6");
    private static final UUID RECURSO_ID = UUID.fromString("d10610e1-99f7-4c79-9e85-729115c9cf43");
    private static final Instant AGORA = Instant.parse("2026-05-25T00:00:00Z");

    @Test
    void deveCadastrarPacotePorHora() {
        PacoteSublocacaoSpaces pacote = PacoteSublocacaoSpaces.cadastrar(
                EMPRESA_ID,
                RECURSO_ID,
                "Hora avulsa",
                TipoPacoteSublocacaoSpaces.HORA,
                "Uso avulso da sala",
                new BigDecimal("1.00"),
                new BigDecimal("80.00"),
                BigDecimal.ZERO,
                AGORA
        );

        assertThat(pacote.id()).isNotNull();
        assertThat(pacote.nome()).isEqualTo("Hora avulsa");
        assertThat(pacote.valorFixo()).isEqualByComparingTo("80.00");
        assertThat(pacote.percentualReceita()).isEqualByComparingTo("0.00");
        assertThat(pacote.ativo()).isTrue();
    }

    @Test
    void deveCadastrarPacoteFixoPercentual() {
        PacoteSublocacaoSpaces pacote = PacoteSublocacaoSpaces.cadastrar(
                EMPRESA_ID,
                null,
                "Fixo mais percentual",
                TipoPacoteSublocacaoSpaces.FIXO_PERCENTUAL,
                null,
                new BigDecimal("160.00"),
                new BigDecimal("900.00"),
                new BigDecimal("12.50"),
                AGORA
        );

        assertThat(pacote.valorFixo()).isEqualByComparingTo("900.00");
        assertThat(pacote.percentualReceita()).isEqualByComparingTo("12.50");
    }

    @Test
    void naoDeveCadastrarPacoteHoraSemValorFixo() {
        assertThatThrownBy(() -> PacoteSublocacaoSpaces.cadastrar(
                EMPRESA_ID,
                RECURSO_ID,
                "Hora sem valor",
                TipoPacoteSublocacaoSpaces.HORA,
                null,
                BigDecimal.ONE,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                AGORA
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("valor fixo do pacote de sublocacao deve ser positivo");
    }
}
