package br.com.atendepro.modules.plano.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.plano.application.result.PlanoResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record PlanosPaginadosResponse(
        List<PlanoResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    static PlanosPaginadosResponse de(ResultadoPaginado<PlanoResult> result) {
        return new PlanosPaginadosResponse(
                result.itens().stream().map(PlanoResponse::de).toList(),
                result.totalItens(),
                result.pagina(),
                result.tamanho(),
                result.totalPaginas()
        );
    }
}
