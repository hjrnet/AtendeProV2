package br.com.atendepro.modules.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.command.AutenticarUsuarioCommand;
import br.com.atendepro.modules.auth.application.port.out.RefreshTokenGerado;
import br.com.atendepro.modules.auth.domain.model.RefreshTokenAutenticacao;
import br.com.atendepro.modules.auth.application.port.out.TokenGerado;
import br.com.atendepro.modules.auth.domain.exception.AutenticacaoException;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

class AutenticarUsuarioServiceTest {

    @Test
    void deveAutenticarUsuarioAtivoComSenhaCorreta() {
        UsuarioAutenticacao usuario = usuario(true);
        SalvarRefreshTokenFake salvarRefreshTokenFake = new SalvarRefreshTokenFake();
        AutenticarUsuarioService service = new AutenticarUsuarioService(
                email -> Optional.of(usuario),
                (senha, hash) -> senha.equals("AtendePro@2026") && hash.equals("hash"),
                usuarioAutenticacao -> new TokenGerado("jwt-token", Instant.parse("2026-05-25T01:00:00Z")),
                usuarioAutenticacao -> new RefreshTokenGerado("refresh-token", "refresh-hash", Instant.parse("2026-06-01T00:00:00Z")),
                salvarRefreshTokenFake,
                Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC)
        );

        var result = service.autenticarUsuario(command());

        assertThat(result.accessToken()).isEqualTo("jwt-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        assertThat(result.tipoToken()).isEqualTo("Bearer");
        assertThat(result.usuario().email()).isEqualTo("admin@atendepro.local");
        assertThat(salvarRefreshTokenFake.refreshTokenSalvo.tokenHash()).isEqualTo("refresh-hash");
    }

    @Test
    void naoDeveAutenticarSenhaIncorreta() {
        AutenticarUsuarioService service = new AutenticarUsuarioService(
                email -> Optional.of(usuario(true)),
                (senha, hash) -> false,
                usuarioAutenticacao -> new TokenGerado("jwt-token", Instant.parse("2026-05-25T01:00:00Z")),
                usuarioAutenticacao -> new RefreshTokenGerado("refresh-token", "refresh-hash", Instant.parse("2026-06-01T00:00:00Z")),
                refreshToken -> {
                },
                Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC)
        );

        assertThatThrownBy(() -> service.autenticarUsuario(command()))
                .isInstanceOf(AutenticacaoException.class)
                .hasMessage("Email ou senha invalidos.");
    }

    @Test
    void naoDeveAutenticarUsuarioInativo() {
        AutenticarUsuarioService service = new AutenticarUsuarioService(
                email -> Optional.of(usuario(false)),
                (senha, hash) -> true,
                usuarioAutenticacao -> new TokenGerado("jwt-token", Instant.parse("2026-05-25T01:00:00Z")),
                usuarioAutenticacao -> new RefreshTokenGerado("refresh-token", "refresh-hash", Instant.parse("2026-06-01T00:00:00Z")),
                refreshToken -> {
                },
                Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC)
        );

        assertThatThrownBy(() -> service.autenticarUsuario(command()))
                .isInstanceOf(AutenticacaoException.class)
                .hasMessage("Usuario inativo.");
    }

    private AutenticarUsuarioCommand command() {
        return new AutenticarUsuarioCommand(EmailUsuario.de("admin@atendepro.local"), "AtendePro@2026");
    }

    private UsuarioAutenticacao usuario(boolean ativo) {
        return new UsuarioAutenticacao(
                UUID.randomUUID(),
                EmailUsuario.de("admin@atendepro.local"),
                "Admin",
                "hash",
                Set.of(PerfilAcesso.SUPER_ADMIN),
                ativo,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarRefreshTokenFake implements br.com.atendepro.modules.auth.application.port.out.SalvarRefreshTokenPort {

        private RefreshTokenAutenticacao refreshTokenSalvo;

        @Override
        public void salvarRefreshToken(RefreshTokenAutenticacao refreshToken) {
            this.refreshTokenSalvo = refreshToken;
        }
    }
}
