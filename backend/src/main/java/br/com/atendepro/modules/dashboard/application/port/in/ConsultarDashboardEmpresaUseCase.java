package br.com.atendepro.modules.dashboard.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.dashboard.application.result.DashboardEmpresaResult;

public interface ConsultarDashboardEmpresaUseCase {

    DashboardEmpresaResult consultarDashboardEmpresa(UUID empresaId);
}
