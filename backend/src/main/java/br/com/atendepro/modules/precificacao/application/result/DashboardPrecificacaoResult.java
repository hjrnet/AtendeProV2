package br.com.atendepro.modules.precificacao.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record DashboardPrecificacaoResult(
        UUID empresaId,
        long totalSimulacoes,
        BigDecimal custoMedio,
        BigDecimal precoMedioRecomendado,
        BigDecimal precoMedioVenda,
        BigDecimal lucroMedio,
        BigDecimal margemMediaPercentual,
        long simulacoesSaudaveis,
        long simulacoesComAlerta,
        List<DistribuicaoStatusPrecificacaoResult> distribuicaoStatus,
        List<SimulacaoDashboardPrecificacaoResult> simulacoesRecentes,
        Instant atualizadoEm
) {

    public static DashboardPrecificacaoResult de(
            UUID empresaId,
            MetricasDashboardPrecificacaoResult metricas,
            Instant atualizadoEm
    ) {
        return new DashboardPrecificacaoResult(
                empresaId,
                metricas.totalSimulacoes(),
                metricas.custoMedio(),
                metricas.precoMedioRecomendado(),
                metricas.precoMedioVenda(),
                metricas.lucroMedio(),
                metricas.margemMediaPercentual(),
                metricas.simulacoesSaudaveis(),
                metricas.simulacoesComAlerta(),
                metricas.distribuicaoStatus(),
                metricas.simulacoesRecentes(),
                atualizadoEm
        );
    }
}
