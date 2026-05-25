package br.com.atendepro.modules.auth.adapter.in.web;

import br.com.atendepro.modules.auth.application.command.SolicitarRecuperacaoSenhaCommand;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitarRecuperacaoSenhaRequest(
        @NotBlank(message = "email e obrigatorio")
        @Email(message = "email invalido")
        String email
) {

    SolicitarRecuperacaoSenhaCommand paraCommand() {
        return new SolicitarRecuperacaoSenhaCommand(EmailUsuario.de(email));
    }
}
