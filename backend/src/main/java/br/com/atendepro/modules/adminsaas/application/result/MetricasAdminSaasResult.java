package br.com.atendepro.modules.adminsaas.application.result;

import java.math.BigDecimal;

public record MetricasAdminSaasResult(
        BigDecimal mrr,
        long empresasAtivas,
        long empresasBloqueadas,
        long trialsAtivos,
        long chamadosAbertos
) {

    public static MetricasAdminSaasResult zeradas() {
        return new MetricasAdminSaasResult(BigDecimal.ZERO, 0, 0, 0, 0);
    }
}
