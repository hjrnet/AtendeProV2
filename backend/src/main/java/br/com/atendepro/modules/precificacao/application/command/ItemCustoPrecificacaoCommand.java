package br.com.atendepro.modules.precificacao.application.command;

import java.math.BigDecimal;

import br.com.atendepro.modules.precificacao.domain.model.CategoriaItemPrecificacao;

public record ItemCustoPrecificacaoCommand(
        String descricao,
        CategoriaItemPrecificacao categoria,
        BigDecimal valor
) {
}
