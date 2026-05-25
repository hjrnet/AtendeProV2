package br.com.atendepro.modules.servico.application.command;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;

public record CadastrarServicoProcedimentoCommand(
        UUID empresaId,
        String nome,
        String descricao,
        AreaCliente area,
        int duracaoMinutos,
        BigDecimal precoBase
) {
}
