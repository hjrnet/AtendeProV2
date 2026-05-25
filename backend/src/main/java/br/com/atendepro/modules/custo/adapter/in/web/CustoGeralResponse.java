package br.com.atendepro.modules.custo.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

import br.com.atendepro.modules.custo.application.result.CustoGeralResult;
import br.com.atendepro.modules.custo.domain.model.TipoCustoGeral;

public record CustoGeralResponse(
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

    public static CustoGeralResponse de(CustoGeralResult result) {
        return new CustoGeralResponse(
                result.id(),
                result.empresaId(),
                result.descricao(),
                result.tipo(),
                result.categoria(),
                result.valor(),
                result.competencia(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
