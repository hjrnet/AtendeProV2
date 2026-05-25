package br.com.atendepro.modules.spaces.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.RecursoSpaces;

public interface CarregarRecursoSpacesPorIdPort {

    Optional<RecursoSpaces> carregarRecursoPorId(UUID recursoId);
}
