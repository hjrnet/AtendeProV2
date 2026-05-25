package br.com.atendepro.modules.nutri.application.command;

import java.util.UUID;

public record ListarPlanosAlimentaresNutriProCommand(
        UUID empresaId,
        UUID pacienteId
) {
}
