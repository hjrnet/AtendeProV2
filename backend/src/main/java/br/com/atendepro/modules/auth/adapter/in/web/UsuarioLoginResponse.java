package br.com.atendepro.modules.auth.adapter.in.web;

import java.util.Set;
import java.util.UUID;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;

public record UsuarioLoginResponse(
        UUID id,
        UUID empresaId,
        String nome,
        String email,
        Set<PerfilAcesso> perfis
) {

    public UsuarioLoginResponse {
        perfis = Set.copyOf(perfis);
    }
}
