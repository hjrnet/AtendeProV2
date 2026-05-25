package br.com.atendepro.modules.nutri.application.result;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.StatusOperacionalNutriPro;

public record VisaoNutriProResult(
        UUID empresaId,
        String empresaNome,
        StatusOperacionalNutriPro statusOperacional,
        List<IndicadorNutriProResult> indicadores,
        List<AtalhoNutriProResult> atalhosPrioritarios,
        List<AtalhoNutriProResult> proximasEvolucoes,
        List<PacienteNutriResumoResult> pacientesRecentes,
        Instant atualizadoEm
) {
}
