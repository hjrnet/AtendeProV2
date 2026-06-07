package br.com.atendepro.modules.integracao.application.result;

import java.time.Instant;
import java.util.List;

import br.com.atendepro.modules.integracao.domain.model.TipoIntegracaoFutura;

public record IntegracaoFuturaStatusResult(
        TipoIntegracaoFutura tipo,
        String nome,
        boolean configurada,
        String provedor,
        String ambiente,
        String mensagem,
        List<String> proximasEtapas,
        Instant consultadoEm
) {

    public IntegracaoFuturaStatusResult {
        proximasEtapas = List.copyOf(proximasEtapas);
    }
}
