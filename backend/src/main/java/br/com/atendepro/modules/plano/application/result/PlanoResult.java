package br.com.atendepro.modules.plano.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import br.com.atendepro.modules.plano.domain.model.ModuloPlano;
import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;

public record PlanoResult(
        UUID id,
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
        Set<ModuloPlano> modulos,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static PlanoResult de(PlanoAssinatura plano) {
        return new PlanoResult(
                plano.id(),
                plano.codigo(),
                plano.nome(),
                plano.descricao(),
                plano.valorMensal(),
                plano.limiteUsuarios(),
                plano.limiteClientes(),
                plano.limiteProfissionais(),
                plano.ativo(),
                plano.estudante(),
                plano.marcaDaguaAcademica(),
                plano.modulos(),
                plano.criadoEm(),
                plano.atualizadoEm()
        );
    }
}
