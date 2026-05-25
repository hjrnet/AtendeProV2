package br.com.atendepro.modules.auth.application.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.domain.model.EmailUsuario;

class CadastrarAdministradorEmpresaCommandTest {

    @Test
    void naoDeveAceitarSenhaFraca() {
        assertThatThrownBy(() -> new CadastrarAdministradorEmpresaCommand(
                UUID.randomUUID(),
                "Admin Empresa",
                EmailUsuario.de("admin.empresa@atendepro.local"),
                "fraca"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("senha do administrador deve ter ao menos 12 caracteres");
    }
}
