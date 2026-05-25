package br.com.atendepro.modules.adminsaas.application.command;

import java.util.UUID;

public record AlterarBloqueioEmpresaAdminSaasCommand(UUID empresaId, boolean bloqueada) {

    public AlterarBloqueioEmpresaAdminSaasCommand {
        if (empresaId == null) {
            throw new IllegalArgumentException("id da empresa e obrigatorio");
        }
    }
}
