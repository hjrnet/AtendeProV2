package br.com.atendepro.modules.auth.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class EmailUsuarioTest {

    @Test
    void deveNormalizarEmail() {
        EmailUsuario email = EmailUsuario.de(" Admin@AtendePro.COM ");

        assertThat(email.valor()).isEqualTo("admin@atendepro.com");
    }

    @Test
    void naoDeveAceitarEmailInvalido() {
        assertThatThrownBy(() -> EmailUsuario.de("email-invalido"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email invalido");
    }
}
