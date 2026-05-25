package br.com.atendepro.modules.spaces.application.command;

import java.math.BigDecimal;
import java.util.UUID;

public record CalcularCustoHoraSpacesCommand(
        UUID empresaId,
        UUID recursoId,
        BigDecimal custoFixoMensal,
        int diasDisponiveisMes,
        BigDecimal horasDisponiveisDia
) {
}
