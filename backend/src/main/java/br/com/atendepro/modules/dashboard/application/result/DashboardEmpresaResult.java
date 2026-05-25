package br.com.atendepro.modules.dashboard.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record DashboardEmpresaResult(
        UUID empresaId,
        long clientesAtivos,
        long compromissosHoje,
        long compromissosProximos7Dias,
        long servicosAtivos,
        long produtosEstoqueBaixo,
        long produtosVencendo30Dias,
        long equipamentosManutencao30Dias,
        BigDecimal custosGeraisAtivos,
        BigDecimal custosAlimentacaoTransporteAtivos,
        Instant atualizadoEm
) {

    public static DashboardEmpresaResult de(UUID empresaId, MetricasDashboardEmpresaResult metricas, Instant atualizadoEm) {
        return new DashboardEmpresaResult(
                empresaId,
                metricas.clientesAtivos(),
                metricas.compromissosHoje(),
                metricas.compromissosProximos7Dias(),
                metricas.servicosAtivos(),
                metricas.produtosEstoqueBaixo(),
                metricas.produtosVencendo30Dias(),
                metricas.equipamentosManutencao30Dias(),
                metricas.custosGeraisAtivos(),
                metricas.custosAlimentacaoTransporteAtivos(),
                atualizadoEm
        );
    }
}
