package br.com.atendepro.modules.integracao.adapter.in.web;

import java.time.Instant;
import java.util.List;

import br.com.atendepro.modules.integracao.application.result.IntegracaoFuturaStatusResult;
import br.com.atendepro.modules.integracao.domain.model.TipoIntegracaoFutura;

public record IntegracaoFuturaStatusResponse(
        TipoIntegracaoFutura tipo,
        String nome,
        boolean configurada,
        String provedor,
        String ambiente,
        String mensagem,
        List<String> proximasEtapas,
        Instant consultadoEm
) {

    public static IntegracaoFuturaStatusResponse de(IntegracaoFuturaStatusResult result) {
        return new IntegracaoFuturaStatusResponse(
                result.tipo(),
                result.nome(),
                result.configurada(),
                result.provedor(),
                result.ambiente(),
                result.mensagem(),
                result.proximasEtapas(),
                result.consultadoEm()
        );
    }
}
