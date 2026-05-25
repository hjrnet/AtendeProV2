package br.com.atendepro.modules.auth.application.result;

import java.time.Instant;

public record AutenticacaoResult(
        String accessToken,
        String refreshToken,
        String tipoToken,
        Instant expiraEm,
        UsuarioAutenticadoResult usuario
) {
}
