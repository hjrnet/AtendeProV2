package br.com.atendepro.modules.nutri.application.command;

import java.util.UUID;

public record DetalharAvaliacaoAntropometricaNutriProCommand(
        UUID empresaId,
        UUID pacienteId,
        UUID avaliacaoId
) {
}
