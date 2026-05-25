package br.com.atendepro.modules.documento.application.port.out;

import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;

public interface AtualizarDocumentoProfissionalPort {

    void atualizarDocumento(DocumentoProfissional documento);
}
