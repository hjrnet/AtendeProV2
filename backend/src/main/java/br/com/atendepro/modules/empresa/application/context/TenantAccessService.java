package br.com.atendepro.modules.empresa.application.context;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.domain.exception.AcessoTenantNegadoException;

@Service
public class TenantAccessService {

    private static final Set<PerfilAcesso> PERFIS_GLOBAIS = Set.of(PerfilAcesso.SUPER_ADMIN, PerfilAcesso.SUPORTE);

    public Optional<UUID> empresaRestrita() {
        return TenantContextHolder.contextoAtual()
                .filter(contexto -> !possuiPerfilGlobal(contexto))
                .map(TenantContext::empresaId);
    }

    public void validarAcessoEmpresa(UUID empresaId) {
        empresaRestrita()
                .filter(empresaRestrita -> !empresaRestrita.equals(empresaId))
                .ifPresent(empresaRestrita -> {
                    throw acessoNegado();
                });
    }

    public void validarOperacaoGlobal() {
        if (empresaRestrita().isPresent()) {
            throw acessoNegado();
        }
    }

    private boolean possuiPerfilGlobal(TenantContext contexto) {
        return contexto.perfis().stream().anyMatch(PERFIS_GLOBAIS::contains);
    }

    private AcessoTenantNegadoException acessoNegado() {
        return new AcessoTenantNegadoException(
                "TENANT_ACESSO_NEGADO",
                "Usuario nao possui acesso a esta empresa."
        );
    }
}
