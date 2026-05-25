package br.com.atendepro.modules.custo.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.custo.application.result.CustoAlimentacaoTransporteResult;
import br.com.atendepro.modules.custo.domain.model.PeriodicidadeCustoPessoal;
import br.com.atendepro.modules.custo.domain.model.TipoCustoPessoal;

public record CustoAlimentacaoTransporteResponse(
        UUID id,
        UUID empresaId,
        UUID profissionalId,
        String descricao,
        TipoCustoPessoal tipo,
        PeriodicidadeCustoPessoal periodicidade,
        BigDecimal valor,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static CustoAlimentacaoTransporteResponse de(CustoAlimentacaoTransporteResult result) {
        return new CustoAlimentacaoTransporteResponse(
                result.id(),
                result.empresaId(),
                result.profissionalId(),
                result.descricao(),
                result.tipo(),
                result.periodicidade(),
                result.valor(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
