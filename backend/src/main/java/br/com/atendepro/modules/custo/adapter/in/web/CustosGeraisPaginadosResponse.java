package br.com.atendepro.modules.custo.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.custo.application.result.CustoGeralResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record CustosGeraisPaginadosResponse(
        List<CustoGeralResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static CustosGeraisPaginadosResponse de(ResultadoPaginado<CustoGeralResult> resultado) {
        return new CustosGeraisPaginadosResponse(
                resultado.itens().stream().map(CustoGeralResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
