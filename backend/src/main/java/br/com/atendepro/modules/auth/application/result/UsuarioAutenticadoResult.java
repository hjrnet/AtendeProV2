package br.com.atendepro.modules.auth.application.result;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;

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

    public Set<PermissaoAcesso> permissoes() {
        return perfis.stream()
                .flatMap(perfil -> perfil.permissoes().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<String> authorities() {
        return permissoes().stream()
                .map(PermissaoAcesso::authority)
                .collect(Collectors.toUnmodifiableSet());
    }
}
