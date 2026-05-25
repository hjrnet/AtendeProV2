package br.com.atendepro.modules.plano.application.port.in;

import java.util.Optional;

import br.com.atendepro.modules.plano.application.command.AtualizarPlanoCommand;
import br.com.atendepro.modules.plano.application.result.PlanoResult;

public interface AtualizarPlanoUseCase {

    Optional<PlanoResult> atualizarPlano(AtualizarPlanoCommand command);
}
