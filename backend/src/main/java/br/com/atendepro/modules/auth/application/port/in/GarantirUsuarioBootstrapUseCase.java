package br.com.atendepro.modules.auth.application.port.in;

import br.com.atendepro.modules.auth.application.command.CadastrarUsuarioBootstrapCommand;
import br.com.atendepro.modules.auth.application.result.UsuarioBootstrapResult;

public interface GarantirUsuarioBootstrapUseCase {

    UsuarioBootstrapResult garantirUsuarioBootstrap(CadastrarUsuarioBootstrapCommand command);
}
