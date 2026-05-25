package br.com.atendepro.modules.spaces.application.port.in;

import br.com.atendepro.modules.spaces.application.command.CalcularCustoHoraSpacesCommand;
import br.com.atendepro.modules.spaces.application.result.CustoHoraSpacesResult;

public interface CalcularCustoHoraSpacesUseCase {

    CustoHoraSpacesResult calcularCustoHora(CalcularCustoHoraSpacesCommand command);
}
