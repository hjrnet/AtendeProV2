package br.com.atendepro.modules.spaces.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.RecursoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarRecursosSpacesPort {

    ResultadoPaginado<RecursoSpaces> listarRecursos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoRecursoSpaces tipo,
            Boolean ativo
    );
}
