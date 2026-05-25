package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;

import br.com.atendepro.modules.precificacao.application.command.ItemCustoPrecificacaoCommand;
import br.com.atendepro.modules.precificacao.domain.model.CategoriaItemPrecificacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemCustoPrecificacaoRequest(
        @NotBlank @Size(max = 160) String descricao,
        @NotNull CategoriaItemPrecificacao categoria,
        @NotNull @DecimalMin("0.00") BigDecimal valor
) {

    public ItemCustoPrecificacaoCommand paraCommand() {
        return new ItemCustoPrecificacaoCommand(descricao, categoria, valor);
    }
}
