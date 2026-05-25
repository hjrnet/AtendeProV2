package br.com.atendepro.modules.dashboard.application.result;

import java.math.BigDecimal;

public record MetricasDashboardEmpresaResult(
        long clientesAtivos,
        long compromissosHoje,
        long compromissosProximos7Dias,
        long servicosAtivos,
        long produtosEstoqueBaixo,
        long produtosVencendo30Dias,
        long equipamentosManutencao30Dias,
        BigDecimal custosGeraisAtivos,
        BigDecimal custosAlimentacaoTransporteAtivos
) {
}
