package br.com.atendepro.modules.auth.application.port.out;

import java.util.Optional;
import java.util.UUID;

import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

public interface CarregarUsuarioPorIdPort {

    Optional<UsuarioAutenticacao> carregarUsuarioPorId(UUID usuarioId);
}
