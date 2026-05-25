package br.com.atendepro.modules.nutri.application.command;

import java.util.UUID;

public record DetalharPlanoAlimentarNutriProCommand(
        UUID empresaId,
        UUID pacienteId,
        UUID planoId
) {
}
