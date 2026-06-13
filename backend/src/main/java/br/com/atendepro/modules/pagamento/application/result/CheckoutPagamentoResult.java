package br.com.atendepro.modules.pagamento.application.result;

import java.util.UUID;

import br.com.atendepro.modules.pagamento.domain.model.AmbientePagamento;
import br.com.atendepro.modules.pagamento.domain.model.PagamentoAssinatura;
import br.com.atendepro.modules.pagamento.domain.model.ProvedorPagamento;
import br.com.atendepro.modules.pagamento.domain.model.StatusPagamentoAssinatura;

public record CheckoutPagamentoResult(
        UUID checkoutId,
        UUID pagamentoAssinaturaId,
        UUID assinaturaId,
        StatusPagamentoAssinatura status,
        String urlPagamento,
        AmbientePagamento ambiente,
        ProvedorPagamento provedor
) {

    public static CheckoutPagamentoResult de(PagamentoAssinatura pagamento, UUID assinaturaId) {
        return new CheckoutPagamentoResult(
                pagamento.id(),
                pagamento.id(),
                assinaturaId,
                pagamento.status(),
                "/sandbox/pagamentos/" + pagamento.checkoutExternoId(),
                pagamento.ambiente(),
                pagamento.provedor()
        );
    }
}
