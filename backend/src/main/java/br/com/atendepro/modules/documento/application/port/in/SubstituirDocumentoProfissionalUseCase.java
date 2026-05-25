package br.com.atendepro.modules.documento.application.port.in;

import br.com.atendepro.modules.documento.application.command.SubstituirDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;

public interface SubstituirDocumentoProfissionalUseCase {

    DocumentoProfissionalResult substituirDocumento(SubstituirDocumentoProfissionalCommand command);
}
