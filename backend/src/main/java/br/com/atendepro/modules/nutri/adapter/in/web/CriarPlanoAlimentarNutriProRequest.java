package br.com.atendepro.modules.nutri.adapter.in.web;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.command.CriarPlanoAlimentarNutriProCommand;
import br.com.atendepro.modules.nutri.domain.model.StatusPlanoAlimentarNutriPro;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CriarPlanoAlimentarNutriProRequest(
        @NotBlank @Size(max = 160) String objetivo,
        @Size(max = 1000) String descricao,
        StatusPlanoAlimentarNutriPro status,
        @Valid @NotEmpty List<CriarRefeicaoPlanoAlimentarNutriProRequest> refeicoes
) {

    public CriarPlanoAlimentarNutriProCommand paraCommand(UUID empresaId, UUID pacienteId) {
        return new CriarPlanoAlimentarNutriProCommand(
                empresaId,
                pacienteId,
                objetivo,
                descricao,
                status,
                refeicoes.stream().map(CriarRefeicaoPlanoAlimentarNutriProRequest::paraCommand).toList()
        );
    }
}
