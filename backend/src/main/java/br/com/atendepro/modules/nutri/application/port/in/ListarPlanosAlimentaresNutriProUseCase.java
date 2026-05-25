package br.com.atendepro.modules.nutri.application.port.in;

import java.util.List;

import br.com.atendepro.modules.nutri.application.command.ListarPlanosAlimentaresNutriProCommand;
import br.com.atendepro.modules.nutri.application.result.PlanoAlimentarNutriProResult;

public interface ListarPlanosAlimentaresNutriProUseCase {

    List<PlanoAlimentarNutriProResult> listarPlanosAlimentares(ListarPlanosAlimentaresNutriProCommand command);
}
