package br.com.atendepro.modules.plano.adapter.in.web;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.atendepro.modules.plano.application.command.CriarPlanoCommand;
import br.com.atendepro.modules.plano.domain.model.ModuloPlano;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CriarPlanoRequest(
        @NotBlank String codigo,
        @NotBlank String nome,
        String descricao,
        @NotNull @DecimalMin("0.00") BigDecimal valorMensal,
        @Min(0) int limiteUsuarios,
        @Min(0) int limiteClientes,
        @Min(0) int limiteProfissionais,
        Boolean ativo,
        Boolean estudante,
        String marcaDaguaAcademica,
        @NotEmpty Set<String> modulos
) {

    CriarPlanoCommand paraCommand() {
        return new CriarPlanoCommand(
                codigo,
                nome,
                descricao,
                valorMensal,
                limiteUsuarios,
                limiteClientes,
                limiteProfissionais,
                ativo == null || ativo,
                estudante != null && estudante,
                marcaDaguaAcademica,
                mapearModulos()
        );
    }

    private Set<ModuloPlano> mapearModulos() {
        return modulos.stream().map(ModuloPlano::deCodigo).collect(Collectors.toUnmodifiableSet());
    }
}
