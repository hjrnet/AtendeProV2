package br.com.atendepro.modules.beauty.application.command;

import java.util.UUID;

public record ListarProtocolosBeautyProCommand(UUID empresaId, UUID clienteId) {
}
