package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.domain.model.CustoRealPrecificacao;

public record CustoRealPrecificacaoResult(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        int duracaoMinutos,
        BigDecimal precoBaseServico,
        BigDecimal custoInsumos,
        BigDecimal custoSala,
        BigDecimal custoTempoProfissional,
        BigDecimal custoDeslocamento,
        BigDecimal custoAlimentacao,
        BigDecimal taxas,
        List<ItemCustoPrecificacaoResult> itensCusto,
        BigDecimal custoTotal,
        Instant calculadoEm
) {

    public static CustoRealPrecificacaoResult de(
            CustoRealPrecificacao custoReal,
            ServicoPrecificacaoResult servico
    ) {
        return new CustoRealPrecificacaoResult(
                custoReal.calculoBase().empresaId(),
                custoReal.calculoBase().servicoProcedimentoId(),
                custoReal.calculoBase().nomeProcedimento(),
                custoReal.duracaoMinutos(),
                servico == null ? null : servico.precoBase(),
                custoReal.custoInsumos(),
                custoReal.custoSala(),
                custoReal.custoTempoProfissional(),
                custoReal.custoDeslocamento(),
                custoReal.custoAlimentacao(),
                custoReal.taxas(),
                custoReal.calculoBase().itensCusto().stream().map(ItemCustoPrecificacaoResult::de).toList(),
                custoReal.custoTotal(),
                custoReal.calculoBase().calculadoEm()
        );
    }
}
