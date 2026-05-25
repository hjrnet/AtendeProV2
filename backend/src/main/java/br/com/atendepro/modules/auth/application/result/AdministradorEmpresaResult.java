package br.com.atendepro.modules.auth.application.result;

import java.util.Set;
import java.util.UUID;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;

public record AdministradorEmpresaResult(
        UUID id,
        UUID empresaId,
        String nome,
        String email,
        Set<PerfilAcesso> perfis,
        boolean ativo
) {

    public AdministradorEmpresaResult {
        perfis = Set.copyOf(perfis);
    }

    public static AdministradorEmpresaResult de(UsuarioAutenticacao usuario) {
        return new AdministradorEmpresaResult(
                usuario.id(),
                usuario.empresaId(),
                usuario.nome(),
                usuario.email().valor(),
                usuario.perfis(),
                usuario.ativo()
        );
    }
}
