package br.com.atendepro.modules.auth.application.port.out;

import java.util.UUID;

public interface AtualizarSenhaUsuarioPort {

    void atualizarSenhaUsuario(UUID usuarioId, String senhaHash);
}
