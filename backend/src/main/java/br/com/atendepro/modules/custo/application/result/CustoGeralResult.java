package br.com.atendepro.modules.custo.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

import br.com.atendepro.modules.custo.domain.model.CustoGeral;
import br.com.atendepro.modules.custo.domain.model.TipoCustoGeral;

public record CustoGeralResult(
        UUID id,
        UUID empresaId,
        String descricao,
        TipoCustoGeral tipo,
        String categoria,
        BigDecimal valor,
        YearMonth competencia,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static CustoGeralResult de(CustoGeral custo) {
        return new CustoGeralResult(
                custo.id(),
                custo.empresaId(),
                custo.descricao(),
                custo.tipo(),
                custo.categoria(),
                custo.valor(),
                custo.competencia(),
                custo.ativo(),
                custo.criadoEm(),
                custo.atualizadoEm()
        );
    }
}
