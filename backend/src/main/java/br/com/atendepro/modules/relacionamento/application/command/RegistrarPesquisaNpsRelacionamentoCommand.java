package br.com.atendepro.modules.relacionamento.application.command;

import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;

public record RegistrarPesquisaNpsRelacionamentoCommand(
        UUID empresaId,
        UUID clienteId,
        AreaCliente area,
        int nota,
        String comentario,
        String origem
) {
}
