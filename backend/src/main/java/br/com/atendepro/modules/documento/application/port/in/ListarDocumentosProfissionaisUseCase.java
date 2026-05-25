package br.com.atendepro.modules.documento.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarDocumentosProfissionaisUseCase {

    ResultadoPaginado<DocumentoProfissionalResult> listarDocumentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoDocumentoProfissional tipo,
            StatusDocumentoProfissional status,
            UUID clientePacienteId,
            Boolean ativo
    );
}
