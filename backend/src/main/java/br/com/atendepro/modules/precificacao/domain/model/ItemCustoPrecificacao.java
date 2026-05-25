package br.com.atendepro.modules.precificacao.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ItemCustoPrecificacao(
        String descricao,
        CategoriaItemPrecificacao categoria,
        BigDecimal valor
) {

    public ItemCustoPrecificacao {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("descricao do item de custo e obrigatoria");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("categoria do item de custo e obrigatoria");
        }
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException("valor do item de custo nao pode ser negativo");
        }
        descricao = descricao.trim();
        valor = valor.setScale(2, RoundingMode.HALF_EVEN);
    }
}
