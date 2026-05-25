package br.com.atendepro.modules.assinatura.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class TrialAssinaturaTest {

    private static final Instant AGORA = Instant.parse("2026-05-25T10:00:00Z");

    @Test
    void deveIniciarTrialComTrintaDiasDeValidade() {
        var trial = TrialAssinatura.iniciar(UUID.randomUUID(), UUID.randomUUID(), AGORA);

        assertThat(trial.status()).isEqualTo(TrialStatus.ATIVO);
        assertThat(trial.expiraEm()).isEqualTo(AGORA.plus(30, ChronoUnit.DAYS));
        assertThat(trial.statusEm(AGORA.plus(29, ChronoUnit.DAYS))).isEqualTo(TrialStatus.ATIVO);
    }

    @Test
    void deveConsiderarTrialExpiradoAposVencimento() {
        var trial = TrialAssinatura.iniciar(UUID.randomUUID(), UUID.randomUUID(), AGORA);

        assertThat(trial.statusEm(AGORA.plus(30, ChronoUnit.DAYS))).isEqualTo(TrialStatus.EXPIRADO);
    }

    @Test
    void deveConverterTrialAtivo() {
        var trial = TrialAssinatura.iniciar(UUID.randomUUID(), UUID.randomUUID(), AGORA);

        var convertido = trial.converter(AGORA.plus(5, ChronoUnit.DAYS));

        assertThat(convertido.status()).isEqualTo(TrialStatus.CONVERTIDO);
        assertThat(convertido.convertidoEm()).isEqualTo(AGORA.plus(5, ChronoUnit.DAYS));
    }

    @Test
    void naoDeveConverterTrialExpirado() {
        var trial = TrialAssinatura.iniciar(UUID.randomUUID(), UUID.randomUUID(), AGORA);

        assertThatThrownBy(() -> trial.converter(AGORA.plus(31, ChronoUnit.DAYS)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("somente trial ativo pode ser convertido");
    }
}
