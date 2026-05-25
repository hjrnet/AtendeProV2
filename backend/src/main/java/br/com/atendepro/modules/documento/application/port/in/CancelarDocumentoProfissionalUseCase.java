package br.com.atendepro.modules.documento.application.port.in;

import br.com.atendepro.modules.documento.application.command.CancelarDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;

public interface CancelarDocumentoProfissionalUseCase {

    DocumentoProfissionalResult cancelarDocumento(CancelarDocumentoProfissionalCommand command);
}
