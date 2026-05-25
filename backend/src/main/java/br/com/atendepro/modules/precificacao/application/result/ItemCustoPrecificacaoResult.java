package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;

import br.com.atendepro.modules.precificacao.domain.model.CategoriaItemPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.ItemCustoPrecificacao;

public record ItemCustoPrecificacaoResult(
        String descricao,
        CategoriaItemPrecificacao categoria,
        BigDecimal valor
) {

    public static ItemCustoPrecificacaoResult de(ItemCustoPrecificacao item) {
        return new ItemCustoPrecificacaoResult(item.descricao(), item.categoria(), item.valor());
    }
}
