package br.com.atendepro.modules.suporte.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.suporte.application.command.AtualizarTriagemChamadoSuporteCommand;
import br.com.atendepro.modules.suporte.domain.model.PrioridadeChamadoSuporte;
import br.com.atendepro.modules.suporte.domain.model.StatusChamadoSuporte;

public record AtualizarTriagemChamadoSuporteRequest(
        StatusChamadoSuporte status,
        PrioridadeChamadoSuporte prioridade
) {

    public AtualizarTriagemChamadoSuporteCommand paraCommand(UUID chamadoId) {
        return new AtualizarTriagemChamadoSuporteCommand(chamadoId, status, prioridade);
    }
}
