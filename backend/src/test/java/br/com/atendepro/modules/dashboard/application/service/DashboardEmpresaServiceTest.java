package br.com.atendepro.modules.dashboard.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.dashboard.application.result.MetricasDashboardEmpresaResult;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;

class DashboardEmpresaServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("fb6766cb-c31d-4e0f-bc55-c9cf15f2bf2e");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveConsultarDashboardDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        DashboardEmpresaService service = new DashboardEmpresaService(
                (empresaId, hoje) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(hoje).isEqualTo(LocalDate.parse("2026-05-25"));
                    return metricas();
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.consultarDashboardEmpresa(null);

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.clientesAtivos()).isEqualTo(12);
        assertThat(result.produtosEstoqueBaixo()).isEqualTo(2);
        assertThat(result.atualizadoEm()).isEqualTo(Instant.parse("2026-05-25T00:00:00Z"));
    }

    @Test
    void naoDeveConsultarDashboardSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        DashboardEmpresaService service = new DashboardEmpresaService(
                (empresaId, hoje) -> metricas(),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        assertThatThrownBy(() -> service.consultarDashboardEmpresa(null))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private MetricasDashboardEmpresaResult metricas() {
        return new MetricasDashboardEmpresaResult(
                12,
                3,
                8,
                5,
                2,
                1,
                4,
                new BigDecimal("1500.00"),
                new BigDecimal("250.00")
        );
    }
}
