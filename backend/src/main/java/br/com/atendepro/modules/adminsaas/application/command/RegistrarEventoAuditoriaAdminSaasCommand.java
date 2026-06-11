package br.com.atendepro.modules.adminsaas.application.command;

import java.util.Map;
import java.util.UUID;

public record RegistrarEventoAuditoriaAdminSaasCommand(
        String tipo,
        String severidade,
        String descricao,
        UUID empresaId,
        UUID usuarioId,
        String referenciaTipo,
        UUID referenciaId,
        Map<String, String> metadados
) {
}
