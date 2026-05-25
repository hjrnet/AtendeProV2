package br.com.atendepro.modules.documento.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;

public interface CarregarCarimboProfissionalPorIdPort {

    Optional<CarimboProfissional> carregarCarimboPorId(UUID carimboId);
}
