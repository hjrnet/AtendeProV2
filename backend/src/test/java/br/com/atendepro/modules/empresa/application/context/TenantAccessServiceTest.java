package br.com.atendepro.modules.empresa.application.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.domain.exception.AcessoTenantNegadoException;

class TenantAccessServiceTest {

    private static final UUID EMPRESA_A = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");
    private static final UUID EMPRESA_B = UUID.fromString("4cc1f2a1-a3e4-4c11-a390-692c6b302351");

    private final TenantAccessService service = new TenantAccessService();

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void devePermitirAcessoNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_A, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));

        assertThatCode(() -> service.validarAcessoEmpresa(EMPRESA_A)).doesNotThrowAnyException();
        assertThat(service.empresaRestrita()).contains(EMPRESA_A);
    }

    @Test
    void naoDevePermitirAcessoEmEmpresaDiferenteDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_A, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));

        assertThatThrownBy(() -> service.validarAcessoEmpresa(EMPRESA_B))
                .isInstanceOf(AcessoTenantNegadoException.class)
                .hasMessage("Usuario nao possui acesso a esta empresa.");
    }

    @Test
    void devePermitirOperacaoGlobalSemContextoDeTenant() {
        assertThatCode(service::validarOperacaoGlobal).doesNotThrowAnyException();
        assertThat(service.empresaRestrita()).isEmpty();
    }

    @Test
    void devePermitirOperacaoGlobalParaSuperAdmin() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_A, UUID.randomUUID(), Set.of(PerfilAcesso.SUPER_ADMIN)));

        assertThatCode(service::validarOperacaoGlobal).doesNotThrowAnyException();
        assertThat(service.empresaRestrita()).isEmpty();
    }

    @Test
    void naoDevePermitirOperacaoGlobalParaPerfilRestrito() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_A, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));

        assertThatThrownBy(service::validarOperacaoGlobal)
                .isInstanceOf(AcessoTenantNegadoException.class)
                .hasMessage("Usuario nao possui acesso a esta empresa.");
    }
}
