package br.com.atendepro.modules.nutri.application.result;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.RefeicaoPlanoAlimentarNutriPro;

public record RefeicaoPlanoAlimentarNutriProResult(
        UUID id,
        String nome,
        String horario,
        String observacoes,
        int ordenacao,
        List<ItemPlanoAlimentarNutriProResult> itens,
        BigDecimal energiaTotalKcal,
        BigDecimal proteinasTotal,
        BigDecimal carboidratosTotal,
        BigDecimal lipidiosTotal
) {

    public static RefeicaoPlanoAlimentarNutriProResult de(RefeicaoPlanoAlimentarNutriPro refeicao) {
        return new RefeicaoPlanoAlimentarNutriProResult(
                refeicao.id(),
                refeicao.nome(),
                refeicao.horario(),
                refeicao.observacoes(),
                refeicao.ordenacao(),
                refeicao.itens().stream().map(ItemPlanoAlimentarNutriProResult::de).toList(),
                refeicao.energiaTotalKcal(),
                refeicao.proteinasTotal(),
                refeicao.carboidratosTotal(),
                refeicao.lipidiosTotal()
        );
    }
}
