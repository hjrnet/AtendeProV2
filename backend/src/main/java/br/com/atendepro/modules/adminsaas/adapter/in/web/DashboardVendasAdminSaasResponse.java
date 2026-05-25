package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import br.com.atendepro.modules.adminsaas.application.result.DashboardVendasAdminSaasResult;

public record DashboardVendasAdminSaasResponse(
        BigDecimal mrr,
        long trialsIniciados,
        long trialsConvertidos,
        BigDecimal taxaConversaoTrial,
        long assinaturasAtivas,
        long assinaturasCanceladas,
        BigDecimal taxaChurn,
        List<PlanoVendidoAdminSaasResponse> planosVendidos,
        Instant atualizadoEm
) {

    public static DashboardVendasAdminSaasResponse de(DashboardVendasAdminSaasResult result) {
        return new DashboardVendasAdminSaasResponse(
                result.mrr(),
                result.trialsIniciados(),
                result.trialsConvertidos(),
                result.taxaConversaoTrial(),
                result.assinaturasAtivas(),
                result.assinaturasCanceladas(),
                result.taxaChurn(),
                result.planosVendidos().stream().map(PlanoVendidoAdminSaasResponse::de).toList(),
                result.atualizadoEm()
        );
    }
}
