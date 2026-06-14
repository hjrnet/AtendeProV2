package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.pagamento.application.command.ReconciliarDivergenciasPagamentosSandboxCommand;

public record ReconciliarDivergenciasPagamentosSandboxRequest(
        UUID empresaId,
        String statusAssinatura,
        String eventoTipo,
        String tipoDivergencia,
        String severidade
) {

    public ReconciliarDivergenciasPagamentosSandboxCommand paraCommand(String token) {
        return new ReconciliarDivergenciasPagamentosSandboxCommand(
                token,
                empresaId,
                statusAssinatura,
                eventoTipo,
                tipoDivergencia,
                severidade
        );
    }
}
