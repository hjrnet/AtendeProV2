package br.com.atendepro.modules.pagamento.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.pagamento.application.result.PagamentosSandboxObservabilidadeResult;

public interface ObterObservabilidadePagamentosSandboxPort {

    PagamentosSandboxObservabilidadeResult consultarObservabilidadePagamentosSandbox(
            UUID empresaId,
            String statusAssinatura,
            String eventoTipo,
            String tipoDivergencia,
            String severidade
    );
}
