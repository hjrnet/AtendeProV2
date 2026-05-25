package br.com.atendepro.modules.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.adapter.out.security.JwtAutenticacaoProperties;
import br.com.atendepro.modules.auth.application.command.SolicitarRecuperacaoSenhaCommand;
import br.com.atendepro.modules.auth.application.port.out.TokenRecuperacaoSenhaGerado;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.TokenRecuperacaoSenha;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

class SolicitarRecuperacaoSenhaServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void deveGerarTokenQuandoUsuarioExistir() {
        SalvarTokenFake salvarTokenFake = new SalvarTokenFake();
        EnviarTokenFake enviarTokenFake = new EnviarTokenFake();
        SolicitarRecuperacaoSenhaService service = new SolicitarRecuperacaoSenhaService(
                email -> Optional.of(usuario()),
                usuario -> new TokenRecuperacaoSenhaGerado("token-local", "token-hash", Instant.parse("2026-05-25T00:30:00Z")),
                salvarTokenFake,
                enviarTokenFake,
                new JwtAutenticacaoProperties("atendepro-test", "segredo", 30, 7, 30, true),
                CLOCK
        );

        var result = service.solicitarRecuperacaoSenha(command());

        assertThat(result.tokenTesteLocal()).isEqualTo("token-local");
        assertThat(salvarTokenFake.tokenSalvo.tokenHash()).isEqualTo("token-hash");
        assertThat(enviarTokenFake.tokenEnviado).isEqualTo("token-local");
    }

    @Test
    void deveResponderSemRevelarQuandoUsuarioNaoExistir() {
        SolicitarRecuperacaoSenhaService service = new SolicitarRecuperacaoSenhaService(
                email -> Optional.empty(),
                usuario -> new TokenRecuperacaoSenhaGerado("token-local", "token-hash", Instant.parse("2026-05-25T00:30:00Z")),
                token -> {
                },
                (usuario, token) -> {
                },
                new JwtAutenticacaoProperties("atendepro-test", "segredo", 30, 7, 30, true),
                CLOCK
        );

        var result = service.solicitarRecuperacaoSenha(command());

        assertThat(result.tokenTesteLocal()).isNull();
    }

    private SolicitarRecuperacaoSenhaCommand command() {
        return new SolicitarRecuperacaoSenhaCommand(EmailUsuario.de("admin@atendepro.local"));
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

    private static class SalvarTokenFake implements br.com.atendepro.modules.auth.application.port.out.SalvarTokenRecuperacaoSenhaPort {

        private TokenRecuperacaoSenha tokenSalvo;

        @Override
        public void salvarTokenRecuperacaoSenha(TokenRecuperacaoSenha token) {
            this.tokenSalvo = token;
        }
    }

    private static class EnviarTokenFake implements br.com.atendepro.modules.auth.application.port.out.EnviarTokenRecuperacaoSenhaPort {

        private String tokenEnviado;

        @Override
        public void enviarTokenRecuperacaoSenha(UsuarioAutenticacao usuario, String tokenEmTexto) {
            this.tokenEnviado = tokenEmTexto;
        }
    }
}
