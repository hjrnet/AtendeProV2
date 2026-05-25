package br.com.atendepro.modules.spaces.application.port.in;

import br.com.atendepro.modules.spaces.application.command.SimularParceiroSpacesCommand;
import br.com.atendepro.modules.spaces.application.result.SimulacaoParceiroSpacesResult;

public interface SimularParceiroSpacesUseCase {

    SimulacaoParceiroSpacesResult simularParceiro(SimularParceiroSpacesCommand command);
}
