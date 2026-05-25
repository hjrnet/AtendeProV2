package br.com.atendepro.modules.nutri.application.command;

import java.util.UUID;

public record ListarPacientesNutriProCommand(
        UUID empresaId,
        String busca
) {
}
