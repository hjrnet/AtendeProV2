package br.com.atendepro.modules.plano.application.port.in;

import br.com.atendepro.modules.plano.application.command.CriarPlanoCommand;
import br.com.atendepro.modules.plano.application.result.PlanoResult;

public interface CriarPlanoUseCase {

    PlanoResult criarPlano(CriarPlanoCommand command);
}
