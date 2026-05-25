package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.command.CalcularPrecoRecomendadoCommand;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CalcularPrecoRecomendadoRequest(
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
        @NotNull @DecimalMin("0.00") @DecimalMax("99.99") BigDecimal margemDesejadaPercentual
) {

    public CalcularPrecoRecomendadoCommand paraCommand() {
        return new CalcularPrecoRecomendadoCommand(
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
                margemDesejadaPercentual
        );
    }
}
