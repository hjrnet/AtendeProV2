package br.com.atendepro.modules.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.command.RenovarSessaoCommand;
import br.com.atendepro.modules.auth.application.port.out.RefreshTokenGerado;
import br.com.atendepro.modules.auth.application.port.out.TokenGerado;
import br.com.atendepro.modules.auth.domain.exception.AutenticacaoException;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.RefreshTokenAutenticacao;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

class RenovarSessaoServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void deveRenovarSessaoComRefreshTokenAtivo() {
        RefreshTokenAutenticacao refreshToken = refreshToken();
        RevogarRefreshTokenFake revogarRefreshTokenFake = new RevogarRefreshTokenFake();
        SalvarRefreshTokenFake salvarRefreshTokenFake = new SalvarRefreshTokenFake();
        RenovarSessaoService service = new RenovarSessaoService(
                token -> "hash-antigo",
                (hash, agora) -> Optional.of(refreshToken),
                usuarioId -> Optional.of(usuario()),
                revogarRefreshTokenFake,
                usuario -> new TokenGerado("novo-access-token", Instant.parse("2026-05-25T01:00:00Z")),
                usuario -> new RefreshTokenGerado("novo-refresh-token", "novo-refresh-hash", Instant.parse("2026-06-01T00:00:00Z")),
                salvarRefreshTokenFake,
                CLOCK
        );

        var result = service.renovarSessao(new RenovarSessaoCommand("refresh-token-antigo"));

        assertThat(result.accessToken()).isEqualTo("novo-access-token");
        assertThat(result.refreshToken()).isEqualTo("novo-refresh-token");
        assertThat(revogarRefreshTokenFake.refreshTokenRevogado).isEqualTo(refreshToken.id());
        assertThat(salvarRefreshTokenFake.refreshTokenSalvo.tokenHash()).isEqualTo("novo-refresh-hash");
    }

    @Test
    void naoDeveRenovarSessaoComRefreshTokenInvalido() {
        RenovarSessaoService service = new RenovarSessaoService(
                token -> "hash-invalido",
                (hash, agora) -> Optional.empty(),
                usuarioId -> Optional.of(usuario()),
                (id, agora) -> {
                },
                usuario -> new TokenGerado("novo-access-token", Instant.parse("2026-05-25T01:00:00Z")),
                usuario -> new RefreshTokenGerado("novo-refresh-token", "novo-refresh-hash", Instant.parse("2026-06-01T00:00:00Z")),
                refreshToken -> {
                },
                CLOCK
        );

        assertThatThrownBy(() -> service.renovarSessao(new RenovarSessaoCommand("refresh-token-antigo")))
                .isInstanceOf(AutenticacaoException.class)
                .hasMessage("Refresh token invalido.");
    }

    private RefreshTokenAutenticacao refreshToken() {
        return new RefreshTokenAutenticacao(
                UUID.fromString("2f52080a-951b-43d7-8f12-3c82de108af9"),
                usuario().id(),
                "hash-antigo",
                Instant.parse("2026-06-01T00:00:00Z"),
                false,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private UsuarioAutenticacao usuario() {
        return new UsuarioAutenticacao(
                UUID.fromString("3d0fbc6f-1770-4d05-a0e6-e21efbcaf606"),
                EmailUsuario.de("admin@atendepro.local"),
                "Admin",
                "hash",
                Set.of(PerfilAcesso.SUPER_ADMIN),
                true,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class RevogarRefreshTokenFake implements br.com.atendepro.modules.auth.application.port.out.RevogarRefreshTokenPort {

        private UUID refreshTokenRevogado;

        @Override
        public void revogarRefreshToken(UUID refreshTokenId, Instant revogadoEm) {
            this.refreshTokenRevogado = refreshTokenId;
        }
    }

    private static class SalvarRefreshTokenFake implements br.com.atendepro.modules.auth.application.port.out.SalvarRefreshTokenPort {

        private RefreshTokenAutenticacao refreshTokenSalvo;

        @Override
        public void salvarRefreshToken(RefreshTokenAutenticacao refreshToken) {
            this.refreshTokenSalvo = refreshToken;
        }
    }
}
