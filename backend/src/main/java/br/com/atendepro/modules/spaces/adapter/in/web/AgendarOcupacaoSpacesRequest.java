package br.com.atendepro.modules.spaces.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.command.AgendarOcupacaoSpacesCommand;
import br.com.atendepro.modules.spaces.domain.model.StatusOcupacaoSpaces;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AgendarOcupacaoSpacesRequest(
        UUID empresaId,
        @NotNull UUID recursoId,
        UUID pacoteId,
        @NotBlank String nomeParceiro,
        @NotNull Instant inicioEm,
        @NotNull Instant fimEm,
        StatusOcupacaoSpaces status,
        String observacao
) {

    AgendarOcupacaoSpacesCommand paraCommand() {
        return new AgendarOcupacaoSpacesCommand(
                empresaId,
                recursoId,
                pacoteId,
                nomeParceiro,
                inicioEm,
                fimEm,
                status,
                observacao
        );
    }
}
