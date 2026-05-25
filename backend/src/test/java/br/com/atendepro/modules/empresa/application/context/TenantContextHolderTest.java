package br.com.atendepro.modules.empresa.application.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;

class TenantContextHolderTest {

    @AfterEach
    void limpar() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveDefinirERecuperarContextoAtual() {
        UUID empresaId = UUID.randomUUID();
        TenantContextHolder.definir(new TenantContext(empresaId, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));

        assertThat(TenantContextHolder.contextoAtual())
                .get()
                .extracting(TenantContext::empresaId)
                .isEqualTo(empresaId);
    }

    @Test
    void deveLimparContextoAtual() {
        TenantContextHolder.definir(new TenantContext(UUID.randomUUID(), null, Set.of()));

        TenantContextHolder.limpar();

        assertThat(TenantContextHolder.contextoAtual()).isEmpty();
    }
}
