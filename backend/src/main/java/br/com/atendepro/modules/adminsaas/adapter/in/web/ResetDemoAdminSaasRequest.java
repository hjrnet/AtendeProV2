package br.com.atendepro.modules.adminsaas.adapter.in.web;

import br.com.atendepro.modules.adminsaas.application.command.ResetarDemoAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.domain.model.PerfilDemoAdminSaas;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResetDemoAdminSaasRequest(
        @NotNull PerfilDemoAdminSaas perfil,
        boolean confirmarReset,
        @Size(max = 240) String motivo
) {

    public ResetarDemoAdminSaasCommand paraCommand() {
        return new ResetarDemoAdminSaasCommand(perfil, confirmarReset, motivo);
    }
}
