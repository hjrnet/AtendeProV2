package br.com.atendepro.modules.spaces.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.command.CalcularCustoHoraSpacesCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CalcularCustoHoraSpacesRequest(
        UUID empresaId,
        UUID recursoId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal custoFixoMensal,
        @Min(1) int diasDisponiveisMes,
        @NotNull @DecimalMin(value = "0.01") BigDecimal horasDisponiveisDia
) {

    CalcularCustoHoraSpacesCommand paraCommand() {
        return new CalcularCustoHoraSpacesCommand(
                empresaId,
                recursoId,
                custoFixoMensal,
                diasDisponiveisMes,
                horasDisponiveisDia
        );
    }
}
