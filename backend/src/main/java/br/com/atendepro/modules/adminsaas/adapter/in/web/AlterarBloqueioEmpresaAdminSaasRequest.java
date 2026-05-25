package br.com.atendepro.modules.adminsaas.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.adminsaas.application.command.AlterarBloqueioEmpresaAdminSaasCommand;
import jakarta.validation.constraints.NotNull;

public record AlterarBloqueioEmpresaAdminSaasRequest(@NotNull Boolean bloqueada) {

    AlterarBloqueioEmpresaAdminSaasCommand paraCommand(UUID empresaId) {
        return new AlterarBloqueioEmpresaAdminSaasCommand(empresaId, bloqueada);
    }
}
