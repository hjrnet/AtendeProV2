package br.com.atendepro.modules.plano.application.port.out;

import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;

public interface AtualizarPlanoPort {

    void atualizarPlano(PlanoAssinatura plano);
}
