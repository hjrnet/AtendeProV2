package br.com.atendepro.modules.auth.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.auth.application.port.out.GerarRefreshTokenPort;
import br.com.atendepro.modules.auth.application.port.out.GerarTokenAutenticacaoPort;
import br.com.atendepro.modules.auth.application.port.out.RefreshTokenGerado;
import br.com.atendepro.modules.auth.application.port.out.SalvarRefreshTokenPort;
import br.com.atendepro.modules.auth.application.port.out.TokenGerado;
import br.com.atendepro.modules.auth.application.result.AutenticacaoResult;
import br.com.atendepro.modules.auth.application.result.UsuarioAutenticadoResult;
import br.com.atendepro.modules.auth.domain.model.RefreshTokenAutenticacao;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

final class RefreshTokenResultFactory {

    private RefreshTokenResultFactory() {
    }

    static AutenticacaoResult criarAutenticacaoComRefresh(
            UsuarioAutenticacao usuario,
            GerarTokenAutenticacaoPort gerarTokenAutenticacaoPort,
            GerarRefreshTokenPort gerarRefreshTokenPort,
            SalvarRefreshTokenPort salvarRefreshTokenPort,
            Clock clock
    ) {
        TokenGerado accessToken = gerarTokenAutenticacaoPort.gerarAccessToken(usuario);
        RefreshTokenGerado refreshToken = gerarRefreshTokenPort.gerarRefreshToken(usuario);
        salvarRefreshTokenPort.salvarRefreshToken(new RefreshTokenAutenticacao(
                UUID.randomUUID(),
                usuario.id(),
                refreshToken.tokenHash(),
                refreshToken.expiraEm(),
                false,
                Instant.now(clock)
        ));
        return new AutenticacaoResult(
                accessToken.valor(),
                refreshToken.valor(),
                "Bearer",
                accessToken.expiraEm(),
                new UsuarioAutenticadoResult(
                        usuario.id(),
                        usuario.nome(),
                        usuario.email().valor(),
                        usuario.perfis()
                )
        );
    }
}
