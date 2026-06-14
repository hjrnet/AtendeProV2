package br.com.atendepro.modules.pagamento.application.result;

import java.util.List;

public record PagamentosSandboxObservabilidadeResult(
        ObservabilidadePagamentosSandboxIndicadorResult indicadores,
        List<ObservabilidadePagamentosSandboxDivergenciaResult> divergencias
) {
}

