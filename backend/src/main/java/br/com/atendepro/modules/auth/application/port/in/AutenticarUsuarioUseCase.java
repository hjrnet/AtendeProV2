package br.com.atendepro.modules.auth.application.port.in;

import br.com.atendepro.modules.auth.application.command.AutenticarUsuarioCommand;
import br.com.atendepro.modules.auth.application.result.AutenticacaoResult;

public interface AutenticarUsuarioUseCase {

    AutenticacaoResult autenticarUsuario(AutenticarUsuarioCommand command);
}
