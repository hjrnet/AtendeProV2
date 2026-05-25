package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.command.CalcularPrecificacaoBaseCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CalcularPrecificacaoBaseRequest(
        UUID empresaId,
        UUID servicoProcedimentoId,
        @Size(max = 160) String nomeProcedimento,
        @Valid @NotEmpty @Size(max = 50) List<ItemCustoPrecificacaoRequest> itensCusto
) {

    public CalcularPrecificacaoBaseCommand paraCommand() {
        return new CalcularPrecificacaoBaseCommand(
                empresaId,
                servicoProcedimentoId,
                nomeProcedimento,
                itensCusto.stream().map(ItemCustoPrecificacaoRequest::paraCommand).toList()
        );
    }
}
