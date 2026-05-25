package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.adminsaas.application.result.PlanoVendidoAdminSaasResult;

public record PlanoVendidoAdminSaasResponse(
        UUID planoId,
        String codigo,
        String nome,
        long totalAssinaturas,
        long assinaturasAtivas,
        BigDecimal mrr
) {

    public static PlanoVendidoAdminSaasResponse de(PlanoVendidoAdminSaasResult result) {
        return new PlanoVendidoAdminSaasResponse(
                result.planoId(),
                result.codigo(),
                result.nome(),
                result.totalAssinaturas(),
                result.assinaturasAtivas(),
                result.mrr()
        );
    }
}
