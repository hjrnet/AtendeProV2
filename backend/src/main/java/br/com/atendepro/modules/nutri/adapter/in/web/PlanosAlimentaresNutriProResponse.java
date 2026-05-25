package br.com.atendepro.modules.nutri.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.nutri.application.result.PlanoAlimentarNutriProResult;

public record PlanosAlimentaresNutriProResponse(
        List<PlanoAlimentarNutriProResponse> itens
) {

    public static PlanosAlimentaresNutriProResponse de(List<PlanoAlimentarNutriProResult> results) {
        return new PlanosAlimentaresNutriProResponse(
                results.stream().map(PlanoAlimentarNutriProResponse::de).toList()
        );
    }
}
