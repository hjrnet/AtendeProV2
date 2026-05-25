package br.com.atendepro.modules.documento.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.ModeloDocumentoProfissionalResult;

public interface DetalharModeloDocumentoProfissionalUseCase {

    Optional<ModeloDocumentoProfissionalResult> detalharModelo(UUID modeloId, UUID empresaId);
}
