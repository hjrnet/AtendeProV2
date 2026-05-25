package br.com.atendepro.modules.spaces.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.RecursoSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarRecursosSpacesUseCase {

    ResultadoPaginado<RecursoSpacesResult> listarRecursos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoRecursoSpaces tipo,
            Boolean ativo
    );
}
