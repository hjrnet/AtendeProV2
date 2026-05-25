package br.com.atendepro.modules.auth.application.port.in;

import br.com.atendepro.modules.auth.application.command.RedefinirSenhaCommand;

public interface RedefinirSenhaUseCase {

    void redefinirSenha(RedefinirSenhaCommand command);
}
