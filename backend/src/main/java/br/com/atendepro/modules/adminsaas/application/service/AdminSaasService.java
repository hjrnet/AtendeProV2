package br.com.atendepro.modules.adminsaas.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarDashboardAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarDashboardVendasAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.out.CarregarMetricasAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.CarregarMetricasVendasAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.AdminSaasDashboardResult;
import br.com.atendepro.modules.adminsaas.application.result.AdminSaasStatusResult;
import br.com.atendepro.modules.adminsaas.application.result.DashboardVendasAdminSaasResult;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;

@Service
@Profile("!test")
public class AdminSaasService implements
        ConsultarAdminSaasUseCase,
        ConsultarDashboardAdminSaasUseCase,
        ConsultarDashboardVendasAdminSaasUseCase {

    private static final BigDecimal CEM = new BigDecimal("100.00");

    private final PermissaoAcessoService permissaoAcessoService;
    private final CarregarMetricasAdminSaasPort carregarMetricasAdminSaasPort;
    private final CarregarMetricasVendasAdminSaasPort carregarMetricasVendasAdminSaasPort;

    public AdminSaasService(
            PermissaoAcessoService permissaoAcessoService,
            CarregarMetricasAdminSaasPort carregarMetricasAdminSaasPort,
            CarregarMetricasVendasAdminSaasPort carregarMetricasVendasAdminSaasPort
    ) {
        this.permissaoAcessoService = permissaoAcessoService;
        this.carregarMetricasAdminSaasPort = carregarMetricasAdminSaasPort;
        this.carregarMetricasVendasAdminSaasPort = carregarMetricasVendasAdminSaasPort;
    }

    @Override
    public AdminSaasStatusResult consultarStatus() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
        return new AdminSaasStatusResult(
                "AtendePro",
                "R2",
                "ADMIN_SAAS_OPERACIONAL",
                List.of(
                        "dashboard-admin-saas",
                        "gestao-empresas",
                        "planos-assinaturas"
                )
        );
    }

    @Override
    public AdminSaasDashboardResult consultarDashboard() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
        var metricas = carregarMetricasAdminSaasPort.carregarMetricas();
        return new AdminSaasDashboardResult(
                metricas.mrr(),
                metricas.empresasAtivas(),
                metricas.empresasBloqueadas(),
                metricas.trialsAtivos(),
                metricas.chamadosAbertos(),
                java.time.Instant.now()
        );
    }

    @Override
    public DashboardVendasAdminSaasResult consultarDashboardVendas() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
        var metricas = carregarMetricasVendasAdminSaasPort.carregarMetricasVendas();
        long baseChurn = metricas.assinaturasAtivas() + metricas.assinaturasCanceladas();
        return new DashboardVendasAdminSaasResult(
                metricas.mrr(),
                metricas.trialsIniciados(),
                metricas.trialsConvertidos(),
                calcularTaxa(metricas.trialsConvertidos(), metricas.trialsIniciados()),
                metricas.assinaturasAtivas(),
                metricas.assinaturasCanceladas(),
                calcularTaxa(metricas.assinaturasCanceladas(), baseChurn),
                metricas.planosVendidos(),
                java.time.Instant.now()
        );
    }

    private BigDecimal calcularTaxa(long parte, long total) {
        if (total <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.valueOf(parte)
                .multiply(CEM)
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_EVEN);
    }
}
