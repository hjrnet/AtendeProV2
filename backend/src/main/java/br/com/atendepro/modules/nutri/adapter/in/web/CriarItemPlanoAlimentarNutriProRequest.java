package br.com.atendepro.modules.nutri.adapter.in.web;

import java.math.BigDecimal;

import br.com.atendepro.modules.nutri.application.command.CriarItemPlanoAlimentarNutriProCommand;
import br.com.atendepro.modules.nutri.domain.model.TipoItemPlanoAlimentarNutriPro;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarItemPlanoAlimentarNutriProRequest(
        @NotNull TipoItemPlanoAlimentarNutriPro tipoItem,
        @NotBlank @Size(max = 160) String nome,
        @Size(max = 80) String grupo,
        @NotBlank @Size(max = 30) String unidadeMedida,
        @NotNull @DecimalMin("0.01") BigDecimal quantidadeBase,
        @NotNull @DecimalMin("0.01") BigDecimal quantidade,
        @NotNull @DecimalMin("0.00") BigDecimal energiaKcalBase,
        @NotNull @DecimalMin("0.00") BigDecimal proteinasBase,
        @NotNull @DecimalMin("0.00") BigDecimal carboidratosBase,
        @NotNull @DecimalMin("0.00") BigDecimal lipidiosBase,
        @Size(max = 500) String observacoes,
        @Min(0) int ordenacao
) {

    public CriarItemPlanoAlimentarNutriProCommand paraCommand() {
        return new CriarItemPlanoAlimentarNutriProCommand(
                tipoItem,
                nome,
                grupo,
                unidadeMedida,
                quantidadeBase,
                quantidade,
                energiaKcalBase,
                proteinasBase,
                carboidratosBase,
                lipidiosBase,
                observacoes,
                ordenacao
        );
    }
}
