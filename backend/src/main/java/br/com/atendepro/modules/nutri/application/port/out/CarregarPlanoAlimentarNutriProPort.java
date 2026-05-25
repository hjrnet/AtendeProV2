package br.com.atendepro.modules.nutri.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.PlanoAlimentarNutriPro;

public interface CarregarPlanoAlimentarNutriProPort {

    Optional<PlanoAlimentarNutriPro> carregarPlanoAlimentar(UUID empresaId, UUID pacienteId, UUID planoId);
}
