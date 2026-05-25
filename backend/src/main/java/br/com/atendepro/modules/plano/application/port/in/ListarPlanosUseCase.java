package br.com.atendepro.modules.plano.application.port.in;

import br.com.atendepro.modules.plano.application.result.PlanoResult;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarPlanosUseCase {

    ResultadoPaginado<PlanoResult> listarPlanos(Paginacao paginacao, String busca, Boolean ativo);
}
