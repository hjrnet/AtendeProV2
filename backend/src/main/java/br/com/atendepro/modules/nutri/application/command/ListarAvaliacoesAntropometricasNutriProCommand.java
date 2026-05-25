package br.com.atendepro.modules.nutri.application.command;

import java.util.UUID;

public record ListarAvaliacoesAntropometricasNutriProCommand(
        UUID empresaId,
        UUID pacienteId
) {
}
