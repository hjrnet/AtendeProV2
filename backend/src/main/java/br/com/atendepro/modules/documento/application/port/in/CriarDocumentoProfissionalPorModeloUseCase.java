package br.com.atendepro.modules.documento.application.port.in;

import br.com.atendepro.modules.documento.application.command.CriarDocumentoPorModeloCommand;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;

public interface CriarDocumentoProfissionalPorModeloUseCase {

    DocumentoProfissionalResult criarDocumentoPorModelo(CriarDocumentoPorModeloCommand command);
}
