package br.com.atendepro.modules.pagamento.adapter.in.web;

import br.com.atendepro.modules.pagamento.application.result.ObservabilidadePagamentosSandboxIndicadorResult;

public record ObservabilidadePagamentosSandboxIndicadorResponse(
        long totalCheckoutsPreparados,
        long totalCobrancasPendentes,
        long totalCobrancasRecebidas,
        long totalCobrancasVencidas,
        long totalCobrancasCanceladas,
        long totalWebhooksProcessados,
        long totalWebhooksNaoProcessados,
        long totalWebhooksDuplicados,
        long totalDivergencias
) {

    public static ObservabilidadePagamentosSandboxIndicadorResponse de(ObservabilidadePagamentosSandboxIndicadorResult result) {
        return new ObservabilidadePagamentosSandboxIndicadorResponse(
                result.totalCheckoutsPreparados(),
                result.totalCobrancasPendentes(),
                result.totalCobrancasRecebidas(),
                result.totalCobrancasVencidas(),
                result.totalCobrancasCanceladas(),
                result.totalWebhooksProcessados(),
                result.totalWebhooksNaoProcessados(),
                result.totalWebhooksDuplicados(),
                result.totalDivergencias()
        );
    }
}
