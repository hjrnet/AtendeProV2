package br.com.atendepro.modules.adminsaas.application.port.in;

import br.com.atendepro.modules.adminsaas.application.command.ResetarDemoAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.application.result.ResetDemoAdminSaasResult;

public interface ResetarDemoAdminSaasUseCase {

    ResetDemoAdminSaasResult resetarDemo(ResetarDemoAdminSaasCommand command);
}
