package br.com.atendepro.modules.nutri.application.port.out;

import java.util.List;
import java.util.UUID;

import br.com.atendepro.modules.nutri.application.result.PacienteNutriResumoResult;

public interface ListarPacientesNutriProPort {

    List<PacienteNutriResumoResult> listarPacientesNutriPro(UUID empresaId, String busca);
}
