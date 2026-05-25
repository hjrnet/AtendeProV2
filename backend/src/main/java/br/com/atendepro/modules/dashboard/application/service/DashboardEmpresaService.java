package br.com.atendepro.modules.dashboard.application.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.dashboard.application.port.in.ConsultarDashboardEmpresaUseCase;
import br.com.atendepro.modules.dashboard.application.port.out.CarregarMetricasDashboardEmpresaPort;
import br.com.atendepro.modules.dashboard.application.result.DashboardEmpresaResult;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class DashboardEmpresaService implements ConsultarDashboardEmpresaUseCase {

    private final CarregarMetricasDashboardEmpresaPort carregarMetricasDashboardEmpresaPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public DashboardEmpresaService(
            CarregarMetricasDashboardEmpresaPort carregarMetricasDashboardEmpresaPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarMetricasDashboardEmpresaPort = carregarMetricasDashboardEmpresaPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public DashboardEmpresaResult consultarDashboardEmpresa(UUID empresaId) {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.VISUALIZAR_EMPRESA);
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        var metricas = carregarMetricasDashboardEmpresaPort.carregarMetricasDashboardEmpresa(
                empresaResolvida,
                LocalDate.now(clock)
        );
        return DashboardEmpresaResult.de(empresaResolvida, metricas, Instant.now(clock));
    }

    private UUID resolverEmpresaId(UUID empresaIdSolicitada) {
        Optional<UUID> empresaRestrita = tenantAccessService.empresaRestrita();
        if (empresaRestrita.isPresent()) {
            if (empresaIdSolicitada != null) {
                tenantAccessService.validarAcessoEmpresa(empresaIdSolicitada);
            }
            return empresaRestrita.get();
        }
        if (empresaIdSolicitada == null) {
            throw new BusinessException("DASHBOARD_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para consultar dashboard.");
        }
        return empresaIdSolicitada;
    }
}
