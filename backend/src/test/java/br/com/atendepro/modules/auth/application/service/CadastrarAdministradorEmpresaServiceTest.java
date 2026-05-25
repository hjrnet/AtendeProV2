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

import br.com.atendepro.modules.auth.application.command.CadastrarAdministradorEmpresaCommand;
import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.domain.model.DocumentoEmpresa;
import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;
import br.com.atendepro.shared.domain.exception.BusinessException;

class CadastrarAdministradorEmpresaServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void deveCadastrarAdministradorVinculadoEmpresa() {
        SalvarUsuarioFake salvarUsuarioFake = new SalvarUsuarioFake();
        CadastrarAdministradorEmpresaService service = new CadastrarAdministradorEmpresaService(
                id -> Optional.of(empresa()),
                email -> Optional.empty(),
                senha -> "hash-" + senha,
                salvarUsuarioFake,
                new TenantAccessService(),
                CLOCK
        );

        var result = service.cadastrarAdministradorEmpresa(command());

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.perfis()).containsExactly(PerfilAcesso.EMPRESA_ADMIN);
        assertThat(salvarUsuarioFake.usuarioSalvo.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(salvarUsuarioFake.usuarioSalvo.senhaHash()).isEqualTo("hash-AdminEmpresa@2026");
    }

    @Test
    void naoDeveCadastrarAdministradorParaEmpresaInexistente() {
        CadastrarAdministradorEmpresaService service = new CadastrarAdministradorEmpresaService(
                id -> Optional.empty(),
                email -> Optional.empty(),
                senha -> "hash-" + senha,
                usuario -> {
                },
                new TenantAccessService(),
                CLOCK
        );

        assertThatThrownBy(() -> service.cadastrarAdministradorEmpresa(command()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa nao encontrada.");
    }

    @Test
    void naoDeveCadastrarAdministradorComEmailDuplicado() {
        CadastrarAdministradorEmpresaService service = new CadastrarAdministradorEmpresaService(
                id -> Optional.of(empresa()),
                email -> Optional.of(usuarioExistente()),
                senha -> "hash-" + senha,
                usuario -> {
                },
                new TenantAccessService(),
                CLOCK
        );

        assertThatThrownBy(() -> service.cadastrarAdministradorEmpresa(command()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Email de usuario ja cadastrado.");
    }

    private CadastrarAdministradorEmpresaCommand command() {
        return new CadastrarAdministradorEmpresaCommand(
                EMPRESA_ID,
                "Admin Empresa",
                EmailUsuario.de("admin.empresa@atendepro.local"),
                "AdminEmpresa@2026"
        );
    }

    private EmpresaTenant empresa() {
        return new EmpresaTenant(
                EMPRESA_ID,
                "Clinica Tenant",
                "Clinica Tenant LTDA",
                DocumentoEmpresa.de("12345678000190"),
                "contato@tenant.test",
                "11999999999",
                true,
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private UsuarioAutenticacao usuarioExistente() {
        return new UsuarioAutenticacao(
                UUID.randomUUID(),
                EMPRESA_ID,
                EmailUsuario.de("admin.empresa@atendepro.local"),
                "Admin Empresa",
                "hash",
                Set.of(PerfilAcesso.EMPRESA_ADMIN),
                true,
                Instant.parse("2026-05-25T00:00:00Z")
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
