package br.com.atendepro.modules.relacionamento.application.command;

import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.relacionamento.domain.model.TipoTarefaRelacionamento;

public record CriarTarefaRelacionamentoCommand(
        UUID empresaId,
        UUID clienteId,
        AreaCliente area,
        TipoTarefaRelacionamento tipo,
        String titulo,
        String descricao,
        LocalDate dataRecomendada,
        String origem
) {
}
