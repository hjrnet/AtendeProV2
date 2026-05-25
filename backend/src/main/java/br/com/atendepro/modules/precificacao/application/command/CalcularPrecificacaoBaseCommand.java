package br.com.atendepro.modules.precificacao.application.command;

import java.util.List;
import java.util.UUID;

public record CalcularPrecificacaoBaseCommand(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        List<ItemCustoPrecificacaoCommand> itensCusto
) {
}
