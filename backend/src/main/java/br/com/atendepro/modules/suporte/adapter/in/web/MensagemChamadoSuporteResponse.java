package br.com.atendepro.modules.suporte.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.suporte.application.result.MensagemChamadoSuporteResult;
import br.com.atendepro.modules.suporte.domain.model.OrigemMensagemChamadoSuporte;

public record MensagemChamadoSuporteResponse(
        UUID id,
        UUID chamadoId,
        UUID autorUsuarioId,
        String autorNome,
        OrigemMensagemChamadoSuporte origem,
        String mensagem,
        Instant criadoEm
) {

    public static MensagemChamadoSuporteResponse de(MensagemChamadoSuporteResult result) {
        return new MensagemChamadoSuporteResponse(
                result.id(),
                result.chamadoId(),
                result.autorUsuarioId(),
                result.autorNome(),
                result.origem(),
                result.mensagem(),
                result.criadoEm()
        );
    }
}
