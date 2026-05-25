package br.com.atendepro.modules.precificacao.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.precificacao.application.result.SimulacaoPrecificacaoResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record SimulacoesPrecificacaoPaginadasResponse(
        List<SimulacaoPrecificacaoResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static SimulacoesPrecificacaoPaginadasResponse de(ResultadoPaginado<SimulacaoPrecificacaoResult> resultado) {
        return new SimulacoesPrecificacaoPaginadasResponse(
                resultado.itens().stream().map(SimulacaoPrecificacaoResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
