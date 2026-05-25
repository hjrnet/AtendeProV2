package br.com.atendepro.modules.nutri.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.nutri.application.command.DetalharPlanoAlimentarNutriProCommand;
import br.com.atendepro.modules.nutri.application.result.PlanoAlimentarNutriProResult;

public interface DetalharPlanoAlimentarNutriProUseCase {

    Optional<PlanoAlimentarNutriProResult> detalharPlanoAlimentar(DetalharPlanoAlimentarNutriProCommand command);
}
