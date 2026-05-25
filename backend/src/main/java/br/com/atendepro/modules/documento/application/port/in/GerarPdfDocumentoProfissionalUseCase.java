package br.com.atendepro.modules.documento.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalPdfResult;

public interface GerarPdfDocumentoProfissionalUseCase {

    DocumentoProfissionalPdfResult gerarPdf(UUID documentoId, UUID carimboId);
}
