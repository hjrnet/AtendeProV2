package br.com.atendepro.modules.empresa.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.empresa.application.result.EmpresaResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record EmpresasPaginadasResponse(
        List<EmpresaResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public EmpresasPaginadasResponse {
        itens = List.copyOf(itens);
    }

    static EmpresasPaginadasResponse de(ResultadoPaginado<EmpresaResult> resultado) {
        return new EmpresasPaginadasResponse(
                resultado.itens().stream().map(EmpresaResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
