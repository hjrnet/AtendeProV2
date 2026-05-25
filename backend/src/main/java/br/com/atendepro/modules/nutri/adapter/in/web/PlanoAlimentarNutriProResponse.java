package br.com.atendepro.modules.nutri.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.PlanoAlimentarNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.StatusPlanoAlimentarNutriPro;

public record PlanoAlimentarNutriProResponse(
        UUID id,
        UUID empresaId,
        UUID pacienteId,
        String objetivo,
        String descricao,
        StatusPlanoAlimentarNutriPro status,
        String statusRotulo,
        List<RefeicaoPlanoAlimentarNutriProResponse> refeicoes,
        BigDecimal energiaTotalKcal,
        BigDecimal proteinasTotal,
        BigDecimal carboidratosTotal,
        BigDecimal lipidiosTotal,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static PlanoAlimentarNutriProResponse de(PlanoAlimentarNutriProResult result) {
        return new PlanoAlimentarNutriProResponse(
                result.id(),
                result.empresaId(),
                result.pacienteId(),
                result.objetivo(),
                result.descricao(),
                result.status(),
                result.statusRotulo(),
                result.refeicoes().stream().map(RefeicaoPlanoAlimentarNutriProResponse::de).toList(),
                result.energiaTotalKcal(),
                result.proteinasTotal(),
                result.carboidratosTotal(),
                result.lipidiosTotal(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
