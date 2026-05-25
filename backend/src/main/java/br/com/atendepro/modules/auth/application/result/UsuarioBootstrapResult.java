package br.com.atendepro.modules.auth.application.result;

import java.util.Set;
import java.util.UUID;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;

public record UsuarioBootstrapResult(
        UUID id,
        String nome,
        String email,
        Set<PerfilAcesso> perfis,
        boolean criado
) {

    public UsuarioBootstrapResult {
        perfis = Set.copyOf(perfis);
    }
}
