package br.com.atendepro.modules.pagamento.application.port.out;

import java.util.Optional;

import br.com.atendepro.modules.pagamento.domain.model.EventoPagamentoGateway;
import br.com.atendepro.modules.pagamento.domain.model.ProvedorPagamento;
import br.com.atendepro.modules.pagamento.domain.model.TipoEventoPagamentoGateway;

public interface CarregarEventoPagamentoGatewayPort {

    Optional<EventoPagamentoGateway> carregarEvento(
            ProvedorPagamento provedor,
            TipoEventoPagamentoGateway tipo,
            String eventoExternoId
    );
}
