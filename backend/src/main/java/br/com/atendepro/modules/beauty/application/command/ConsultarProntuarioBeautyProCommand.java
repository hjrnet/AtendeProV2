package br.com.atendepro.modules.beauty.application.command;

import java.util.UUID;

public record ConsultarProntuarioBeautyProCommand(UUID empresaId, UUID clienteId) {
}
