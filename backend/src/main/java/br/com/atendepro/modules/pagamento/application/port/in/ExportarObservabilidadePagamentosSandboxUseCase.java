package br.com.atendepro.modules.pagamento.application.port.in;

import java.util.UUID;

public interface ExportarObservabilidadePagamentosSandboxUseCase {

    byte[] exportarObservabilidadePagamentosSandboxCsv(
            UUID empresaId,
            String statusAssinatura,
            String eventoTipo,
            String tipoDivergencia,
            String severidade
    );
}

