package br.com.atendepro.modules.adminsaas.adapter.in.web;

import br.com.atendepro.modules.adminsaas.application.result.ChecklistAuditoriaAdminSaasResult;

public record ChecklistAuditoriaAdminSaasResponse(
        String codigo,
        String titulo,
        String status,
        String detalhe,
        String severidade
) {

    static ChecklistAuditoriaAdminSaasResponse de(ChecklistAuditoriaAdminSaasResult result) {
        return new ChecklistAuditoriaAdminSaasResponse(
                result.codigo(),
                result.titulo(),
                result.status(),
                result.detalhe(),
                result.severidade()
        );
    }
}
