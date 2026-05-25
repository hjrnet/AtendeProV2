package br.com.atendepro.modules.nutri.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.nutri.application.result.AvaliacaoAntropometricaNutriProResult;

public record AvaliacoesAntropometricasNutriProResponse(
        List<AvaliacaoAntropometricaNutriProResponse> itens
) {

    public static AvaliacoesAntropometricasNutriProResponse de(List<AvaliacaoAntropometricaNutriProResult> results) {
        return new AvaliacoesAntropometricasNutriProResponse(
                results.stream().map(AvaliacaoAntropometricaNutriProResponse::de).toList()
        );
    }
}
