package br.com.atendepro.modules.nutri.application.command;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.ObjetivoNutricionalNutriPro;
import br.com.atendepro.modules.nutri.domain.model.SexoBiologicoNutriPro;

public record CriarAvaliacaoAntropometricaNutriProCommand(
        UUID empresaId,
        UUID pacienteId,
        BigDecimal pesoKg,
        BigDecimal alturaCm,
        int idade,
        SexoBiologicoNutriPro sexo,
        ObjetivoNutricionalNutriPro objetivo,
        BigDecimal fatorAtividade,
        String observacoes
) {
}
