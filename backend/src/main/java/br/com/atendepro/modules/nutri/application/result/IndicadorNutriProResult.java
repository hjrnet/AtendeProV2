package br.com.atendepro.modules.nutri.application.result;

public record IndicadorNutriProResult(
        String codigo,
        String titulo,
        long valor,
        String descricao,
        String status
) {
}
