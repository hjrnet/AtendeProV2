package br.com.atendepro.modules.pagamento.application.port.out;

import java.util.Optional;

import br.com.atendepro.modules.pagamento.domain.model.PagamentoAssinatura;

public interface CarregarPagamentoAssinaturaPorAssinaturaExternaPort {

    Optional<PagamentoAssinatura> carregarPorAssinaturaExterna(String assinaturaExternaId);
}
