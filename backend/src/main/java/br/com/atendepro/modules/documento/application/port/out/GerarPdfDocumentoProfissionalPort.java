package br.com.atendepro.modules.documento.application.port.out;

import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalPdfResult;
import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;

public interface GerarPdfDocumentoProfissionalPort {

    DocumentoProfissionalPdfResult gerarPdf(DocumentoProfissional documento, CarimboProfissional carimbo);
}
