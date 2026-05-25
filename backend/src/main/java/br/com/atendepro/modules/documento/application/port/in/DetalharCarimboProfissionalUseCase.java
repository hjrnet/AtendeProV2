package br.com.atendepro.modules.documento.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.CarimboProfissionalResult;

public interface DetalharCarimboProfissionalUseCase {

    Optional<CarimboProfissionalResult> detalharCarimbo(UUID carimboId);
}
