package br.com.atendepro.modules.pagamento.application.result;

import java.util.UUID;

public record ReconciliacaoDivergenciaPagamentosSandboxItemResult(
        UUID pagamentoAssinaturaId,
        String tipoDivergencia,
        String tipoEvento,
        boolean processado,
        boolean duplicado,
        boolean ignorado,
        String motivo,
        String mensagem
) {
}
