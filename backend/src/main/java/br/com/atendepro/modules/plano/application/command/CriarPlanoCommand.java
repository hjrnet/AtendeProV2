package br.com.atendepro.modules.plano.application.command;

import java.math.BigDecimal;
import java.util.Set;

import br.com.atendepro.modules.plano.domain.model.ModuloPlano;

public record CriarPlanoCommand(
        String codigo,
        String nome,
        String descricao,
        BigDecimal valorMensal,
        int limiteUsuarios,
        int limiteClientes,
        int limiteProfissionais,
        boolean ativo,
        boolean estudante,
        String marcaDaguaAcademica,
        Set<ModuloPlano> modulos
) {
}
