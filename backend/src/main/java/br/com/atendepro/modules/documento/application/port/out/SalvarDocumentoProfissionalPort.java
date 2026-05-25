package br.com.atendepro.modules.documento.application.port.out;

import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;

public interface SalvarDocumentoProfissionalPort {

    void salvarDocumento(DocumentoProfissional documento);
}
