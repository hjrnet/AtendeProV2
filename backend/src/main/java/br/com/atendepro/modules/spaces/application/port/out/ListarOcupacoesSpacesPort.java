package br.com.atendepro.modules.spaces.application.port.out;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.OcupacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.StatusOcupacaoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarOcupacoesSpacesPort {

    ResultadoPaginado<OcupacaoSpaces> listarOcupacoes(
            UUID empresaId,
            Paginacao paginacao,
            UUID recursoId,
            Instant inicioEm,
            Instant fimEm,
            StatusOcupacaoSpaces status
    );
}
