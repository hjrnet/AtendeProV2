package br.com.atendepro.modules.beauty.application.result;

import java.time.Instant;
import java.util.UUID;

public record AgendaBeautyProResult(
        UUID id,
        UUID clienteId,
        String clienteNome,
        String profissionalNome,
        String sala,
        String tipo,
        String status,
        String statusRotulo,
        Instant inicio,
        Instant fim,
        String observacoes
) {
}
