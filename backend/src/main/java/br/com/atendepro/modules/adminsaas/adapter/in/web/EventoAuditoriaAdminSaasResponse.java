package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.adminsaas.application.result.EventoAuditoriaAdminSaasResult;

public record EventoAuditoriaAdminSaasResponse(
        UUID id,
        String tipo,
        String severidade,
        String descricao,
        UUID empresaId,
        String empresaNome,
        UUID usuarioId,
        String referenciaTipo,
        UUID referenciaId,
        String metadados,
        Instant criadoEm
) {

    static EventoAuditoriaAdminSaasResponse de(EventoAuditoriaAdminSaasResult result) {
        return new EventoAuditoriaAdminSaasResponse(
                result.id(),
                result.tipo(),
                result.severidade(),
                result.descricao(),
                result.empresaId(),
                result.empresaNome(),
                result.usuarioId(),
                result.referenciaTipo(),
                result.referenciaId(),
                result.metadados(),
                result.criadoEm()
        );
    }
}
