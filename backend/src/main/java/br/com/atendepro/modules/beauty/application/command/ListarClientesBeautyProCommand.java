package br.com.atendepro.modules.beauty.application.command;

import java.util.UUID;

public record ListarClientesBeautyProCommand(UUID empresaId, String busca) {
}
