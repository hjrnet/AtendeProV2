package br.com.atendepro.modules.assinatura.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.assinatura.application.result.AssinaturaResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record AssinaturasPaginadasResponse(
        List<AssinaturaResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    static AssinaturasPaginadasResponse de(ResultadoPaginado<AssinaturaResult> result) {
        return new AssinaturasPaginadasResponse(
                result.itens().stream().map(AssinaturaResponse::de).toList(),
                result.totalItens(),
                result.pagina(),
                result.tamanho(),
                result.totalPaginas()
        );
    }
}
