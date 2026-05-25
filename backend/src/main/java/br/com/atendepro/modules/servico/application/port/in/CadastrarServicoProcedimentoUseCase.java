package br.com.atendepro.modules.servico.application.port.in;

import br.com.atendepro.modules.servico.application.command.CadastrarServicoProcedimentoCommand;
import br.com.atendepro.modules.servico.application.result.ServicoProcedimentoResult;

public interface CadastrarServicoProcedimentoUseCase {

    ServicoProcedimentoResult cadastrarServicoProcedimento(CadastrarServicoProcedimentoCommand command);
}
