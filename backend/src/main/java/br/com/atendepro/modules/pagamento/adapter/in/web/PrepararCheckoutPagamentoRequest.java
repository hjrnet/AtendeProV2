package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.pagamento.application.command.PrepararCheckoutPagamentoCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PrepararCheckoutPagamentoRequest(
        @NotNull UUID empresaId,
        @NotNull UUID planoId,
        @Email @NotBlank String emailResponsavel,
        @NotBlank String nomeResponsavel,
        @NotBlank String documentoResponsavel,
        String telefoneResponsavel,
        String formaPagamentoPreferida
) {

    public PrepararCheckoutPagamentoCommand paraCommand() {
        return new PrepararCheckoutPagamentoCommand(
                empresaId,
                planoId,
                emailResponsavel,
                nomeResponsavel,
                documentoResponsavel,
                telefoneResponsavel,
                formaPagamentoPreferida
        );
    }
}
