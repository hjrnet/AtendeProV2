package br.com.atendepro.modules.spaces.application.command;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.StatusOcupacaoSpaces;

public record AgendarOcupacaoSpacesCommand(
        UUID empresaId,
        UUID recursoId,
        UUID pacoteId,
        String nomeParceiro,
        Instant inicioEm,
        Instant fimEm,
        StatusOcupacaoSpaces status,
        String observacao
) {
}
