package br.com.atendepro.modules.auth.application.permission;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;

class PermissaoAcessoServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");

    private final PermissaoAcessoService service = new PermissaoAcessoService();

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void devePermitirQuandoPerfilTemPermissao() {
        TenantContextHolder.definir(new TenantContext(
                EMPRESA_ID,
                UUID.randomUUID(),
                Set.of(PerfilAcesso.EMPRESA_ADMIN)
        ));

        assertThatCode(() -> service.validarPermissao(PermissaoAcesso.CADASTRAR_ADMINISTRADOR_EMPRESA))
                .doesNotThrowAnyException();
    }

    @Test
    void naoDevePermitirQuandoPerfilNaoTemPermissao() {
        TenantContextHolder.definir(new TenantContext(
                EMPRESA_ID,
                UUID.randomUUID(),
                Set.of(PerfilAcesso.PROFISSIONAL)
        ));

        assertThatThrownBy(() -> service.validarPermissao(PermissaoAcesso.CADASTRAR_ADMINISTRADOR_EMPRESA))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    @Test
    void devePermitirSemContextoDuranteFaseLocalEBootstrap() {
        assertThatCode(() -> service.validarPermissao(PermissaoAcesso.CADASTRAR_EMPRESA))
                .doesNotThrowAnyException();
    }
}
