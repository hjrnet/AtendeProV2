package br.com.atendepro.modules.nutri.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

public record ItemPlanoAlimentarNutriPro(
        UUID id,
        UUID empresaId,
        UUID refeicaoId,
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
        BigDecimal energiaKcal,
        BigDecimal proteinas,
        BigDecimal carboidratos,
        BigDecimal lipidios,
        String observacoes,
        int ordenacao
) {

    public ItemPlanoAlimentarNutriPro {
        if (id == null) {
            throw new IllegalArgumentException("id do item do plano alimentar e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do item do plano alimentar e obrigatoria");
        }
        if (refeicaoId == null) {
            throw new IllegalArgumentException("refeicao do item do plano alimentar e obrigatoria");
        }
        if (tipoItem == null) {
            throw new IllegalArgumentException("tipo do item do plano alimentar e obrigatorio");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome do item do plano alimentar e obrigatorio");
        }
        if (unidadeMedida == null || unidadeMedida.isBlank()) {
            throw new IllegalArgumentException("unidade do item do plano alimentar e obrigatoria");
        }
        quantidadeBase = valorPositivo(quantidadeBase, "quantidade base");
        quantidade = valorPositivo(quantidade, "quantidade");
        energiaKcalBase = valorNaoNegativo(energiaKcalBase, "energia base");
        proteinasBase = valorNaoNegativo(proteinasBase, "proteinas base");
        carboidratosBase = valorNaoNegativo(carboidratosBase, "carboidratos base");
        lipidiosBase = valorNaoNegativo(lipidiosBase, "lipidios base");
        energiaKcal = valorNaoNegativo(energiaKcal, "energia");
        proteinas = valorNaoNegativo(proteinas, "proteinas");
        carboidratos = valorNaoNegativo(carboidratos, "carboidratos");
        lipidios = valorNaoNegativo(lipidios, "lipidios");
        if (ordenacao < 0) {
            throw new IllegalArgumentException("ordenacao do item do plano alimentar nao pode ser negativa");
        }
        nome = nome.trim();
        grupo = grupo == null || grupo.isBlank() ? null : grupo.trim();
        unidadeMedida = unidadeMedida.trim();
        observacoes = observacoes == null || observacoes.isBlank() ? null : observacoes.trim();
    }

    public static ItemPlanoAlimentarNutriPro criar(
            UUID empresaId,
            UUID refeicaoId,
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
        BigDecimal base = valorPositivo(quantidadeBase, "quantidade base");
        BigDecimal quantidadeNormalizada = valorPositivo(quantidade, "quantidade");
        BigDecimal fator = quantidadeNormalizada.divide(base, 8, RoundingMode.HALF_EVEN);
        return new ItemPlanoAlimentarNutriPro(
                UUID.randomUUID(),
                empresaId,
                refeicaoId,
                tipoItem,
                nome,
                grupo,
                unidadeMedida,
                base,
                quantidadeNormalizada,
                valorNaoNegativo(energiaKcalBase, "energia base"),
                valorNaoNegativo(proteinasBase, "proteinas base"),
                valorNaoNegativo(carboidratosBase, "carboidratos base"),
                valorNaoNegativo(lipidiosBase, "lipidios base"),
                proporcional(energiaKcalBase, fator),
                proporcional(proteinasBase, fator),
                proporcional(carboidratosBase, fator),
                proporcional(lipidiosBase, fator),
                observacoes,
                ordenacao
        );
    }

    private static BigDecimal proporcional(BigDecimal valorBase, BigDecimal fator) {
        return valorNaoNegativo(valorBase, "valor base").multiply(fator).setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal valorPositivo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException(campo + " do item do plano alimentar deve ser positiva");
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal valorNaoNegativo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() < 0) {
            throw new IllegalArgumentException(campo + " do item do plano alimentar nao pode ser negativo");
        }
        return valor.setScale(2, RoundingMode.HALF_EVEN);
    }
}
