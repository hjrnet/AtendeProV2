package br.com.atendepro.modules.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.command.CadastrarUsuarioBootstrapCommand;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

class GarantirUsuarioBootstrapServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void deveCadastrarUsuarioBootstrapQuandoNaoExistir() {
        SalvarUsuarioFake salvarUsuarioFake = new SalvarUsuarioFake();
        GarantirUsuarioBootstrapService service = new GarantirUsuarioBootstrapService(
                email -> Optional.empty(),
                salvarUsuarioFake,
                senha -> "hash-" + senha,
                CLOCK
        );

        var result = service.garantirUsuarioBootstrap(command());

        assertThat(result.criado()).isTrue();
        assertThat(result.email()).isEqualTo("admin@atendepro.local");
        assertThat(salvarUsuarioFake.usuarioSalvo.senhaHash()).isEqualTo("hash-AtendePro@2026");
        assertThat(salvarUsuarioFake.usuarioSalvo.ativo()).isTrue();
    }

    @Test
    void naoDeveCadastrarUsuarioBootstrapQuandoJaExistir() {
        UsuarioAutenticacao usuarioExistente = new UsuarioAutenticacao(
                java.util.UUID.randomUUID(),
                EmailUsuario.de("admin@atendepro.local"),
                "Admin",
                "hash-existente",
                Set.of(PerfilAcesso.SUPER_ADMIN),
                true,
                Instant.now(CLOCK)
        );
        SalvarUsuarioFake salvarUsuarioFake = new SalvarUsuarioFake();
        GarantirUsuarioBootstrapService service = new GarantirUsuarioBootstrapService(
                email -> Optional.of(usuarioExistente),
                salvarUsuarioFake,
                senha -> "hash-" + senha,
                CLOCK
        );

        var result = service.garantirUsuarioBootstrap(command());

        assertThat(result.criado()).isFalse();
        assertThat(result.id()).isEqualTo(usuarioExistente.id());
        assertThat(salvarUsuarioFake.usuarioSalvo).isNull();
    }

    private CadastrarUsuarioBootstrapCommand command() {
        return new CadastrarUsuarioBootstrapCommand(
                "Admin",
                EmailUsuario.de("admin@atendepro.local"),
                "AtendePro@2026",
                Set.of(PerfilAcesso.SUPER_ADMIN)
        );
    }

    private static class SalvarUsuarioFake implements br.com.atendepro.modules.auth.application.port.out.SalvarUsuarioAutenticacaoPort {

        private UsuarioAutenticacao usuarioSalvo;

        @Override
        public void salvarUsuarioAutenticacao(UsuarioAutenticacao usuario) {
            this.usuarioSalvo = usuario;
        }
    }
}
