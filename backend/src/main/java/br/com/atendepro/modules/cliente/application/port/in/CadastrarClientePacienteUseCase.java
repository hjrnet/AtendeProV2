package br.com.atendepro.modules.cliente.application.port.in;

import br.com.atendepro.modules.cliente.application.command.CadastrarClientePacienteCommand;
import br.com.atendepro.modules.cliente.application.result.ClientePacienteResult;

public interface CadastrarClientePacienteUseCase {

    ClientePacienteResult cadastrarClientePaciente(CadastrarClientePacienteCommand command);
}
