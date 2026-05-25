package br.com.atendepro.modules.auth.application.command;

import br.com.atendepro.modules.auth.domain.model.EmailUsuario;

public record AutenticarUsuarioCommand(EmailUsuario email, String senha) {

    public AutenticarUsuarioCommand {
        if (senha == null || senha.isBlank()) {
            throw new IllegalArgumentException("senha e obrigatoria");
        }
    }
}
