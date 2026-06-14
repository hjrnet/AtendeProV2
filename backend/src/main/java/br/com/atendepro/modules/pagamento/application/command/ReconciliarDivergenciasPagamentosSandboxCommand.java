package br.com.atendepro.modules.pagamento.application.command;

import java.util.UUID;

public record ReconciliarDivergenciasPagamentosSandboxCommand(
        String token,
        UUID empresaId,
        String statusAssinatura,
        String eventoTipo,
        String tipoDivergencia,
        String severidade
) {
}
