package br.com.atendepro.modules.busca.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.busca.domain.model.TipoResultadoBusca;

public record ResultadoBuscaGlobalItemResult(
        UUID id,
        TipoResultadoBusca tipo,
        String titulo,
        String descricao,
        String categoria,
        String status,
        String destino,
        Instant dataReferencia
) {
}
