package br.com.atendepro.modules.vertical.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.vertical.application.result.VerticalProfissionalResult;
import br.com.atendepro.modules.vertical.domain.model.CodigoVerticalProfissional;

public interface DetalharVerticalProfissionalUseCase {

    Optional<VerticalProfissionalResult> detalharVertical(CodigoVerticalProfissional codigo);
}
