package br.com.atendepro.modules.nutri.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.domain.model.PlanoAlimentarNutriPro;

public interface ListarPlanosAlimentaresNutriProPort {

    List<PlanoAlimentarNutriPro> listarPlanosAlimentares(UUID empresaId, UUID pacienteId);
}
