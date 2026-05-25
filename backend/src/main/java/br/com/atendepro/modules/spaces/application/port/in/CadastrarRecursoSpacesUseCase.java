package br.com.atendepro.modules.spaces.application.port.in;

import br.com.atendepro.modules.spaces.application.command.CadastrarRecursoSpacesCommand;
import br.com.atendepro.modules.spaces.application.result.RecursoSpacesResult;

public interface CadastrarRecursoSpacesUseCase {

    RecursoSpacesResult cadastrarRecurso(CadastrarRecursoSpacesCommand command);
}
