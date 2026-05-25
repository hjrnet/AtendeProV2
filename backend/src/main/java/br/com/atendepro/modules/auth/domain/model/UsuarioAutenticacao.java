package br.com.atendepro.modules.auth.domain.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UsuarioAutenticacao(
        UUID id,
        UUID empresaId,
        EmailUsuario email,
        String nome,
        String senhaHash,
        Set<PerfilAcesso> perfis,
        boolean ativo,
        Instant criadoEm
) {

    public UsuarioAutenticacao(
            UUID id,
            EmailUsuario email,
            String nome,
            String senhaHash,
            Set<PerfilAcesso> perfis,
            boolean ativo,
            Instant criadoEm
    ) {
        this(id, null, email, nome, senhaHash, perfis, ativo, criadoEm);
    }

    public UsuarioAutenticacao {
        perfis = Set.copyOf(perfis);
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome e obrigatorio");
        }
        if (senhaHash == null || senhaHash.isBlank()) {
            throw new IllegalArgumentException("senha hash e obrigatoria");
        }
        if (perfis.isEmpty()) {
            throw new IllegalArgumentException("ao menos um perfil e obrigatorio");
        }
    }

    public boolean possuiPerfil(PerfilAcesso perfil) {
        return perfis.contains(perfil);
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

    public boolean possuiPermissao(PermissaoAcesso permissao) {
        return permissoes().contains(permissao);
    }
}
