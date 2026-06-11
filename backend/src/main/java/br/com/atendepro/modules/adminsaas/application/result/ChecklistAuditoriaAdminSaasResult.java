package br.com.atendepro.modules.adminsaas.application.result;

public record ChecklistAuditoriaAdminSaasResult(
        String codigo,
        String titulo,
        String status,
        String detalhe,
        String severidade
) {
}
