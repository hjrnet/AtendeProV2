package br.com.atendepro.modules.spaces.application.command;

import java.time.Instant;
import java.util.UUID;

public record ConsultarDisponibilidadeSpacesCommand(
        UUID empresaId,
        UUID recursoId,
        Instant inicioEm,
        Instant fimEm
) {
}
