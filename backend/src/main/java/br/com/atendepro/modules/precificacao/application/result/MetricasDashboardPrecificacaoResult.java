package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.util.List;

public record MetricasDashboardPrecificacaoResult(
        long totalSimulacoes,
        BigDecimal custoMedio,
        BigDecimal precoMedioRecomendado,
        BigDecimal precoMedioVenda,
        BigDecimal lucroMedio,
        BigDecimal margemMediaPercentual,
        long simulacoesSaudaveis,
        long simulacoesComAlerta,
        List<DistribuicaoStatusPrecificacaoResult> distribuicaoStatus,
        List<SimulacaoDashboardPrecificacaoResult> simulacoesRecentes
) {

    public MetricasDashboardPrecificacaoResult {
        distribuicaoStatus = distribuicaoStatus == null ? List.of() : List.copyOf(distribuicaoStatus);
        simulacoesRecentes = simulacoesRecentes == null ? List.of() : List.copyOf(simulacoesRecentes);
    }
}
