package br.com.atendepro.modules.suporte.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.suporte.application.result.ChamadoSuporteResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record ChamadosSuportePaginadosResponse(
        List<ChamadoSuporteResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static ChamadosSuportePaginadosResponse de(ResultadoPaginado<ChamadoSuporteResult> result) {
        return new ChamadosSuportePaginadosResponse(
                result.itens().stream().map(ChamadoSuporteResponse::de).toList(),
                result.totalItens(),
                result.pagina(),
                result.tamanho(),
                result.totalPaginas()
        );
    }
}
