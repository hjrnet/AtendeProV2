package br.com.atendepro.modules.auth.application.command;

import br.com.atendepro.modules.auth.domain.model.EmailUsuario;

public record SolicitarRecuperacaoSenhaCommand(EmailUsuario email) {

    public SolicitarRecuperacaoSenhaCommand {
        if (email == null) {
            throw new IllegalArgumentException("email e obrigatorio");
        }
    }
}
