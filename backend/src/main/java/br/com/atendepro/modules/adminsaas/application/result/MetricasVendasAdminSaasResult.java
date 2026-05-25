package br.com.atendepro.modules.adminsaas.application.result;

import java.math.BigDecimal;
import java.util.List;

public record MetricasVendasAdminSaasResult(
        BigDecimal mrr,
        long trialsIniciados,
        long trialsConvertidos,
        long assinaturasAtivas,
        long assinaturasCanceladas,
        List<PlanoVendidoAdminSaasResult> planosVendidos
) {

    public MetricasVendasAdminSaasResult {
        planosVendidos = List.copyOf(planosVendidos);
    }
}
