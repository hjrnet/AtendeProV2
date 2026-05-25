package br.com.atendepro.modules.nutri.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.AvaliacaoAntropometricaNutriPro;
import br.com.atendepro.modules.nutri.domain.model.ObjetivoNutricionalNutriPro;
import br.com.atendepro.modules.nutri.domain.model.SexoBiologicoNutriPro;

public record AvaliacaoAntropometricaNutriProResult(
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

    public static AvaliacaoAntropometricaNutriProResult de(AvaliacaoAntropometricaNutriPro avaliacao) {
        return new AvaliacaoAntropometricaNutriProResult(
                avaliacao.id(),
                avaliacao.empresaId(),
                avaliacao.pacienteId(),
                avaliacao.pesoKg(),
                avaliacao.alturaCm(),
                avaliacao.idade(),
                avaliacao.sexo(),
                avaliacao.sexo().rotulo(),
                avaliacao.imc(),
                avaliacao.objetivo(),
                avaliacao.objetivo().rotulo(),
                avaliacao.fatorAtividade(),
                avaliacao.gebKcal(),
                avaliacao.tmbKcal(),
                avaliacao.getKcal(),
                avaliacao.metaEnergeticaKcal(),
                avaliacao.formula(),
                avaliacao.aviso(),
                avaliacao.observacoes(),
                avaliacao.criadoEm(),
                avaliacao.atualizadoEm()
        );
    }
}
