package br.com.atendepro.modules.spaces.application.port.out;

import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.PacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarPacotesSublocacaoSpacesPort {

    ResultadoPaginado<PacoteSublocacaoSpaces> listarPacotes(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            UUID recursoId,
            TipoPacoteSublocacaoSpaces tipo,
            Boolean ativo
    );
}
