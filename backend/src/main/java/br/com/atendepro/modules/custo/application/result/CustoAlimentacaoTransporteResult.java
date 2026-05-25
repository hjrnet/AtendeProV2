package br.com.atendepro.modules.custo.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.custo.domain.model.CustoAlimentacaoTransporte;
import br.com.atendepro.modules.custo.domain.model.PeriodicidadeCustoPessoal;
import br.com.atendepro.modules.custo.domain.model.TipoCustoPessoal;

public record CustoAlimentacaoTransporteResult(
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

    public static CustoAlimentacaoTransporteResult de(CustoAlimentacaoTransporte custo) {
        return new CustoAlimentacaoTransporteResult(
                custo.id(),
                custo.empresaId(),
                custo.profissionalId(),
                custo.descricao(),
                custo.tipo(),
                custo.periodicidade(),
                custo.valor(),
                custo.ativo(),
                custo.criadoEm(),
                custo.atualizadoEm()
        );
    }
}
