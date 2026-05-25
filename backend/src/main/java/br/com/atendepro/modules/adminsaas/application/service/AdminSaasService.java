package br.com.atendepro.modules.adminsaas.application.service;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.result.AdminSaasStatusResult;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;

@Service
@Profile("!test")
public class AdminSaasService implements ConsultarAdminSaasUseCase {

    private final PermissaoAcessoService permissaoAcessoService;

    public AdminSaasService(PermissaoAcessoService permissaoAcessoService) {
        this.permissaoAcessoService = permissaoAcessoService;
    }

    @Override
    public AdminSaasStatusResult consultarStatus() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
        return new AdminSaasStatusResult(
                "AtendePro",
                "R2",
                "ADMIN_SAAS_OPERACIONAL",
                List.of(
                        "dashboard-admin-saas",
                        "gestao-empresas",
                        "planos-assinaturas"
                )
        );
    }
}
