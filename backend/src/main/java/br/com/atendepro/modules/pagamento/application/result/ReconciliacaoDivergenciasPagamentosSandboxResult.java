package br.com.atendepro.modules.pagamento.application.result;

import java.util.List;

public record ReconciliacaoDivergenciasPagamentosSandboxResult(
        int totalEncontradas,
        int totalProcessadas,
        int totalIgnoradas,
        int totalDuplicadas,
        int totalFalhas,
        List<ReconciliacaoDivergenciaPagamentosSandboxItemResult> itens
) {
}
