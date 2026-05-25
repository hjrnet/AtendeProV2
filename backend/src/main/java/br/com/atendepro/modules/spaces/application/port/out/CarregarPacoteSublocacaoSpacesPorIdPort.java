package br.com.atendepro.modules.spaces.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.PacoteSublocacaoSpaces;

public interface CarregarPacoteSublocacaoSpacesPorIdPort {

    Optional<PacoteSublocacaoSpaces> carregarPacotePorId(UUID pacoteId);
}
