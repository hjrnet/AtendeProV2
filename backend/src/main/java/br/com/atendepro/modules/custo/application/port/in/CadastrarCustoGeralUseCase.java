package br.com.atendepro.modules.custo.application.port.in;

import br.com.atendepro.modules.custo.application.command.CadastrarCustoGeralCommand;
import br.com.atendepro.modules.custo.application.result.CustoGeralResult;

public interface CadastrarCustoGeralUseCase {

    CustoGeralResult cadastrarCustoGeral(CadastrarCustoGeralCommand command);
}
