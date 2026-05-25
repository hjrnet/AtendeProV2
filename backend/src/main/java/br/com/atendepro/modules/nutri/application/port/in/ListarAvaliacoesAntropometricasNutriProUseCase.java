package br.com.atendepro.modules.nutri.application.port.in;

import java.util.List;

import br.com.atendepro.modules.nutri.application.command.ListarAvaliacoesAntropometricasNutriProCommand;
import br.com.atendepro.modules.nutri.application.result.AvaliacaoAntropometricaNutriProResult;

public interface ListarAvaliacoesAntropometricasNutriProUseCase {

    List<AvaliacaoAntropometricaNutriProResult> listarAvaliacoesAntropometricas(ListarAvaliacoesAntropometricasNutriProCommand command);
}
