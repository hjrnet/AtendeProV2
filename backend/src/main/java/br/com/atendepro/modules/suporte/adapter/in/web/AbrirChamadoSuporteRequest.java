package br.com.atendepro.modules.suporte.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.suporte.application.command.AbrirChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AbrirChamadoSuporteRequest(
        UUID empresaId,
        UUID solicitanteUsuarioId,
        @Size(max = 160) String solicitanteNome,
        @Email @Size(max = 180) String solicitanteEmail,
        @NotBlank @Size(max = 180) String titulo,
        @NotBlank @Size(max = 3000) String descricao,
        PrioridadeChamadoSuporte prioridade,
        @Size(max = 80) String categoria
) {

    public AbrirChamadoSuporteCommand paraCommand() {
        return new AbrirChamadoSuporteCommand(
                empresaId,
                solicitanteUsuarioId,
                solicitanteNome,
                solicitanteEmail,
                titulo,
                descricao,
                prioridade,
                categoria
        );
    }
}
