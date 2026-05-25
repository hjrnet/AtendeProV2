package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.domain.model.CalculoPrecificacao;

public record CalculoPrecificacaoBaseResult(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        Integer duracaoMinutosServico,
        BigDecimal precoBaseServico,
        List<ItemCustoPrecificacaoResult> itensCusto,
        BigDecimal custoTotal,
        Instant calculadoEm
) {

    public static CalculoPrecificacaoBaseResult de(
            CalculoPrecificacao calculo,
            ServicoPrecificacaoResult servico
    ) {
        return new CalculoPrecificacaoBaseResult(
                calculo.empresaId(),
                calculo.servicoProcedimentoId(),
                calculo.nomeProcedimento(),
                servico == null ? null : servico.duracaoMinutos(),
                servico == null ? null : servico.precoBase(),
                calculo.itensCusto().stream().map(ItemCustoPrecificacaoResult::de).toList(),
                calculo.custoTotal(),
                calculo.calculadoEm()
        );
    }
}
