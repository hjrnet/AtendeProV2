package br.com.atendepro.modules.suporte.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.suporte.domain.model.MensagemChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.OrigemMensagemChamadoSuporte;

public record MensagemChamadoSuporteResult(
        UUID id,
        UUID chamadoId,
        UUID autorUsuarioId,
        String autorNome,
        OrigemMensagemChamadoSuporte origem,
        String mensagem,
        Instant criadoEm
) {

    public static MensagemChamadoSuporteResult de(MensagemChamadoSuporte mensagem) {
        return new MensagemChamadoSuporteResult(
                mensagem.id(),
                mensagem.chamadoId(),
                mensagem.autorUsuarioId(),
                mensagem.autorNome(),
                mensagem.origem(),
                mensagem.mensagem(),
                mensagem.criadoEm()
        );
    }
}
