package br.com.atendepro.modules.nutri.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.PlanoAlimentarNutriPro;
import br.com.atendepro.modules.nutri.domain.model.StatusPlanoAlimentarNutriPro;

public record PlanoAlimentarNutriProResult(
        UUID id,
        UUID empresaId,
        UUID pacienteId,
        String objetivo,
        String descricao,
        StatusPlanoAlimentarNutriPro status,
        String statusRotulo,
        List<RefeicaoPlanoAlimentarNutriProResult> refeicoes,
        BigDecimal energiaTotalKcal,
        BigDecimal proteinasTotal,
        BigDecimal carboidratosTotal,
        BigDecimal lipidiosTotal,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static PlanoAlimentarNutriProResult de(PlanoAlimentarNutriPro plano) {
        return new PlanoAlimentarNutriProResult(
                plano.id(),
                plano.empresaId(),
                plano.pacienteId(),
                plano.objetivo(),
                plano.descricao(),
                plano.status(),
                plano.status().rotulo(),
                plano.refeicoes().stream().map(RefeicaoPlanoAlimentarNutriProResult::de).toList(),
                plano.energiaTotalKcal(),
                plano.proteinasTotal(),
                plano.carboidratosTotal(),
                plano.lipidiosTotal(),
                plano.criadoEm(),
                plano.atualizadoEm()
        );
    }
}
