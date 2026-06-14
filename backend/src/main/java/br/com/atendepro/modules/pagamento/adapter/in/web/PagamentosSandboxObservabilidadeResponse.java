package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.pagamento.application.result.PagamentosSandboxObservabilidadeResult;

public record PagamentosSandboxObservabilidadeResponse(
        ObservabilidadePagamentosSandboxIndicadorResponse indicadores,
        List<ObservabilidadePagamentosSandboxDivergenciaResponse> divergencias
) {

    public static PagamentosSandboxObservabilidadeResponse de(PagamentosSandboxObservabilidadeResult result) {
        return new PagamentosSandboxObservabilidadeResponse(
                ObservabilidadePagamentosSandboxIndicadorResponse.de(result.indicadores()),
                result.divergencias().stream().map(ObservabilidadePagamentosSandboxDivergenciaResponse::de).toList()
        );
    }
}
