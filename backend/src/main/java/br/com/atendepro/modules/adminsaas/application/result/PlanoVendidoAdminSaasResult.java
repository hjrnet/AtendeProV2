package br.com.atendepro.modules.adminsaas.application.result;

import java.math.BigDecimal;
import java.util.UUID;

public record PlanoVendidoAdminSaasResult(
        UUID planoId,
        String codigo,
        String nome,
        long totalAssinaturas,
        long assinaturasAtivas,
        BigDecimal mrr
) {
}
