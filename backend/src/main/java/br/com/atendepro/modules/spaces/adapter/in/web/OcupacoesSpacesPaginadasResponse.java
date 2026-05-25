package br.com.atendepro.modules.spaces.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.spaces.application.result.OcupacaoSpacesResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record OcupacoesSpacesPaginadasResponse(
        List<OcupacaoSpacesResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    static OcupacoesSpacesPaginadasResponse de(ResultadoPaginado<OcupacaoSpacesResult> result) {
        return new OcupacoesSpacesPaginadasResponse(
                result.itens().stream().map(OcupacaoSpacesResponse::de).toList(),
                result.totalItens(),
                result.pagina(),
                result.tamanho(),
                result.totalPaginas()
        );
    }
}
