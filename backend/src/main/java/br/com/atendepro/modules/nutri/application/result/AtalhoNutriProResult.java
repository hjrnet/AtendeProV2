package br.com.atendepro.modules.nutri.application.result;

public record AtalhoNutriProResult(
        String codigo,
        String titulo,
        String descricao,
        String status,
        String destino
) {
}
