package br.com.atendepro.modules.adminsaas.application.port.in;

import br.com.atendepro.modules.adminsaas.application.result.AdminSaasStatusResult;

public interface ConsultarAdminSaasUseCase {

    AdminSaasStatusResult consultarStatus();
}
