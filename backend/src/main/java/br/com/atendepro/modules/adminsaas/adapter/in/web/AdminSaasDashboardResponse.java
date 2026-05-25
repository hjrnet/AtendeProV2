package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;

import br.com.atendepro.modules.adminsaas.application.result.AdminSaasDashboardResult;

public record AdminSaasDashboardResponse(
        BigDecimal mrr,
        long empresasAtivas,
        long empresasBloqueadas,
        long trialsAtivos,
        long chamadosAbertos,
        Instant atualizadoEm
) {

    public static AdminSaasDashboardResponse de(AdminSaasDashboardResult result) {
        return new AdminSaasDashboardResponse(
                result.mrr(),
                result.empresasAtivas(),
                result.empresasBloqueadas(),
                result.trialsAtivos(),
                result.chamadosAbertos(),
                result.atualizadoEm()
        );
    }
}
