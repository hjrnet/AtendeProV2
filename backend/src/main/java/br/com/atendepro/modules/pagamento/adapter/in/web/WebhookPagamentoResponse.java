package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.pagamento.application.result.WebhookPagamentoResult;

public record WebhookPagamentoResponse(
        UUID eventoId,
        String tipo,
        boolean processado,
        boolean duplicado,
        String mensagem
) {

    public static WebhookPagamentoResponse de(WebhookPagamentoResult result) {
        return new WebhookPagamentoResponse(
                result.eventoId(),
                result.tipo().name(),
                result.processado(),
                result.duplicado(),
                result.mensagem()
        );
    }
}
