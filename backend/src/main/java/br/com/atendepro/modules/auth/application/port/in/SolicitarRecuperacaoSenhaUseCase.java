package br.com.atendepro.modules.auth.application.port.in;

import br.com.atendepro.modules.auth.application.command.SolicitarRecuperacaoSenhaCommand;
import br.com.atendepro.modules.auth.application.result.SolicitarRecuperacaoSenhaResult;

public interface SolicitarRecuperacaoSenhaUseCase {

    SolicitarRecuperacaoSenhaResult solicitarRecuperacaoSenha(SolicitarRecuperacaoSenhaCommand command);
}
