package br.com.atendepro.modules.auth.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.command.CadastrarAdministradorEmpresaCommand;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.application.port.in.CadastrarAdministradorEmpresaUseCase;
import br.com.atendepro.modules.auth.application.port.out.CarregarUsuarioPorEmailPort;
import br.com.atendepro.modules.auth.application.port.out.CriptografarSenhaPort;
import br.com.atendepro.modules.auth.application.port.out.SalvarUsuarioAutenticacaoPort;
import br.com.atendepro.modules.auth.application.result.AdministradorEmpresaResult;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.auth.domain.model.UsuarioAutenticacao;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.port.out.CarregarEmpresaPorIdPort;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class CadastrarAdministradorEmpresaService implements CadastrarAdministradorEmpresaUseCase {

    private final CarregarEmpresaPorIdPort carregarEmpresaPorIdPort;
    private final CarregarUsuarioPorEmailPort carregarUsuarioPorEmailPort;
    private final CriptografarSenhaPort criptografarSenhaPort;
    private final SalvarUsuarioAutenticacaoPort salvarUsuarioAutenticacaoPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public CadastrarAdministradorEmpresaService(
            CarregarEmpresaPorIdPort carregarEmpresaPorIdPort,
            CarregarUsuarioPorEmailPort carregarUsuarioPorEmailPort,
            CriptografarSenhaPort criptografarSenhaPort,
            SalvarUsuarioAutenticacaoPort salvarUsuarioAutenticacaoPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarEmpresaPorIdPort = carregarEmpresaPorIdPort;
        this.carregarUsuarioPorEmailPort = carregarUsuarioPorEmailPort;
        this.criptografarSenhaPort = criptografarSenhaPort;
        this.salvarUsuarioAutenticacaoPort = salvarUsuarioAutenticacaoPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public AdministradorEmpresaResult cadastrarAdministradorEmpresa(CadastrarAdministradorEmpresaCommand command) {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.CADASTRAR_ADMINISTRADOR_EMPRESA);
        tenantAccessService.validarAcessoEmpresa(command.empresaId());
        carregarEmpresaPorIdPort.carregarEmpresaPorId(command.empresaId())
                .orElseThrow(() -> new BusinessException("EMPRESA_NAO_ENCONTRADA", "Empresa nao encontrada."));
        carregarUsuarioPorEmailPort.carregarUsuarioPorEmail(command.email())
                .ifPresent(usuario -> {
                    throw new BusinessException("USUARIO_EMAIL_JA_CADASTRADO", "Email de usuario ja cadastrado.");
                });

        UsuarioAutenticacao usuario = new UsuarioAutenticacao(
                UUID.randomUUID(),
                command.empresaId(),
                command.email(),
                command.nome().trim(),
                criptografarSenhaPort.criptografarSenha(command.senha()),
                Set.of(PerfilAcesso.EMPRESA_ADMIN),
                true,
                Instant.now(clock)
        );
        salvarUsuarioAutenticacaoPort.salvarUsuarioAutenticacao(usuario);
        return AdministradorEmpresaResult.de(usuario);
    }
}
