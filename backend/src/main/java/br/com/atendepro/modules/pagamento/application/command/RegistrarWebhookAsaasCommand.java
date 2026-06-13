package br.com.atendepro.modules.pagamento.application.command;

public record RegistrarWebhookAsaasCommand(
        String token,
        String event,
        String paymentId,
        String subscriptionId,
        String payload
) {
}
