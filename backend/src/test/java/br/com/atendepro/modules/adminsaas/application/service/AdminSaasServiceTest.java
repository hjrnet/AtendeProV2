package br.com.atendepro.modules.adminsaas.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.adminsaas.application.port.out.CarregarMetricasAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.CarregarMetricasVendasAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.MetricasAdminSaasResult;
import br.com.atendepro.modules.adminsaas.application.result.MetricasVendasAdminSaasResult;
import br.com.atendepro.modules.adminsaas.application.result.PlanoVendidoAdminSaasResult;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;

class AdminSaasServiceTest {

    private final CarregarMetricasAdminSaasPort metricasPort = () -> new MetricasAdminSaasResult(
            new BigDecimal("1290.00"),
            8,
            2,
            3,
            5
    );
    private final CarregarMetricasVendasAdminSaasPort metricasVendasPort = () -> new MetricasVendasAdminSaasResult(
            new BigDecimal("2490.00"),
            10,
            4,
            7,
            3,
            List.of(new PlanoVendidoAdminSaasResult(
                    UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    "CARE",
                    "Care",
                    5,
                    4,
                    new BigDecimal("1196.00")
            ))
    );
    private final AdminSaasService service = new AdminSaasService(
            new PermissaoAcessoService(),
            metricasPort,
            metricasVendasPort
    );

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
    void deveConsultarDashboardDoAdminSaasParaSuperAdmin() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.SUPER_ADMIN)
        ));

        var result = service.consultarDashboard();

        assertThat(result.mrr()).isEqualByComparingTo("1290.00");
        assertThat(result.empresasAtivas()).isEqualTo(8);
        assertThat(result.empresasBloqueadas()).isEqualTo(2);
        assertThat(result.trialsAtivos()).isEqualTo(3);
        assertThat(result.chamadosAbertos()).isEqualTo(5);
        assertThat(result.atualizadoEm()).isNotNull();
    }

    @Test
    void deveConsultarDashboardDeVendasParaSuperAdmin() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.SUPER_ADMIN)
        ));

        var result = service.consultarDashboardVendas();

        assertThat(result.mrr()).isEqualByComparingTo("2490.00");
        assertThat(result.trialsIniciados()).isEqualTo(10);
        assertThat(result.trialsConvertidos()).isEqualTo(4);
        assertThat(result.taxaConversaoTrial()).isEqualByComparingTo("40.00");
        assertThat(result.assinaturasAtivas()).isEqualTo(7);
        assertThat(result.assinaturasCanceladas()).isEqualTo(3);
        assertThat(result.taxaChurn()).isEqualByComparingTo("30.00");
        assertThat(result.planosVendidos())
                .singleElement()
                .satisfies(plano -> {
                    assertThat(plano.codigo()).isEqualTo("CARE");
                    assertThat(plano.totalAssinaturas()).isEqualTo(5);
                    assertThat(plano.assinaturasAtivas()).isEqualTo(4);
                    assertThat(plano.mrr()).isEqualByComparingTo("1196.00");
                });
        assertThat(result.atualizadoEm()).isNotNull();
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

    @Test
    void naoDeveConsultarDashboardDoAdminSaasSemPermissao() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.EMPRESA_ADMIN)
        ));

        assertThatThrownBy(service::consultarDashboard)
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    @Test
    void naoDeveConsultarDashboardDeVendasSemPermissao() {
        TenantContextHolder.definir(new TenantContext(
                UUID.randomUUID(),
                UUID.randomUUID(),
                Set.of(PerfilAcesso.EMPRESA_ADMIN)
        ));

        assertThatThrownBy(service::consultarDashboardVendas)
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }
}
