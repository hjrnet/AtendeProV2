package br.com.atendepro.modules.nutri.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.nutri.application.command.CriarRefeicaoPlanoAlimentarNutriProCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CriarRefeicaoPlanoAlimentarNutriProRequest(
        @NotBlank @Size(max = 120) String nome,
        @Size(max = 20) String horario,
        @Size(max = 500) String observacoes,
        @Min(0) int ordenacao,
        @Valid @NotEmpty List<CriarItemPlanoAlimentarNutriProRequest> itens
) {

    public CriarRefeicaoPlanoAlimentarNutriProCommand paraCommand() {
        return new CriarRefeicaoPlanoAlimentarNutriProCommand(
                nome,
                horario,
                observacoes,
                ordenacao,
                itens.stream().map(CriarItemPlanoAlimentarNutriProRequest::paraCommand).toList()
        );
    }
}
