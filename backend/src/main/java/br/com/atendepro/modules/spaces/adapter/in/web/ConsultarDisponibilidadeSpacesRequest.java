package br.com.atendepro.modules.spaces.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.command.ConsultarDisponibilidadeSpacesCommand;
import jakarta.validation.constraints.NotNull;

public record ConsultarDisponibilidadeSpacesRequest(
        UUID empresaId,
        @NotNull UUID recursoId,
        @NotNull Instant inicioEm,
        @NotNull Instant fimEm
) {

    ConsultarDisponibilidadeSpacesCommand paraCommand() {
        return new ConsultarDisponibilidadeSpacesCommand(empresaId, recursoId, inicioEm, fimEm);
    }
}
