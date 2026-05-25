package br.com.atendepro.modules.nutri.application.port.in;

import java.util.List;

import br.com.atendepro.modules.nutri.application.command.ListarPacientesNutriProCommand;
import br.com.atendepro.modules.nutri.application.result.PacienteNutriResumoResult;

public interface ListarPacientesNutriProUseCase {

    List<PacienteNutriResumoResult> listarPacientesNutriPro(ListarPacientesNutriProCommand command);
}
