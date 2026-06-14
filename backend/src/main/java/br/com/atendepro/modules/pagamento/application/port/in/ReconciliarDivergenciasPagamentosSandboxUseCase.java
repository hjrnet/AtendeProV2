package br.com.atendepro.modules.pagamento.application.port.in;

import br.com.atendepro.modules.pagamento.application.command.ReconciliarDivergenciasPagamentosSandboxCommand;
import br.com.atendepro.modules.pagamento.application.result.ReconciliacaoDivergenciasPagamentosSandboxResult;

public interface ReconciliarDivergenciasPagamentosSandboxUseCase {

    ReconciliacaoDivergenciasPagamentosSandboxResult reconciliarDivergenciasPagamentosSandbox(
            ReconciliarDivergenciasPagamentosSandboxCommand command
    );
}
