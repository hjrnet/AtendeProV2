package br.com.atendepro.modules.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.command.RedefinirSenhaCommand;
import br.com.atendepro.modules.auth.domain.exception.AutenticacaoException;
import br.com.atendepro.modules.auth.domain.model.TokenRecuperacaoSenha;

class RedefinirSenhaServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void deveRedefinirSenhaComTokenAtivo() {
        AtualizarSenhaFake atualizarSenhaFake = new AtualizarSenhaFake();
        MarcarTokenFake marcarTokenFake = new MarcarTokenFake();
        RedefinirSenhaService service = new RedefinirSenhaService(
                token -> "token-hash",
                (hash, agora) -> Optional.of(token()),
                atualizarSenhaFake,
                senha -> "hash-" + senha,
                marcarTokenFake,
                CLOCK
        );

        service.redefinirSenha(new RedefinirSenhaCommand("token-local", "NovaSenha@2026"));

        assertThat(atualizarSenhaFake.senhaHash).isEqualTo("hash-NovaSenha@2026");
        assertThat(marcarTokenFake.tokenUtilizado).isEqualTo(token().id());
    }

    @Test
    void naoDeveRedefinirSenhaComTokenInvalido() {
        RedefinirSenhaService service = new RedefinirSenhaService(
                token -> "token-hash",
                (hash, agora) -> Optional.empty(),
                (usuarioId, senhaHash) -> {
                },
                senha -> "hash-" + senha,
                (tokenId, utilizadoEm) -> {
                },
                CLOCK
        );

        assertThatThrownBy(() -> service.redefinirSenha(new RedefinirSenhaCommand("token-local", "NovaSenha@2026")))
                .isInstanceOf(AutenticacaoException.class)
                .hasMessage("Token de recuperacao invalido.");
    }

    private TokenRecuperacaoSenha token() {
        return new TokenRecuperacaoSenha(
                UUID.fromString("2f52080a-951b-43d7-8f12-3c82de108af9"),
                UUID.fromString("3d0fbc6f-1770-4d05-a0e6-e21efbcaf606"),
                "token-hash",
                Instant.parse("2026-05-25T00:30:00Z"),
                false,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class AtualizarSenhaFake implements br.com.atendepro.modules.auth.application.port.out.AtualizarSenhaUsuarioPort {

        private String senhaHash;

        @Override
        public void atualizarSenhaUsuario(UUID usuarioId, String senhaHash) {
            this.senhaHash = senhaHash;
        }
    }

    private static class MarcarTokenFake implements br.com.atendepro.modules.auth.application.port.out.MarcarTokenRecuperacaoSenhaUtilizadoPort {

        private UUID tokenUtilizado;

        @Override
        public void marcarTokenRecuperacaoSenhaUtilizado(UUID tokenId, Instant utilizadoEm) {
            this.tokenUtilizado = tokenId;
        }
    }
}
