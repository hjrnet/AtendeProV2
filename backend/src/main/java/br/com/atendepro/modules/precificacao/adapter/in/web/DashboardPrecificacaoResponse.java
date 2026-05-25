package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.precificacao.application.result.DashboardPrecificacaoResult;

public record DashboardPrecificacaoResponse(
        UUID empresaId,
        long totalSimulacoes,
        BigDecimal custoMedio,
        BigDecimal precoMedioRecomendado,
        BigDecimal precoMedioVenda,
        BigDecimal lucroMedio,
        BigDecimal margemMediaPercentual,
        long simulacoesSaudaveis,
        long simulacoesComAlerta,
        List<DistribuicaoStatusPrecificacaoResponse> distribuicaoStatus,
        List<SimulacaoDashboardPrecificacaoResponse> simulacoesRecentes,
        Instant atualizadoEm
) {

    public static DashboardPrecificacaoResponse de(DashboardPrecificacaoResult result) {
        return new DashboardPrecificacaoResponse(
                result.empresaId(),
                result.totalSimulacoes(),
                result.custoMedio(),
                result.precoMedioRecomendado(),
                result.precoMedioVenda(),
                result.lucroMedio(),
                result.margemMediaPercentual(),
                result.simulacoesSaudaveis(),
                result.simulacoesComAlerta(),
                result.distribuicaoStatus().stream().map(DistribuicaoStatusPrecificacaoResponse::de).toList(),
                result.simulacoesRecentes().stream().map(SimulacaoDashboardPrecificacaoResponse::de).toList(),
                result.atualizadoEm()
        );
    }
}
