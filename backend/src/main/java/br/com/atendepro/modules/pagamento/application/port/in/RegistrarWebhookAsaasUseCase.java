package br.com.atendepro.modules.pagamento.application.port.in;

import br.com.atendepro.modules.pagamento.application.command.RegistrarWebhookAsaasCommand;
import br.com.atendepro.modules.pagamento.application.result.WebhookPagamentoResult;

public interface RegistrarWebhookAsaasUseCase {

    WebhookPagamentoResult registrarWebhook(RegistrarWebhookAsaasCommand command);
}
