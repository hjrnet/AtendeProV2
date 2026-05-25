package br.com.atendepro.modules.documento.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.documento.application.result.ModeloDocumentoProfissionalResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record ModelosDocumentoProfissionalPaginadosResponse(
        List<ModeloDocumentoProfissionalResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static ModelosDocumentoProfissionalPaginadosResponse de(
            ResultadoPaginado<ModeloDocumentoProfissionalResult> resultado
    ) {
        return new ModelosDocumentoProfissionalPaginadosResponse(
                resultado.itens().stream().map(ModeloDocumentoProfissionalResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
