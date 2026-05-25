package br.com.atendepro.modules.busca.adapter.in.web;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.busca.application.result.BuscaGlobalResult;

public record BuscaGlobalResponse(
        UUID empresaId,
        String busca,
        String categoria,
        String status,
        int limitePorTipo,
        int totalItens,
        List<ResultadoBuscaGlobalItemResponse> itens
) {

    public static BuscaGlobalResponse de(BuscaGlobalResult result) {
        return new BuscaGlobalResponse(
                result.empresaId(),
                result.busca(),
                result.categoria(),
                result.status(),
                result.limitePorTipo(),
                result.totalItens(),
                result.itens().stream().map(ResultadoBuscaGlobalItemResponse::de).toList()
        );
    }
}
