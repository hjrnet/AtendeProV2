package br.com.atendepro.modules.assinatura.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.assinatura.application.result.TrialResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record TrialsPaginadosResponse(
        List<TrialResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    static TrialsPaginadosResponse de(ResultadoPaginado<TrialResult> result) {
        return new TrialsPaginadosResponse(
                result.itens().stream().map(TrialResponse::de).toList(),
                result.totalItens(),
                result.pagina(),
                result.tamanho(),
                result.totalPaginas()
        );
    }
}
