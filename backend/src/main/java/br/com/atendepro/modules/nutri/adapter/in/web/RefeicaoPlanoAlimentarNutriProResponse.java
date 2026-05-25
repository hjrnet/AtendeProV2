package br.com.atendepro.modules.nutri.adapter.in.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.RefeicaoPlanoAlimentarNutriProResult;

public record RefeicaoPlanoAlimentarNutriProResponse(
        UUID id,
        String nome,
        String horario,
        String observacoes,
        int ordenacao,
        List<ItemPlanoAlimentarNutriProResponse> itens,
        BigDecimal energiaTotalKcal,
        BigDecimal proteinasTotal,
        BigDecimal carboidratosTotal,
        BigDecimal lipidiosTotal
) {

    public static RefeicaoPlanoAlimentarNutriProResponse de(RefeicaoPlanoAlimentarNutriProResult result) {
        return new RefeicaoPlanoAlimentarNutriProResponse(
                result.id(),
                result.nome(),
                result.horario(),
                result.observacoes(),
                result.ordenacao(),
                result.itens().stream().map(ItemPlanoAlimentarNutriProResponse::de).toList(),
                result.energiaTotalKcal(),
                result.proteinasTotal(),
                result.carboidratosTotal(),
                result.lipidiosTotal()
        );
    }
}
