package br.com.atendepro.modules.nutri.application.port.out;

import java.util.UUID;

public interface VerificarPacienteNutriProPort {

    boolean existePacienteNutriPro(UUID empresaId, UUID pacienteId);
}
