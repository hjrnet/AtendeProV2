package br.com.atendepro.modules.empresa.application.port.out;

import java.util.Optional;

import br.com.atendepro.modules.empresa.domain.model.DocumentoEmpresa;
import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;

public interface CarregarEmpresaPorDocumentoPort {

    Optional<EmpresaTenant> carregarEmpresaPorDocumento(DocumentoEmpresa documento);
}
