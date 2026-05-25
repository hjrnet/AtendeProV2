package br.com.atendepro.modules.adminsaas.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.adminsaas.application.command.AlterarBloqueioEmpresaAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasDetalheResult;

public interface AlterarBloqueioEmpresaAdminSaasUseCase {

    Optional<EmpresaAdminSaasDetalheResult> alterarBloqueioEmpresa(AlterarBloqueioEmpresaAdminSaasCommand command);
}
