package br.com.atendepro.modules.busca.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.busca.application.result.ResultadoBuscaGlobalItemResult;
import br.com.atendepro.modules.busca.domain.model.TipoResultadoBusca;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;

class BuscaGlobalServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("bba6b60c-b431-483f-830f-7ec7f6188d9a");

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveBuscarGlobalNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        BuscaGlobalService service = new BuscaGlobalService(
                (empresaId, busca, categoria, status, limitePorTipo) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("ana");
                    assertThat(categoria).isEqualTo("NUTRI");
                    assertThat(status).isEqualTo("ATIVO");
                    assertThat(limitePorTipo).isEqualTo(20);
                    return List.of(item());
                },
                new TenantAccessService(),
                new PermissaoAcessoService()
        );

        var result = service.buscarGlobal(null, " ana ", " NUTRI ", " ATIVO ", 99);

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("tipo").containsExactly(TipoResultadoBusca.CLIENTE_PACIENTE);
    }

    @Test
    void naoDeveBuscarGlobalSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        BuscaGlobalService service = new BuscaGlobalService(
                (empresaId, busca, categoria, status, limitePorTipo) -> List.of(),
                new TenantAccessService(),
                new PermissaoAcessoService()
        );

        assertThatThrownBy(() -> service.buscarGlobal(null, "ana", null, null, 5))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private ResultadoBuscaGlobalItemResult item() {
        return new ResultadoBuscaGlobalItemResult(
                UUID.randomUUID(),
                TipoResultadoBusca.CLIENTE_PACIENTE,
                "Ana Cliente",
                "ana@example.com",
                "NUTRI",
                "ATIVO",
                "/app/clientes-pacientes/1",
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }
}
