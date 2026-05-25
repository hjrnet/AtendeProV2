package br.com.atendepro.modules.nutri.application.command;

import java.math.BigDecimal;

import br.com.atendepro.modules.nutri.domain.model.TipoItemPlanoAlimentarNutriPro;

public record CriarItemPlanoAlimentarNutriProCommand(
        TipoItemPlanoAlimentarNutriPro tipoItem,
        String nome,
        String grupo,
        String unidadeMedida,
        BigDecimal quantidadeBase,
        BigDecimal quantidade,
        BigDecimal energiaKcalBase,
        BigDecimal proteinasBase,
        BigDecimal carboidratosBase,
        BigDecimal lipidiosBase,
        String observacoes,
        int ordenacao
) {
}
