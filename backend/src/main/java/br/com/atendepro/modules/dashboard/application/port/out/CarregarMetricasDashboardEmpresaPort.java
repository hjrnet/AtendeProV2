package br.com.atendepro.modules.dashboard.application.port.out;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.dashboard.application.result.MetricasDashboardEmpresaResult;

public interface CarregarMetricasDashboardEmpresaPort {

    MetricasDashboardEmpresaResult carregarMetricasDashboardEmpresa(UUID empresaId, LocalDate hoje);
}
