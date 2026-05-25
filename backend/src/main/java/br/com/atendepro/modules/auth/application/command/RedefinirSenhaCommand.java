package br.com.atendepro.modules.auth.application.command;

import br.com.atendepro.modules.auth.domain.model.PoliticaSenha;

public record RedefinirSenhaCommand(String token, String novaSenha) {

    public RedefinirSenhaCommand {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token de recuperacao e obrigatorio");
        }
        PoliticaSenha.validarSenhaForte(novaSenha, "nova senha");
    }
}
