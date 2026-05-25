package br.com.atendepro.modules.nutri.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.nutri.application.command.ConsultarProntuarioNutriProCommand;
import br.com.atendepro.modules.nutri.application.result.ProntuarioNutriProResult;

public interface ConsultarProntuarioNutriProUseCase {

    Optional<ProntuarioNutriProResult> consultarProntuarioNutriPro(ConsultarProntuarioNutriProCommand command);
}
