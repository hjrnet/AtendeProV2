package br.com.atendepro.modules.documento.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.ModeloDocumentoProfissional;

public interface CarregarModeloDocumentoProfissionalPorIdPort {

    Optional<ModeloDocumentoProfissional> carregarModeloPorId(UUID modeloId);
}
