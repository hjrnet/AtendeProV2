package br.com.atendepro.modules.auth.adapter.in.web;

import br.com.atendepro.modules.auth.application.command.AutenticarUsuarioCommand;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "email e obrigatorio")
        @Email(message = "email invalido")
        String email,

        @NotBlank(message = "senha e obrigatoria")
        String senha
) {

    AutenticarUsuarioCommand paraCommand() {
        return new AutenticarUsuarioCommand(EmailUsuario.de(email), senha);
    }
}
