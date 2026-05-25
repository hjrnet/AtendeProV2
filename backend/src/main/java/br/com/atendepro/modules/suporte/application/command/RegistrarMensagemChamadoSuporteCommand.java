package br.com.atendepro.modules.suporte.application.command;

import java.util.UUID;

import br.com.atendepro.modules.suporte.domain.model.OrigemMensagemChamadoSuporte;

public record RegistrarMensagemChamadoSuporteCommand(
        UUID chamadoId,
        UUID autorUsuarioId,
        String autorNome,
        OrigemMensagemChamadoSuporte origem,
        String mensagem
) {
}
