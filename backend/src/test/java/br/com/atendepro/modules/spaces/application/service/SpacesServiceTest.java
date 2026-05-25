package br.com.atendepro.modules.spaces.application.service;

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
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;

class SpacesServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("6ca68360-c969-4da4-8700-3b42cd74e01a");

    private final SpacesService service = new SpacesService(new PermissaoAcessoService());

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveConsultarStatusDoModuloSpaces() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));

        var result = service.consultarStatus();

        assertThat(result.produto()).isEqualTo("AtendePro Spaces");
        assertThat(result.release()).isEqualTo("R5");
        assertThat(result.status()).isEqualTo("SPACES_OPERACIONAL");
        assertThat(result.tiposRecurso())
                .containsExactly(
                        TipoRecursoSpaces.SALA,
                        TipoRecursoSpaces.CADEIRA,
                        TipoRecursoSpaces.CABINE,
                        TipoRecursoSpaces.EQUIPAMENTO
                );
        assertThat(result.capacidades()).contains("sublocacao");
    }

    @Test
    void naoDeveConsultarStatusSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));

        assertThatThrownBy(service::consultarStatus)
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }
}
