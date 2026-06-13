package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.pagamento.application.result.CheckoutPagamentoResult;

public record CheckoutPagamentoResponse(
        UUID checkoutId,
        UUID pagamentoAssinaturaId,
        UUID assinaturaId,
        String status,
        String urlPagamento,
        String ambiente,
        String provedor
) {

    public static CheckoutPagamentoResponse de(CheckoutPagamentoResult result) {
        return new CheckoutPagamentoResponse(
                result.checkoutId(),
                result.pagamentoAssinaturaId(),
                result.assinaturaId(),
                result.status().name(),
                result.urlPagamento(),
                result.ambiente().name(),
                result.provedor().name()
        );
    }
}
