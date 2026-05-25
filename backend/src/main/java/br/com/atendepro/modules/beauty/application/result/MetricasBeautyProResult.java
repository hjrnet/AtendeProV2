package br.com.atendepro.modules.beauty.application.result;

import java.util.List;

public record MetricasBeautyProResult(
        String empresaNome,
        long clientesAtivos,
        long agendaHoje,
        long agendaProximos7Dias,
        long servicosBeautyAtivos,
        long produtosAtivos,
        long equipamentosAtivos,
        long simulacoesPrecificacao,
        long simulacoesEmAlerta,
        long protocolosAtivos,
        long sessoesRealizadas,
        long termosConsentimento,
        long evidenciasSeguras,
        long produtosVinculados,
        long alertasProdutos,
        List<ClienteBeautyResumoResult> clientesRecentes
) {
}
