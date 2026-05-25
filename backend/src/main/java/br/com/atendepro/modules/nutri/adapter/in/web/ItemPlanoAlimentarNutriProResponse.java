package br.com.atendepro.modules.nutri.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.ItemPlanoAlimentarNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.TipoItemPlanoAlimentarNutriPro;

public record ItemPlanoAlimentarNutriProResponse(
        UUID id,
        TipoItemPlanoAlimentarNutriPro tipoItem,
        String tipoItemRotulo,
        String nome,
        String grupo,
        String unidadeMedida,
        BigDecimal quantidade,
        BigDecimal quantidadeBase,
        BigDecimal energiaKcal,
        BigDecimal proteinas,
        BigDecimal carboidratos,
        BigDecimal lipidios,
        String observacoes,
        int ordenacao
) {

    public static ItemPlanoAlimentarNutriProResponse de(ItemPlanoAlimentarNutriProResult result) {
        return new ItemPlanoAlimentarNutriProResponse(
                result.id(),
                result.tipoItem(),
                result.tipoItemRotulo(),
                result.nome(),
                result.grupo(),
                result.unidadeMedida(),
                result.quantidade(),
                result.quantidadeBase(),
                result.energiaKcal(),
                result.proteinas(),
                result.carboidratos(),
                result.lipidios(),
                result.observacoes(),
                result.ordenacao()
        );
    }
}
