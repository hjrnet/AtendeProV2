package br.com.atendepro.modules.custo.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.custo.application.result.CustoAlimentacaoTransporteResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record CustosAlimentacaoTransportePaginadosResponse(
        List<CustoAlimentacaoTransporteResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static CustosAlimentacaoTransportePaginadosResponse de(ResultadoPaginado<CustoAlimentacaoTransporteResult> resultado) {
        return new CustosAlimentacaoTransportePaginadosResponse(
                resultado.itens().stream().map(CustoAlimentacaoTransporteResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
