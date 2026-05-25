package br.com.atendepro.modules.spaces.application.port.in;

import br.com.atendepro.modules.spaces.application.command.AgendarOcupacaoSpacesCommand;
import br.com.atendepro.modules.spaces.application.result.OcupacaoSpacesResult;

public interface AgendarOcupacaoSpacesUseCase {

    OcupacaoSpacesResult agendarOcupacao(AgendarOcupacaoSpacesCommand command);
}
