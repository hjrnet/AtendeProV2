package br.com.atendepro.modules.pagamento.application.command;

import java.util.UUID;

public record PrepararCheckoutPagamentoCommand(
        UUID empresaId,
        UUID planoId,
        String emailResponsavel,
        String nomeResponsavel,
        String documentoResponsavel,
        String telefoneResponsavel,
        String formaPagamentoPreferida
) {
}
