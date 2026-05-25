package br.com.atendepro.modules.plano.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;

public interface CarregarPlanoPorIdPort {

    Optional<PlanoAssinatura> carregarPlanoPorId(UUID planoId);
}
