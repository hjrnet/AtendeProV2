package br.com.atendepro.modules.spaces.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.OcupacaoSpacesResult;

public interface DetalharOcupacaoSpacesUseCase {

    Optional<OcupacaoSpacesResult> detalharOcupacao(UUID ocupacaoId);
}
