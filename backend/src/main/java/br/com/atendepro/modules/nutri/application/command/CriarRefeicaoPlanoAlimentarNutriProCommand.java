package br.com.atendepro.modules.nutri.application.command;

import java.util.List;

public record CriarRefeicaoPlanoAlimentarNutriProCommand(
        String nome,
        String horario,
        String observacoes,
        int ordenacao,
        List<CriarItemPlanoAlimentarNutriProCommand> itens
) {
}
