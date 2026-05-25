package br.com.atendepro.modules.spaces.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.spaces.application.result.RecursoSpacesResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record RecursosSpacesPaginadosResponse(
        List<RecursoSpacesResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    static RecursosSpacesPaginadosResponse de(ResultadoPaginado<RecursoSpacesResult> result) {
        return new RecursosSpacesPaginadosResponse(
                result.itens().stream().map(RecursoSpacesResponse::de).toList(),
                result.totalItens(),
                result.pagina(),
                result.tamanho(),
                result.totalPaginas()
        );
    }
}
