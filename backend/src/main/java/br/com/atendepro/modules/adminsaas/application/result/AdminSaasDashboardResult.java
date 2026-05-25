package br.com.atendepro.modules.adminsaas.application.result;

import java.math.BigDecimal;
import java.time.Instant;

public record AdminSaasDashboardResult(
        BigDecimal mrr,
        long empresasAtivas,
        long empresasBloqueadas,
        long trialsAtivos,
        long chamadosAbertos,
        Instant atualizadoEm
) {
}
