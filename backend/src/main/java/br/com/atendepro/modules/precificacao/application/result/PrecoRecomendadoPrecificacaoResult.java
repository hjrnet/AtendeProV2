package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.domain.model.PrecoRecomendadoPrecificacao;

public record PrecoRecomendadoPrecificacaoResult(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        int duracaoMinutos,
        BigDecimal precoBaseServico,
        BigDecimal custoTotal,
        BigDecimal precoMinimo,
        BigDecimal margemDesejadaPercentual,
        BigDecimal precoRecomendado,
        List<ItemCustoPrecificacaoResult> itensCusto,
        Instant calculadoEm
) {

    public static PrecoRecomendadoPrecificacaoResult de(
            PrecoRecomendadoPrecificacao precoRecomendado,
            ServicoPrecificacaoResult servico
    ) {
        var custoReal = precoRecomendado.precoMinimo().custoReal();
        return new PrecoRecomendadoPrecificacaoResult(
                custoReal.calculoBase().empresaId(),
                custoReal.calculoBase().servicoProcedimentoId(),
                custoReal.calculoBase().nomeProcedimento(),
                custoReal.duracaoMinutos(),
                servico == null ? null : servico.precoBase(),
                custoReal.custoTotal(),
                precoRecomendado.precoMinimo().precoMinimo(),
                precoRecomendado.margemDesejadaPercentual(),
                precoRecomendado.precoRecomendado(),
                custoReal.calculoBase().itensCusto().stream().map(ItemCustoPrecificacaoResult::de).toList(),
                custoReal.calculoBase().calculadoEm()
        );
    }
}
