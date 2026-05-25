package br.com.atendepro.modules.adminsaas.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;

class AdminSaasServiceTest {

    private final AdminSaasService service = new AdminSaasService(new PermissaoAcessoService());

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveConsultarStatusDoAdminSaasParaSuperAdmin() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.SUPER_ADMIN)
        ));

        var result = service.consultarStatus();

        assertThat(result.produto()).isEqualTo("AtendePro");
        assertThat(result.release()).isEqualTo("R2");
        assertThat(result.capacidades()).contains("dashboard-admin-saas", "gestao-empresas", "planos-assinaturas");
    }

    @Test
    void naoDeveConsultarStatusDoAdminSaasSemPermissao() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.EMPRESA_ADMIN)
        ));

        assertThatThrownBy(service::consultarStatus)
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }
}
