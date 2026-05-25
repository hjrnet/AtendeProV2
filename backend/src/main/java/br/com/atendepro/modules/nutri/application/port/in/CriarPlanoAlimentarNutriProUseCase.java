package br.com.atendepro.modules.nutri.application.port.in;

import br.com.atendepro.modules.nutri.application.command.CriarPlanoAlimentarNutriProCommand;
import br.com.atendepro.modules.nutri.application.result.PlanoAlimentarNutriProResult;

public interface CriarPlanoAlimentarNutriProUseCase {

    PlanoAlimentarNutriProResult criarPlanoAlimentar(CriarPlanoAlimentarNutriProCommand command);
}
