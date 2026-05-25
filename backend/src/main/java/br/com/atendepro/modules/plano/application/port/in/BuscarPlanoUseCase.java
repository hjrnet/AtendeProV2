package br.com.atendepro.modules.plano.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.plano.application.result.PlanoResult;

public interface BuscarPlanoUseCase {

    Optional<PlanoResult> buscarPlanoPorId(UUID planoId);
}
