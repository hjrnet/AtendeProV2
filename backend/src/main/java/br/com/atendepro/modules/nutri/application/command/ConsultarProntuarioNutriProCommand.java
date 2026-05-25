package br.com.atendepro.modules.nutri.application.command;

import java.util.UUID;

public record ConsultarProntuarioNutriProCommand(
        UUID empresaId,
        UUID pacienteId
) {
}
