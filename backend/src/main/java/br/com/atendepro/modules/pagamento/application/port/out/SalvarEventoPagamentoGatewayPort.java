package br.com.atendepro.modules.pagamento.application.port.out;

import br.com.atendepro.modules.pagamento.domain.model.EventoPagamentoGateway;

public interface SalvarEventoPagamentoGatewayPort {

    void salvarEventoPagamentoGateway(EventoPagamentoGateway evento);
}
