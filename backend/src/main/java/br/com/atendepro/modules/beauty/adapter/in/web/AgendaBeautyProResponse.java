package br.com.atendepro.modules.beauty.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.AgendaBeautyProResult;

public record AgendaBeautyProResponse(
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
    public static AgendaBeautyProResponse de(AgendaBeautyProResult result) {
        return new AgendaBeautyProResponse(
                result.id(),
                result.clienteId(),
                result.clienteNome(),
                result.profissionalNome(),
                result.sala(),
                result.tipo(),
                result.status(),
                result.statusRotulo(),
                result.inicio(),
                result.fim(),
                result.observacoes()
        );
    }
}
