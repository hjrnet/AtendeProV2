package br.com.atendepro.modules.documento.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarDocumentosProfissionaisPort {

    ResultadoPaginado<DocumentoProfissional> listarDocumentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoDocumentoProfissional tipo,
            StatusDocumentoProfissional status,
            UUID clientePacienteId,
            Boolean ativo
    );
}
