package br.com.atendepro.modules.adminsaas.application.result;

import java.time.Instant;
import java.util.List;

public record AuditoriaOperacionalAdminSaasResult(
        long eventosUltimos7Dias,
        long eventosCriticosUltimos7Dias,
        long empresasBloqueadas,
        long trialsExpirando7Dias,
        long chamadosCriticosAbertos,
        List<ChecklistAuditoriaAdminSaasResult> checklist,
        List<EventoAuditoriaAdminSaasResult> eventosRecentes,
        Instant atualizadoEm
) {
}
