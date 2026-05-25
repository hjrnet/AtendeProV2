package br.com.atendepro.modules.nutri.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.nutri.application.command.DetalharAvaliacaoAntropometricaNutriProCommand;
import br.com.atendepro.modules.nutri.application.result.AvaliacaoAntropometricaNutriProResult;

public interface DetalharAvaliacaoAntropometricaNutriProUseCase {

    Optional<AvaliacaoAntropometricaNutriProResult> detalharAvaliacaoAntropometrica(DetalharAvaliacaoAntropometricaNutriProCommand command);
}
