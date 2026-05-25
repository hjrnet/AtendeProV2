package br.com.atendepro.modules.adminsaas.application.service;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarDashboardAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.out.CarregarMetricasAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.AdminSaasDashboardResult;
import br.com.atendepro.modules.adminsaas.application.result.AdminSaasStatusResult;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;

@Service
@Profile("!test")
public class AdminSaasService implements ConsultarAdminSaasUseCase, ConsultarDashboardAdminSaasUseCase {

    private final PermissaoAcessoService permissaoAcessoService;
    private final CarregarMetricasAdminSaasPort carregarMetricasAdminSaasPort;

    public AdminSaasService(
            PermissaoAcessoService permissaoAcessoService,
            CarregarMetricasAdminSaasPort carregarMetricasAdminSaasPort
    ) {
        this.permissaoAcessoService = permissaoAcessoService;
        this.carregarMetricasAdminSaasPort = carregarMetricasAdminSaasPort;
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
}
