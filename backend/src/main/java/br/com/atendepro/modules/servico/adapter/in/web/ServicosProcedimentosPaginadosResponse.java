package br.com.atendepro.modules.servico.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.servico.application.result.ServicoProcedimentoResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record ServicosProcedimentosPaginadosResponse(
        List<ServicoProcedimentoResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static ServicosProcedimentosPaginadosResponse de(ResultadoPaginado<ServicoProcedimentoResult> resultado) {
        return new ServicosProcedimentosPaginadosResponse(
                resultado.itens().stream().map(ServicoProcedimentoResponse::de).toList(),
                resultado.totalItens(),
                resultado.pagina(),
                resultado.tamanho(),
                resultado.totalPaginas()
        );
    }
}
