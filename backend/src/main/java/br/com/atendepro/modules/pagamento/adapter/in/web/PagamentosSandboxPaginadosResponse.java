package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.pagamento.application.result.PagamentoSandboxResumoResult;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public record PagamentosSandboxPaginadosResponse(
        List<PagamentoSandboxResumoResponse> itens,
        long totalItens,
        int pagina,
        int tamanho,
        int totalPaginas
) {

    public static PagamentosSandboxPaginadosResponse de(ResultadoPaginado<PagamentoSandboxResumoResult> result) {
        return new PagamentosSandboxPaginadosResponse(
                result.itens().stream().map(PagamentoSandboxResumoResponse::de).toList(),
                result.totalItens(),
                result.pagina(),
                result.tamanho(),
                result.totalPaginas()
        );
    }
}
