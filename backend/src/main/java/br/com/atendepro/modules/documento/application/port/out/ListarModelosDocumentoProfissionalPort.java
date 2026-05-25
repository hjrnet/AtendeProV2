package br.com.atendepro.modules.documento.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.ModeloDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarModelosDocumentoProfissionalPort {

    ResultadoPaginado<ModeloDocumentoProfissional> listarModelos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoDocumentoProfissional tipo,
            Boolean ativo
    );
}
