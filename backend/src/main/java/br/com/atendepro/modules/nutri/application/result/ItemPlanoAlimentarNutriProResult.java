package br.com.atendepro.modules.nutri.application.result;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.ItemPlanoAlimentarNutriPro;
import br.com.atendepro.modules.nutri.domain.model.TipoItemPlanoAlimentarNutriPro;

public record ItemPlanoAlimentarNutriProResult(
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

    public static ItemPlanoAlimentarNutriProResult de(ItemPlanoAlimentarNutriPro item) {
        return new ItemPlanoAlimentarNutriProResult(
                item.id(),
                item.tipoItem(),
                item.tipoItem().rotulo(),
                item.nome(),
                item.grupo(),
                item.unidadeMedida(),
                item.quantidade(),
                item.quantidadeBase(),
                item.energiaKcal(),
                item.proteinas(),
                item.carboidratos(),
                item.lipidios(),
                item.observacoes(),
                item.ordenacao()
        );
    }
}
