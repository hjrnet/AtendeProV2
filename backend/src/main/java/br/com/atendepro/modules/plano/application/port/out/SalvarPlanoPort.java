package br.com.atendepro.modules.plano.application.port.out;

import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;

public interface SalvarPlanoPort {

    void salvarPlano(PlanoAssinatura plano);
}
