package br.com.atendepro.modules.pagamento.adapter.in.web;

import br.com.atendepro.modules.pagamento.application.command.RegistrarWebhookAsaasCommand;
import jakarta.validation.constraints.NotBlank;

public record WebhookAsaasRequest(
        @NotBlank String event,
        @NotBlank String paymentId,
        @NotBlank String subscriptionId,
        String payload
) {

    public RegistrarWebhookAsaasCommand paraCommand(String token) {
        return new RegistrarWebhookAsaasCommand(token, event, paymentId, subscriptionId, payload);
    }
}
