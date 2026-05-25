package br.com.atendepro.modules.spaces.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.command.SimularParceiroSpacesCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SimularParceiroSpacesRequest(
        UUID empresaId,
        @NotNull UUID pacoteId,
        @Min(1) int quantidadePacotesMes,
        @Min(1) int atendimentosMes,
        @NotNull @DecimalMin(value = "0.00") BigDecimal ticketMedio,
        @NotNull @DecimalMin(value = "0.00") BigDecimal custosOperacionaisParceiro
) {

    SimularParceiroSpacesCommand paraCommand() {
        return new SimularParceiroSpacesCommand(
                empresaId,
                pacoteId,
                quantidadePacotesMes,
                atendimentosMes,
                ticketMedio,
                custosOperacionaisParceiro
        );
    }
}
