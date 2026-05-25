package br.com.atendepro.modules.busca.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.busca.application.result.ResultadoBuscaGlobalItemResult;
import br.com.atendepro.modules.busca.domain.model.TipoResultadoBusca;

public record ResultadoBuscaGlobalItemResponse(
        UUID id,
        TipoResultadoBusca tipo,
        String titulo,
        String descricao,
        String categoria,
        String status,
        String destino,
        Instant dataReferencia
) {

    public static ResultadoBuscaGlobalItemResponse de(ResultadoBuscaGlobalItemResult result) {
        return new ResultadoBuscaGlobalItemResponse(
                result.id(),
                result.tipo(),
                result.titulo(),
                result.descricao(),
                result.categoria(),
                result.status(),
                result.destino(),
                result.dataReferencia()
        );
    }
}
