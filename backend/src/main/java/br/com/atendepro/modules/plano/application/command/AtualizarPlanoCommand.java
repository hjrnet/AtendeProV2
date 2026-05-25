package br.com.atendepro.modules.plano.application.command;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import br.com.atendepro.modules.plano.domain.model.ModuloPlano;

public record AtualizarPlanoCommand(
        UUID id,
        String codigo,
        String nome,
        String descricao,
        BigDecimal valorMensal,
        int limiteUsuarios,
        int limiteClientes,
        int limiteProfissionais,
        boolean ativo,
        Set<ModuloPlano> modulos
) {

    public AtualizarPlanoCommand {
        if (id == null) {
            throw new IllegalArgumentException("id do plano e obrigatorio");
        }
    }
}
