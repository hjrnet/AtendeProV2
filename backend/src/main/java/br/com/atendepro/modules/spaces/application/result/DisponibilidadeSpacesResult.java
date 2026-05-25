package br.com.atendepro.modules.spaces.application.result;

import java.time.Instant;
import java.util.UUID;

public record DisponibilidadeSpacesResult(
        UUID empresaId,
        UUID recursoId,
        Instant inicioEm,
        Instant fimEm,
        boolean disponivel,
        String motivo
) {
}
