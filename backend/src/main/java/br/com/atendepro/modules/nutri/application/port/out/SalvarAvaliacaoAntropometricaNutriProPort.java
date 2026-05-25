package br.com.atendepro.modules.nutri.application.port.out;

import br.com.atendepro.modules.nutri.domain.model.AvaliacaoAntropometricaNutriPro;

public interface SalvarAvaliacaoAntropometricaNutriProPort {

    void salvarAvaliacaoAntropometrica(AvaliacaoAntropometricaNutriPro avaliacao);
}
