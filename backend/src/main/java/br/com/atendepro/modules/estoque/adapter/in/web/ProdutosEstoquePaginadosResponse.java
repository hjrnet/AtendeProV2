package br.com.atendepro.modules.estoque.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.estoque.application.result.ProdutoEstoqueResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record ProdutosEstoquePaginadosResponse(
        List<ProdutoEstoqueResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static ProdutosEstoquePaginadosResponse de(ResultadoPaginado<ProdutoEstoqueResult> resultado) {
        return new ProdutosEstoquePaginadosResponse(
                resultado.itens().stream().map(ProdutoEstoqueResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
