package br.com.atendepro.modules.documento.application.port.in;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;

public interface DetalharDocumentoProfissionalUseCase {

    Optional<DocumentoProfissionalResult> detalharDocumento(UUID documentoId);
}
