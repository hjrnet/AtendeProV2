package br.com.atendepro.modules.plano.application.port.out;

import java.util.Optional;

import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;

public interface CarregarPlanoPorCodigoPort {

    Optional<PlanoAssinatura> carregarPlanoPorCodigo(String codigo);
}
