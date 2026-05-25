package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.domain.model.PrecoMinimoPrecificacao;

public record PrecoMinimoPrecificacaoResult(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        int duracaoMinutos,
        BigDecimal precoBaseServico,
        BigDecimal custoTotal,
        BigDecimal precoMinimo,
        List<ItemCustoPrecificacaoResult> itensCusto,
        Instant calculadoEm
) {

    public static PrecoMinimoPrecificacaoResult de(
            PrecoMinimoPrecificacao precoMinimo,
            ServicoPrecificacaoResult servico
    ) {
        var custoReal = precoMinimo.custoReal();
        return new PrecoMinimoPrecificacaoResult(
                custoReal.calculoBase().empresaId(),
                custoReal.calculoBase().servicoProcedimentoId(),
                custoReal.calculoBase().nomeProcedimento(),
                custoReal.duracaoMinutos(),
                servico == null ? null : servico.precoBase(),
                custoReal.custoTotal(),
                precoMinimo.precoMinimo(),
                custoReal.calculoBase().itensCusto().stream().map(ItemCustoPrecificacaoResult::de).toList(),
                custoReal.calculoBase().calculadoEm()
        );
    }
}
