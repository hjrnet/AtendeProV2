package br.com.atendepro.modules.auth.application.command;

public record RenovarSessaoCommand(String refreshToken) {

    public RenovarSessaoCommand {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("refresh token e obrigatorio");
        }
    }
}
