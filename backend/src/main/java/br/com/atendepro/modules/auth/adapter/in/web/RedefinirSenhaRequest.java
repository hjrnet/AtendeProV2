package br.com.atendepro.modules.auth.adapter.in.web;

import br.com.atendepro.modules.auth.application.command.RedefinirSenhaCommand;
import jakarta.validation.constraints.NotBlank;

public record RedefinirSenhaRequest(
        @NotBlank(message = "token e obrigatorio")
        String token,

        @NotBlank(message = "nova senha e obrigatoria")
        String novaSenha
) {

    RedefinirSenhaCommand paraCommand() {
        return new RedefinirSenhaCommand(token, novaSenha);
    }
}
