package br.com.atendepro.modules.relacionamento.application.command;

import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.relacionamento.domain.model.CanalContatoRelacionamento;

public record RegistrarContatoRelacionamentoCommand(
        UUID empresaId,
        UUID clienteId,
        AreaCliente area,
        CanalContatoRelacionamento canal,
        String templateCodigo,
        String mensagem,
        String observacoes
) {
}
