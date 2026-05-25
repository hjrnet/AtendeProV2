package br.com.atendepro.modules.nutri.application.command;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.StatusPlanoAlimentarNutriPro;

public record CriarPlanoAlimentarNutriProCommand(
        UUID empresaId,
        UUID pacienteId,
        String objetivo,
        String descricao,
        StatusPlanoAlimentarNutriPro status,
        List<CriarRefeicaoPlanoAlimentarNutriProCommand> refeicoes
) {
}
