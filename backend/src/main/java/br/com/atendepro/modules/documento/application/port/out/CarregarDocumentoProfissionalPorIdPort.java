package br.com.atendepro.modules.documento.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;

public interface CarregarDocumentoProfissionalPorIdPort {

    Optional<DocumentoProfissional> carregarDocumentoPorId(UUID documentoId);
}
