package br.com.atendepro.modules.documento.application.port.in;

import br.com.atendepro.modules.documento.application.command.CriarDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;

public interface CriarDocumentoProfissionalUseCase {

    DocumentoProfissionalResult criarDocumento(CriarDocumentoProfissionalCommand command);
}
