package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.domain.model.AnaliseMargemLucroPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;

public record MargemLucroPrecificacaoResult(
        UUID empresaId,
        UUID servicoProcedimentoId,
        String nomeProcedimento,
        int duracaoMinutos,
        BigDecimal precoBaseServico,
        BigDecimal custoTotal,
        BigDecimal precoMinimo,
        BigDecimal precoVenda,
        BigDecimal lucroEstimado,
        BigDecimal margemRealPercentual,
        StatusMargemPrecificacao status,
        List<AlertaPrecificacaoResult> alertas,
        List<ItemCustoPrecificacaoResult> itensCusto,
        Instant calculadoEm
) {

    public static MargemLucroPrecificacaoResult de(
            AnaliseMargemLucroPrecificacao analise,
            ServicoPrecificacaoResult servico
    ) {
        var custoReal = analise.precoMinimo().custoReal();
        return new MargemLucroPrecificacaoResult(
                custoReal.calculoBase().empresaId(),
                custoReal.calculoBase().servicoProcedimentoId(),
                custoReal.calculoBase().nomeProcedimento(),
                custoReal.duracaoMinutos(),
                servico == null ? null : servico.precoBase(),
                custoReal.custoTotal(),
                analise.precoMinimo().precoMinimo(),
                analise.precoVenda(),
                analise.lucroEstimado(),
                analise.margemRealPercentual(),
                analise.status(),
                analise.alertas().stream().map(AlertaPrecificacaoResult::de).toList(),
                custoReal.calculoBase().itensCusto().stream().map(ItemCustoPrecificacaoResult::de).toList(),
                custoReal.calculoBase().calculadoEm()
        );
    }
}
