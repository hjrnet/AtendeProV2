package br.com.atendepro.modules.documento.application.port.in;

import br.com.atendepro.modules.documento.application.result.ValidacaoDocumentoProfissionalResult;

public interface ValidarDocumentoProfissionalUseCase {

    ValidacaoDocumentoProfissionalResult validarDocumento(String codigoValidacao);
}
