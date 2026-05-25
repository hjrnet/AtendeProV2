package br.com.atendepro.modules.suporte.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.suporte.application.command.RegistrarMensagemChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.domain.model.OrigemMensagemChamadoSuporte;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarMensagemChamadoSuporteRequest(
        UUID autorUsuarioId,
        @Size(max = 160) String autorNome,
        OrigemMensagemChamadoSuporte origem,
        @NotBlank @Size(max = 3000) String mensagem
) {

    public RegistrarMensagemChamadoSuporteCommand paraCommand(UUID chamadoId) {
        return new RegistrarMensagemChamadoSuporteCommand(
                chamadoId,
                autorUsuarioId,
                autorNome,
                origem,
                mensagem
        );
    }
}
