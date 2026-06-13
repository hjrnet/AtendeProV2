package br.com.atendepro.modules.pagamento.application.port.out;

import br.com.atendepro.modules.pagamento.domain.model.CobrancaPagamento;

public interface AtualizarCobrancaPagamentoPort {

    void atualizarCobrancaPagamento(CobrancaPagamento cobranca);
}
