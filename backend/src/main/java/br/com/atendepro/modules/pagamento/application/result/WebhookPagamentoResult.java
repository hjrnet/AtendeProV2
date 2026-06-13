package br.com.atendepro.modules.pagamento.application.result;

import java.util.UUID;

import br.com.atendepro.modules.pagamento.domain.model.TipoEventoPagamentoGateway;

public record WebhookPagamentoResult(
        UUID eventoId,
        TipoEventoPagamentoGateway tipo,
        boolean processado,
        boolean duplicado,
        String mensagem
) {
}
