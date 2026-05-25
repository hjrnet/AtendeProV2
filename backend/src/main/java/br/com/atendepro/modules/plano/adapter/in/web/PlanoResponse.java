package br.com.atendepro.modules.plano.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.plano.application.result.PlanoResult;
import br.com.atendepro.modules.plano.domain.model.ModuloPlano;

public record PlanoResponse(
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
        List<String> modulos,
        Instant criadoEm,
        Instant atualizadoEm
) {

    static PlanoResponse de(PlanoResult result) {
        return new PlanoResponse(
                result.id(),
                result.codigo(),
                result.nome(),
                result.descricao(),
                result.valorMensal(),
                result.limiteUsuarios(),
                result.limiteClientes(),
                result.limiteProfissionais(),
                result.ativo(),
                result.estudante(),
                result.marcaDaguaAcademica(),
                result.modulos().stream().map(ModuloPlano::codigo).sorted(Comparator.naturalOrder()).toList(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
