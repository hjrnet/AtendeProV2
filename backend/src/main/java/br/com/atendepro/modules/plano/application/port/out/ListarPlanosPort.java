package br.com.atendepro.modules.plano.application.port.out;

import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

public interface ListarPlanosPort {

    ResultadoPaginado<PlanoAssinatura> listarPlanos(Paginacao paginacao, String busca, Boolean ativo);
}
