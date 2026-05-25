package br.com.atendepro.modules.precificacao.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.MetricasDashboardPrecificacaoResult;

public interface CarregarMetricasDashboardPrecificacaoPort {

    MetricasDashboardPrecificacaoResult carregarMetricasDashboardPrecificacao(UUID empresaId);
}
