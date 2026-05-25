package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.command.CalcularMargemLucroCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CalcularMargemLucroRequest(
        UUID empresaId,
        UUID servicoProcedimentoId,
        @Size(max = 160) String nomeProcedimento,
        @Min(1) Integer duracaoMinutos,
        @NotNull @DecimalMin("0.00") BigDecimal custoInsumos,
        @NotNull @DecimalMin("0.00") BigDecimal custoSalaPorHora,
        @NotNull @DecimalMin("0.00") BigDecimal valorHoraProfissional,
        @NotNull @DecimalMin("0.00") BigDecimal custoDeslocamento,
        @NotNull @DecimalMin("0.00") BigDecimal custoAlimentacao,
        @NotNull @DecimalMin("0.00") BigDecimal taxas,
        @NotNull @DecimalMin(value = "0.01") BigDecimal precoVenda
) {

    public CalcularMargemLucroCommand paraCommand() {
        return new CalcularMargemLucroCommand(
                empresaId,
                servicoProcedimentoId,
                nomeProcedimento,
                duracaoMinutos,
                custoInsumos,
                custoSalaPorHora,
                valorHoraProfissional,
                custoDeslocamento,
                custoAlimentacao,
                taxas,
                precoVenda
        );
    }
}
