package br.com.atendepro.modules.documento.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.documento.application.result.ModeloDocumentoProfissionalResult;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarModelosDocumentoProfissionalUseCase {

    ResultadoPaginado<ModeloDocumentoProfissionalResult> listarModelos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoDocumentoProfissional tipo,
            Boolean ativo
    );
}
