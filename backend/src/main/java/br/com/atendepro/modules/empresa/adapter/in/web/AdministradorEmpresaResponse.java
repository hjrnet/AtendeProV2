package br.com.atendepro.modules.empresa.adapter.in.web;

import java.util.Set;
import java.util.UUID;

import br.com.atendepro.modules.auth.application.result.AdministradorEmpresaResult;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;

public record AdministradorEmpresaResponse(
        UUID id,
        UUID empresaId,
        String nome,
        String email,
        Set<PerfilAcesso> perfis,
        boolean ativo
) {

    public AdministradorEmpresaResponse {
        perfis = Set.copyOf(perfis);
    }

    static AdministradorEmpresaResponse de(AdministradorEmpresaResult result) {
        return new AdministradorEmpresaResponse(
                result.id(),
                result.empresaId(),
                result.nome(),
                result.email(),
                result.perfis(),
                result.ativo()
        );
    }
}
