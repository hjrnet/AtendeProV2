package br.com.atendepro.modules.nutri.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.AvaliacaoAntropometricaNutriPro;

public interface CarregarAvaliacaoAntropometricaNutriProPort {

    Optional<AvaliacaoAntropometricaNutriPro> carregarAvaliacaoAntropometrica(UUID empresaId, UUID pacienteId, UUID avaliacaoId);
}
