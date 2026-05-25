package br.com.atendepro.modules.nutri.application.port.in;

import br.com.atendepro.modules.nutri.application.command.CriarAvaliacaoAntropometricaNutriProCommand;
import br.com.atendepro.modules.nutri.application.result.AvaliacaoAntropometricaNutriProResult;

public interface CriarAvaliacaoAntropometricaNutriProUseCase {

    AvaliacaoAntropometricaNutriProResult criarAvaliacaoAntropometrica(CriarAvaliacaoAntropometricaNutriProCommand command);
}
