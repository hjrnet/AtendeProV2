package br.com.atendepro.modules.pagamento.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.pagamento.application.result.PagamentosSandboxObservabilidadeResult;

public interface ObterObservabilidadePagamentosSandboxUseCase {

    PagamentosSandboxObservabilidadeResult consultarObservabilidadePagamentosSandbox(
            UUID empresaId,
            String statusAssinatura,
            String eventoTipo,
            String tipoDivergencia,
            String severidade
    );
}
