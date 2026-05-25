package br.com.atendepro.modules.spaces.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.PacoteSublocacaoSpacesResult;

public interface DetalharPacoteSublocacaoSpacesUseCase {

    Optional<PacoteSublocacaoSpacesResult> detalharPacote(UUID pacoteId);
}
