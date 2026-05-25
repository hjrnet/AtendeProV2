package br.com.atendepro.modules.spaces.application.port.in;

import br.com.atendepro.modules.spaces.application.command.ConsultarDisponibilidadeSpacesCommand;
import br.com.atendepro.modules.spaces.application.result.DisponibilidadeSpacesResult;

public interface ConsultarDisponibilidadeSpacesUseCase {

    DisponibilidadeSpacesResult consultarDisponibilidade(ConsultarDisponibilidadeSpacesCommand command);
}
