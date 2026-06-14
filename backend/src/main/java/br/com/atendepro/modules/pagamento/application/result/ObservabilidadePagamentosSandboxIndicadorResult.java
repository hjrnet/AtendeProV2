package br.com.atendepro.modules.pagamento.application.result;

public record ObservabilidadePagamentosSandboxIndicadorResult(
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
}

