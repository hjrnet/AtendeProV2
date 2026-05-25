package br.com.atendepro.modules.nutri.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.command.CriarAvaliacaoAntropometricaNutriProCommand;
import br.com.atendepro.modules.nutri.domain.model.ObjetivoNutricionalNutriPro;
import br.com.atendepro.modules.nutri.domain.model.SexoBiologicoNutriPro;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarAvaliacaoAntropometricaNutriProRequest(
        @NotNull
        @DecimalMin("1.00")
        @DecimalMax("500.00")
        BigDecimal pesoKg,

        @NotNull
        @DecimalMin("30.00")
        @DecimalMax("250.00")
        BigDecimal alturaCm,

        @Min(1)
        @Max(120)
        int idade,

        @NotNull
        SexoBiologicoNutriPro sexo,

        @NotNull
        ObjetivoNutricionalNutriPro objetivo,

        @DecimalMin("1.00")
        @DecimalMax("3.00")
        BigDecimal fatorAtividade,

        @Size(max = 1000)
        String observacoes
) {

    public CriarAvaliacaoAntropometricaNutriProCommand paraCommand(UUID empresaId, UUID pacienteId) {
        return new CriarAvaliacaoAntropometricaNutriProCommand(
                empresaId,
                pacienteId,
                pesoKg,
                alturaCm,
                idade,
                sexo,
                objetivo,
                fatorAtividade,
                observacoes
        );
    }
}
