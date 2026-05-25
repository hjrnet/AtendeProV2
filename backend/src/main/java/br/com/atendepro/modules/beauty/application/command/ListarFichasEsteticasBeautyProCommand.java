package br.com.atendepro.modules.beauty.application.command;

import java.util.UUID;

public record ListarFichasEsteticasBeautyProCommand(UUID empresaId, UUID clienteId) {
}
