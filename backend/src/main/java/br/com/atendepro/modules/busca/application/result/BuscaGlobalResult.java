package br.com.atendepro.modules.busca.application.result;

import java.util.List;
import java.util.UUID;

public record BuscaGlobalResult(
        UUID empresaId,
        String busca,
        String categoria,
        String status,
        int limitePorTipo,
        int totalItens,
        List<ResultadoBuscaGlobalItemResult> itens
) {

    public BuscaGlobalResult {
        itens = List.copyOf(itens);
    }
}
