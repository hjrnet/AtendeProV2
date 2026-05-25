package br.com.atendepro.modules.auth.domain.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UsuarioAutenticacao(
        UUID id,
        EmailUsuario email,
        String nome,
        String senhaHash,
        Set<PerfilAcesso> perfis,
        boolean ativo,
        Instant criadoEm
) {

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
}
