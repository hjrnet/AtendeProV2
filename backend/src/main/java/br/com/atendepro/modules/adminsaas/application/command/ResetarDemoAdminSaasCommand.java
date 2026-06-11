package br.com.atendepro.modules.adminsaas.application.command;

import br.com.atendepro.modules.adminsaas.domain.model.PerfilDemoAdminSaas;

public record ResetarDemoAdminSaasCommand(
        PerfilDemoAdminSaas perfil,
        boolean confirmarReset,
        String motivo
) {
}
