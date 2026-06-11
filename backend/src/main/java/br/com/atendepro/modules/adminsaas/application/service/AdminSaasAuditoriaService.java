package br.com.atendepro.modules.adminsaas.application.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.adminsaas.application.port.in.ConsultarAuditoriaOperacionalAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.out.ConsultarAuditoriaOperacionalAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.AuditoriaOperacionalAdminSaasResult;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;

@Service
@Profile("!test")
public class AdminSaasAuditoriaService implements ConsultarAuditoriaOperacionalAdminSaasUseCase {

    private final PermissaoAcessoService permissaoAcessoService;
    private final ConsultarAuditoriaOperacionalAdminSaasPort consultarAuditoriaOperacionalAdminSaasPort;

    public AdminSaasAuditoriaService(
            PermissaoAcessoService permissaoAcessoService,
            ConsultarAuditoriaOperacionalAdminSaasPort consultarAuditoriaOperacionalAdminSaasPort
    ) {
        this.permissaoAcessoService = permissaoAcessoService;
        this.consultarAuditoriaOperacionalAdminSaasPort = consultarAuditoriaOperacionalAdminSaasPort;
    }

    @Override
    public AuditoriaOperacionalAdminSaasResult consultarAuditoriaOperacional() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
        return consultarAuditoriaOperacionalAdminSaasPort.carregarAuditoriaOperacional();
    }
}
