package br.com.atendepro.modules.documento.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record DocumentosProfissionaisPaginadosResponse(
        List<DocumentoProfissionalResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static DocumentosProfissionaisPaginadosResponse de(ResultadoPaginado<DocumentoProfissionalResult> resultado) {
        return new DocumentosProfissionaisPaginadosResponse(
                resultado.itens().stream().map(DocumentoProfissionalResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
