package br.com.atendepro.modules.spaces.application.port.in;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.OcupacaoSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.StatusOcupacaoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarOcupacoesSpacesUseCase {

    ResultadoPaginado<OcupacaoSpacesResult> listarOcupacoes(
            UUID empresaId,
            Paginacao paginacao,
            UUID recursoId,
            Instant inicioEm,
            Instant fimEm,
            StatusOcupacaoSpaces status
    );
}
