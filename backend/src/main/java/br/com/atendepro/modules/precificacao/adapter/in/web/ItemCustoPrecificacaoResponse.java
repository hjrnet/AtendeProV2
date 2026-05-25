package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;

import br.com.atendepro.modules.precificacao.application.result.ItemCustoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.domain.model.CategoriaItemPrecificacao;

public record ItemCustoPrecificacaoResponse(
        String descricao,
        CategoriaItemPrecificacao categoria,
        BigDecimal valor
) {

    public static ItemCustoPrecificacaoResponse de(ItemCustoPrecificacaoResult result) {
        return new ItemCustoPrecificacaoResponse(result.descricao(), result.categoria(), result.valor());
    }
}
