package br.com.atendepro.modules.auth.application.port.in;

import br.com.atendepro.modules.auth.application.command.RenovarSessaoCommand;
import br.com.atendepro.modules.auth.application.result.AutenticacaoResult;

public interface RenovarSessaoUseCase {

    AutenticacaoResult renovarSessao(RenovarSessaoCommand command);
}
