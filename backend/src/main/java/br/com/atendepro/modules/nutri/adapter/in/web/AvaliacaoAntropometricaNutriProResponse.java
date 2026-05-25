package br.com.atendepro.modules.nutri.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.AvaliacaoAntropometricaNutriProResult;
import br.com.atendepro.modules.nutri.domain.model.ObjetivoNutricionalNutriPro;
import br.com.atendepro.modules.nutri.domain.model.SexoBiologicoNutriPro;

public record AvaliacaoAntropometricaNutriProResponse(
        UUID id,
        UUID empresaId,
        UUID pacienteId,
        BigDecimal pesoKg,
        BigDecimal alturaCm,
        int idade,
        SexoBiologicoNutriPro sexo,
        String sexoRotulo,
        BigDecimal imc,
        ObjetivoNutricionalNutriPro objetivo,
        String objetivoRotulo,
        BigDecimal fatorAtividade,
        BigDecimal gebKcal,
        BigDecimal tmbKcal,
        BigDecimal getKcal,
        BigDecimal metaEnergeticaKcal,
        String formula,
        String aviso,
        String observacoes,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static AvaliacaoAntropometricaNutriProResponse de(AvaliacaoAntropometricaNutriProResult result) {
        return new AvaliacaoAntropometricaNutriProResponse(
                result.id(),
                result.empresaId(),
                result.pacienteId(),
                result.pesoKg(),
                result.alturaCm(),
                result.idade(),
                result.sexo(),
                result.sexoRotulo(),
                result.imc(),
                result.objetivo(),
                result.objetivoRotulo(),
                result.fatorAtividade(),
                result.gebKcal(),
                result.tmbKcal(),
                result.getKcal(),
                result.metaEnergeticaKcal(),
                result.formula(),
                result.aviso(),
                result.observacoes(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
