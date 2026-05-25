package br.com.atendepro.modules.assinatura.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.assinatura.domain.model.TrialAssinatura;

public interface CarregarTrialAtivoPorEmpresaPort {

    Optional<TrialAssinatura> carregarTrialAtivoPorEmpresa(UUID empresaId);
}
