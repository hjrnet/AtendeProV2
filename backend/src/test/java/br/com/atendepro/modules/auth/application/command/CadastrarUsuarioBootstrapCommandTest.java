package br.com.atendepro.modules.auth.application.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;

class CadastrarUsuarioBootstrapCommandTest {

    @Test
    void naoDeveAceitarSenhaFraca() {
        assertThatThrownBy(() -> new CadastrarUsuarioBootstrapCommand(
                "Admin",
                EmailUsuario.de("admin@atendepro.local"),
                "senha",
                Set.of(PerfilAcesso.SUPER_ADMIN)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("senha bootstrap deve ter ao menos 12 caracteres");
    }
}
