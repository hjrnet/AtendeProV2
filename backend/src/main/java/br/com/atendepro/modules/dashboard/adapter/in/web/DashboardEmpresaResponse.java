package br.com.atendepro.modules.dashboard.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.dashboard.application.result.DashboardEmpresaResult;

public record DashboardEmpresaResponse(
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

    public static DashboardEmpresaResponse de(DashboardEmpresaResult result) {
        return new DashboardEmpresaResponse(
                result.empresaId(),
                result.clientesAtivos(),
                result.compromissosHoje(),
                result.compromissosProximos7Dias(),
                result.servicosAtivos(),
                result.produtosEstoqueBaixo(),
                result.produtosVencendo30Dias(),
                result.equipamentosManutencao30Dias(),
                result.custosGeraisAtivos(),
                result.custosAlimentacaoTransporteAtivos(),
                result.atualizadoEm()
        );
    }
}
