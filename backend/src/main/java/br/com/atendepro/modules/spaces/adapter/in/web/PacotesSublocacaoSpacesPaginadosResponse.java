package br.com.atendepro.modules.spaces.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.spaces.application.result.PacoteSublocacaoSpacesResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record PacotesSublocacaoSpacesPaginadosResponse(
        List<PacoteSublocacaoSpacesResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    static PacotesSublocacaoSpacesPaginadosResponse de(ResultadoPaginado<PacoteSublocacaoSpacesResult> result) {
        return new PacotesSublocacaoSpacesPaginadosResponse(
                result.itens().stream().map(PacoteSublocacaoSpacesResponse::de).toList(),
                result.totalItens(),
                result.pagina(),
                result.tamanho(),
                result.totalPaginas()
        );
    }
}
