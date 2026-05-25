package br.com.atendepro.modules.documento.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.documento.application.result.HistoricoDocumentoProfissionalResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record HistoricoDocumentoProfissionalPaginadoResponse(
        List<HistoricoDocumentoProfissionalResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static HistoricoDocumentoProfissionalPaginadoResponse de(
            ResultadoPaginado<HistoricoDocumentoProfissionalResult> resultado
    ) {
        return new HistoricoDocumentoProfissionalPaginadoResponse(
                resultado.itens().stream().map(HistoricoDocumentoProfissionalResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
