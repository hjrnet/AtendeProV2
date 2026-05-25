package br.com.atendepro.modules.nutri.application.port.in;

import br.com.atendepro.modules.nutri.application.command.ConsultarVisaoNutriProCommand;
import br.com.atendepro.modules.nutri.application.result.VisaoNutriProResult;

public interface ConsultarVisaoNutriProUseCase {

    VisaoNutriProResult consultarVisaoNutriPro(ConsultarVisaoNutriProCommand command);
}
