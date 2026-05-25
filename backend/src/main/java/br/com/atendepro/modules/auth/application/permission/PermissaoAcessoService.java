package br.com.atendepro.modules.auth.application.permission;

import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;

@Service
public class PermissaoAcessoService {

    public void validarPermissao(PermissaoAcesso permissao) {
        TenantContextHolder.contextoAtual()
                .filter(contexto -> !possuiPermissao(contexto, permissao))
                .ifPresent(contexto -> {
                    throw new PermissaoNegadaException(
                            "PERMISSAO_NEGADA",
                            "Usuario nao possui permissao para executar esta acao."
                    );
                });
    }

    private boolean possuiPermissao(TenantContext contexto, PermissaoAcesso permissao) {
        return contexto.perfis().stream().anyMatch(perfil -> perfil.possuiPermissao(permissao));
    }
}
