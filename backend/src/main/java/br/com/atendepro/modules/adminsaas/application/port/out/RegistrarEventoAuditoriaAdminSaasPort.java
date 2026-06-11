package br.com.atendepro.modules.adminsaas.application.port.out;

import br.com.atendepro.modules.adminsaas.application.command.RegistrarEventoAuditoriaAdminSaasCommand;

public interface RegistrarEventoAuditoriaAdminSaasPort {

    void registrarEvento(RegistrarEventoAuditoriaAdminSaasCommand command);
}
