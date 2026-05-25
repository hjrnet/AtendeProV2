package br.com.atendepro.modules.spaces.application.port.in;

import br.com.atendepro.modules.spaces.application.command.CadastrarPacoteSublocacaoSpacesCommand;
import br.com.atendepro.modules.spaces.application.result.PacoteSublocacaoSpacesResult;

public interface CadastrarPacoteSublocacaoSpacesUseCase {

    PacoteSublocacaoSpacesResult cadastrarPacote(CadastrarPacoteSublocacaoSpacesCommand command);
}
