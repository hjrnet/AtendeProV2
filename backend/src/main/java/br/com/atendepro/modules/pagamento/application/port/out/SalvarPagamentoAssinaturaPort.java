package br.com.atendepro.modules.pagamento.application.port.out;

import br.com.atendepro.modules.pagamento.domain.model.PagamentoAssinatura;

public interface SalvarPagamentoAssinaturaPort {

    void salvarPagamentoAssinatura(PagamentoAssinatura pagamento);
}
