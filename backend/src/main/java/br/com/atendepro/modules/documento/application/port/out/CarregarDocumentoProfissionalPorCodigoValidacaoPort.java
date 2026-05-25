package br.com.atendepro.modules.documento.application.port.out;

import java.util.Optional;

import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;

public interface CarregarDocumentoProfissionalPorCodigoValidacaoPort {

    Optional<DocumentoProfissional> carregarDocumentoPorCodigoValidacao(String codigoValidacao);
}
