package br.com.atendepro.modules.suporte.application.command;

import java.util.UUID;

import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;

public record AtualizarTriagemChamadoSuporteCommand(
        UUID chamadoId,
        StatusChamadoSuporte status,
        PrioridadeChamadoSuporte prioridade
) {
}
