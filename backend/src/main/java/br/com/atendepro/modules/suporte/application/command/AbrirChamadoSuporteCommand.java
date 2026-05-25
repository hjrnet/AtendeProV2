package br.com.atendepro.modules.suporte.application.command;

import java.util.UUID;

import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;

public record AbrirChamadoSuporteCommand(
        UUID empresaId,
        UUID solicitanteUsuarioId,
        String solicitanteNome,
        String solicitanteEmail,
        String titulo,
        String descricao,
        PrioridadeChamadoSuporte prioridade,
        String categoria
) {
}
