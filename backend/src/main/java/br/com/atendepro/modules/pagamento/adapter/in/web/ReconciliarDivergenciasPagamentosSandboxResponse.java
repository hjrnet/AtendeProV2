package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.pagamento.application.result.ReconciliacaoDivergenciaPagamentosSandboxItemResult;
import br.com.atendepro.modules.pagamento.application.result.ReconciliacaoDivergenciasPagamentosSandboxResult;

public record ReconciliarDivergenciasPagamentosSandboxResponse(
        int totalEncontradas,
        int totalProcessadas,
        int totalIgnoradas,
        int totalDuplicadas,
        int totalFalhas,
        List<ReconciliacaoDivergenciasPagamentosSandboxItemResponse> itens
) {
    public static ReconciliarDivergenciasPagamentosSandboxResponse de(
            ReconciliacaoDivergenciasPagamentosSandboxResult result
    ) {
        return new ReconciliarDivergenciasPagamentosSandboxResponse(
                result.totalEncontradas(),
                result.totalProcessadas(),
                result.totalIgnoradas(),
                result.totalDuplicadas(),
                result.totalFalhas(),
                result.itens().stream().map(ReconciliacaoDivergenciasPagamentosSandboxItemResponse::de).toList()
        );
    }
}

record ReconciliacaoDivergenciasPagamentosSandboxItemResponse(
        String pagamentoAssinaturaId,
        String tipoDivergencia,
        String tipoEvento,
        boolean processado,
        boolean duplicado,
        boolean ignorado,
        String motivo,
        String mensagem
) {
    public static ReconciliacaoDivergenciasPagamentosSandboxItemResponse de(
            ReconciliacaoDivergenciaPagamentosSandboxItemResult result
    ) {
        return new ReconciliacaoDivergenciasPagamentosSandboxItemResponse(
                result.pagamentoAssinaturaId() == null ? null : result.pagamentoAssinaturaId().toString(),
                result.tipoDivergencia(),
                result.tipoEvento(),
                result.processado(),
                result.duplicado(),
                result.ignorado(),
                result.motivo(),
                result.mensagem()
        );
    }
}
