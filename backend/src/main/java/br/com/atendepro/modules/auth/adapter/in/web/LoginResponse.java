package br.com.atendepro.modules.auth.adapter.in.web;

import java.time.Instant;

import br.com.atendepro.modules.auth.application.result.AutenticacaoResult;

public record LoginResponse(
        String accessToken,
        String tipoToken,
        Instant expiraEm,
        UsuarioLoginResponse usuario
) {

    static LoginResponse de(AutenticacaoResult result) {
        return new LoginResponse(
                result.accessToken(),
                result.tipoToken(),
                result.expiraEm(),
                new UsuarioLoginResponse(
                        result.usuario().id(),
                        result.usuario().nome(),
                        result.usuario().email(),
                        result.usuario().perfis()
                )
        );
    }
}
