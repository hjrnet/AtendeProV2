package br.com.atendepro.modules.suporte.application.result;

import java.util.List;

import br.com.atendepro.modules.suporte.domain.model.ChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte;

public record DetalheChamadoSuporteResult(
        ChamadoSuporteResult chamado,
        List<MensagemChamadoSuporteResult> mensagens
) {

    public DetalheChamadoSuporteResult {
        mensagens = List.copyOf(mensagens);
    }

    public static DetalheChamadoSuporteResult de(ChamadoSuporte chamado, List<MensagemChamadoSuporte> mensagens) {
        return new DetalheChamadoSuporteResult(
                ChamadoSuporteResult.de(chamado),
                mensagens.stream().map(MensagemChamadoSuporteResult::de).toList()
        );
    }
}
