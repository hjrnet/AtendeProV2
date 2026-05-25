package br.com.atendepro.modules.spaces.application.port.in;

import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.PacoteSublocacaoSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarPacotesSublocacaoSpacesUseCase {

    ResultadoPaginado<PacoteSublocacaoSpacesResult> listarPacotes(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            UUID recursoId,
            TipoPacoteSublocacaoSpaces tipo,
            Boolean ativo
    );
}
