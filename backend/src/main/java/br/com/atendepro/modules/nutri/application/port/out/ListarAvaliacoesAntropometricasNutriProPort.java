package br.com.atendepro.modules.nutri.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.AvaliacaoAntropometricaNutriPro;

public interface ListarAvaliacoesAntropometricasNutriProPort {

    List<AvaliacaoAntropometricaNutriPro> listarAvaliacoesAntropometricas(UUID empresaId, UUID pacienteId);
}
