package br.com.atendepro.modules.auth.adapter.in.web;

import br.com.atendepro.modules.auth.application.command.RenovarSessaoCommand;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @NotBlank(message = "refresh token e obrigatorio")
        String refreshToken
) {

    RenovarSessaoCommand paraCommand() {
        return new RenovarSessaoCommand(refreshToken);
    }
}
