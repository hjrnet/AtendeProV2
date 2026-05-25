package br.com.atendepro.modules.auth.application.port.in;

import br.com.atendepro.modules.auth.application.command.CadastrarAdministradorEmpresaCommand;
import br.com.atendepro.modules.auth.application.result.AdministradorEmpresaResult;

public interface CadastrarAdministradorEmpresaUseCase {

    AdministradorEmpresaResult cadastrarAdministradorEmpresa(CadastrarAdministradorEmpresaCommand command);
}
