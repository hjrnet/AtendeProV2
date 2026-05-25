package br.com.atendepro.modules.empresa.application.context;

import java.util.Set;
import java.util.UUID;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;

public record TenantContext(
        UUID empresaId,
        UUID usuarioId,
        Set<PerfilAcesso> perfis
) {

    public TenantContext {
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do contexto de tenant e obrigatoria");
        }
        perfis = perfis == null ? Set.of() : Set.copyOf(perfis);
    }
}
