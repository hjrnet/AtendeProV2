package br.com.atendepro.modules.nutri.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

public record RefeicaoPlanoAlimentarNutriPro(
        UUID id,
        UUID empresaId,
        UUID planoId,
        String nome,
        String horario,
        String observacoes,
        int ordenacao,
        List<ItemPlanoAlimentarNutriPro> itens,
        BigDecimal energiaTotalKcal,
        BigDecimal proteinasTotal,
        BigDecimal carboidratosTotal,
        BigDecimal lipidiosTotal
) {

    public RefeicaoPlanoAlimentarNutriPro {
        if (id == null) {
            throw new IllegalArgumentException("id da refeicao do plano alimentar e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa da refeicao do plano alimentar e obrigatoria");
        }
        if (planoId == null) {
            throw new IllegalArgumentException("plano da refeicao e obrigatorio");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome da refeicao e obrigatorio");
        }
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("refeicao do plano alimentar deve ter ao menos um item");
        }
        if (ordenacao < 0) {
            throw new IllegalArgumentException("ordenacao da refeicao nao pode ser negativa");
        }
        nome = nome.trim();
        horario = horario == null || horario.isBlank() ? null : horario.trim();
        observacoes = observacoes == null || observacoes.isBlank() ? null : observacoes.trim();
        itens = List.copyOf(itens);
        energiaTotalKcal = total(itens.stream().map(ItemPlanoAlimentarNutriPro::energiaKcal).toList());
        proteinasTotal = total(itens.stream().map(ItemPlanoAlimentarNutriPro::proteinas).toList());
        carboidratosTotal = total(itens.stream().map(ItemPlanoAlimentarNutriPro::carboidratos).toList());
        lipidiosTotal = total(itens.stream().map(ItemPlanoAlimentarNutriPro::lipidios).toList());
    }

    public static RefeicaoPlanoAlimentarNutriPro criar(
            UUID empresaId,
            UUID planoId,
            String nome,
            String horario,
            String observacoes,
            int ordenacao,
            List<ItemPlanoAlimentarNutriPro> itens
    ) {
        return new RefeicaoPlanoAlimentarNutriPro(
                UUID.randomUUID(),
                empresaId,
                planoId,
                nome,
                horario,
                observacoes,
                ordenacao,
                itens,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    private static BigDecimal total(List<BigDecimal> valores) {
        return valores.stream().reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_EVEN);
    }
}
