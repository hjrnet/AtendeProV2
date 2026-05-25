package br.com.atendepro.modules.empresa.application.port.in;

import br.com.atendepro.modules.empresa.application.command.CadastrarEmpresaCommand;
import br.com.atendepro.modules.empresa.application.result.EmpresaResult;

public interface CadastrarEmpresaUseCase {

    EmpresaResult cadastrarEmpresa(CadastrarEmpresaCommand command);
}
