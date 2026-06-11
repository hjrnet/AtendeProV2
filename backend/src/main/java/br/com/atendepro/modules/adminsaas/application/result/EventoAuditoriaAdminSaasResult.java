package br.com.atendepro.modules.adminsaas.application.result;

import java.time.Instant;
import java.util.UUID;

public record EventoAuditoriaAdminSaasResult(
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
}
