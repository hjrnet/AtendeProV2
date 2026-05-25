package br.com.atendepro.modules.suporte.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.suporte.application.result.DetalheChamadoSuporteResult;

public record DetalheChamadoSuporteResponse(
        ChamadoSuporteResponse chamado,
        List<MensagemChamadoSuporteResponse> mensagens
) {

    public static DetalheChamadoSuporteResponse de(DetalheChamadoSuporteResult result) {
        return new DetalheChamadoSuporteResponse(
                ChamadoSuporteResponse.de(result.chamado()),
                result.mensagens().stream().map(MensagemChamadoSuporteResponse::de).toList()
        );
    }
}
