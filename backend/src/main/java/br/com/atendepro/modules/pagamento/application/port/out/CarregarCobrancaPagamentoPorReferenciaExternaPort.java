package br.com.atendepro.modules.pagamento.application.port.out;

import java.util.Optional;

import br.com.atendepro.modules.pagamento.domain.model.CobrancaPagamento;

public interface CarregarCobrancaPagamentoPorReferenciaExternaPort {

    Optional<CobrancaPagamento> carregarPorCobrancaExterna(String cobrancaExternaId);
}
