package br.com.atendepro.modules.beauty.application.command;

import java.util.UUID;

public record DetalharProtocoloBeautyProCommand(UUID empresaId, UUID clienteId, UUID protocoloId) {
}
