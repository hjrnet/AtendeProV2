package br.com.atendepro.modules.pagamento.application.port.in;

import br.com.atendepro.modules.pagamento.application.command.PrepararCheckoutPagamentoCommand;
import br.com.atendepro.modules.pagamento.application.result.CheckoutPagamentoResult;

public interface PrepararCheckoutPagamentoUseCase {

    CheckoutPagamentoResult prepararCheckout(PrepararCheckoutPagamentoCommand command);
}
