package br.com.atendepro.modules.auth.application.result;

import java.util.Set;
import java.util.UUID;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;

public record UsuarioAutenticadoResult(
        UUID id,
        UUID empresaId,
        String nome,
        String email,
        Set<PerfilAcesso> perfis
) {

    public UsuarioAutenticadoResult(UUID id, String nome, String email, Set<PerfilAcesso> perfis) {
        this(id, null, nome, email, perfis);
    }

    public UsuarioAutenticadoResult {
        perfis = Set.copyOf(perfis);
    }
}
