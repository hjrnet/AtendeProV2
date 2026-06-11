package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.time.Instant;
import java.util.List;

import br.com.atendepro.modules.adminsaas.application.result.AuditoriaOperacionalAdminSaasResult;

public record AuditoriaOperacionalAdminSaasResponse(
        long eventosUltimos7Dias,
        long eventosCriticosUltimos7Dias,
        long empresasBloqueadas,
        long trialsExpirando7Dias,
        long chamadosCriticosAbertos,
        List<ChecklistAuditoriaAdminSaasResponse> checklist,
        List<EventoAuditoriaAdminSaasResponse> eventosRecentes,
        Instant atualizadoEm
) {

    static AuditoriaOperacionalAdminSaasResponse de(AuditoriaOperacionalAdminSaasResult result) {
        return new AuditoriaOperacionalAdminSaasResponse(
                result.eventosUltimos7Dias(),
                result.eventosCriticosUltimos7Dias(),
                result.empresasBloqueadas(),
                result.trialsExpirando7Dias(),
                result.chamadosCriticosAbertos(),
                result.checklist().stream().map(ChecklistAuditoriaAdminSaasResponse::de).toList(),
                result.eventosRecentes().stream().map(EventoAuditoriaAdminSaasResponse::de).toList(),
                result.atualizadoEm()
        );
    }
}
