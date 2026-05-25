package br.com.atendepro.modules.adminsaas.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record DashboardVendasAdminSaasResult(
        BigDecimal mrr,
        long trialsIniciados,
        long trialsConvertidos,
        BigDecimal taxaConversaoTrial,
        long assinaturasAtivas,
        long assinaturasCanceladas,
        BigDecimal taxaChurn,
        List<PlanoVendidoAdminSaasResult> planosVendidos,
        Instant atualizadoEm
) {

    public DashboardVendasAdminSaasResult {
        planosVendidos = List.copyOf(planosVendidos);
    }
}
