package br.com.atendepro.modules.auth.application.port.out;

import java.util.Optional;

import br.com.atendepro.modules.auth.domain.model.EmailUsuario;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

public interface CarregarUsuarioPorEmailPort {

    Optional<UsuarioAutenticacao> carregarUsuarioPorEmail(EmailUsuario email);
}
