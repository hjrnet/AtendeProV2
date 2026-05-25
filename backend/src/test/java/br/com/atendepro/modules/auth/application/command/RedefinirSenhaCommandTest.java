package br.com.atendepro.modules.auth.application.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RedefinirSenhaCommandTest {

    @Test
    void naoDeveAceitarNovaSenhaFraca() {
        assertThatThrownBy(() -> new RedefinirSenhaCommand("token", "fraca"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("nova senha deve ter ao menos 12 caracteres");
    }
}
