package br.com.atendepro.modules.beauty.application.command;

import java.util.UUID;

public record ConsultarSegurancaOperacionalBeautyProCommand(
        UUID empresaId,
        UUID clienteId
) {
}
