package br.com.atendepro.modules.adminsaas.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.adminsaas.application.command.AlterarBloqueioEmpresaAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.application.port.in.AlterarBloqueioEmpresaAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.DetalharEmpresaAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ListarEmpresasAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.in.ObservarEmpresaAdminSaasUseCase;
import br.com.atendepro.modules.adminsaas.application.port.out.AtualizarBloqueioEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.CarregarEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.ContarUsuariosEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.ListarEmpresasAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasDetalheResult;
import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasObservacaoResult;
import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasResumoResult;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

@Service
@Profile("!test")
public class AdminSaasEmpresaService implements
        ListarEmpresasAdminSaasUseCase,
        DetalharEmpresaAdminSaasUseCase,
        AlterarBloqueioEmpresaAdminSaasUseCase,
        ObservarEmpresaAdminSaasUseCase {

    private final PermissaoAcessoService permissaoAcessoService;
    private final ListarEmpresasAdminSaasPort listarEmpresasAdminSaasPort;
    private final CarregarEmpresaAdminSaasPort carregarEmpresaAdminSaasPort;
    private final AtualizarBloqueioEmpresaAdminSaasPort atualizarBloqueioEmpresaAdminSaasPort;
    private final ContarUsuariosEmpresaAdminSaasPort contarUsuariosEmpresaAdminSaasPort;
    private final Clock clock;

    public AdminSaasEmpresaService(
            PermissaoAcessoService permissaoAcessoService,
            ListarEmpresasAdminSaasPort listarEmpresasAdminSaasPort,
            CarregarEmpresaAdminSaasPort carregarEmpresaAdminSaasPort,
            AtualizarBloqueioEmpresaAdminSaasPort atualizarBloqueioEmpresaAdminSaasPort,
            ContarUsuariosEmpresaAdminSaasPort contarUsuariosEmpresaAdminSaasPort,
            Clock clock
    ) {
        this.permissaoAcessoService = permissaoAcessoService;
        this.listarEmpresasAdminSaasPort = listarEmpresasAdminSaasPort;
        this.carregarEmpresaAdminSaasPort = carregarEmpresaAdminSaasPort;
        this.atualizarBloqueioEmpresaAdminSaasPort = atualizarBloqueioEmpresaAdminSaasPort;
        this.contarUsuariosEmpresaAdminSaasPort = contarUsuariosEmpresaAdminSaasPort;
        this.clock = clock;
    }

    @Override
    public ResultadoPaginado<EmpresaAdminSaasResumoResult> listarEmpresas(Paginacao paginacao, String busca) {
        validarAcessoAdminSaas();
        return listarEmpresasAdminSaasPort.listarEmpresas(paginacao, busca);
    }

    @Override
    public Optional<EmpresaAdminSaasDetalheResult> detalharEmpresa(UUID empresaId) {
        validarAcessoAdminSaas();
        return carregarEmpresaAdminSaasPort.carregarEmpresa(empresaId);
    }

    @Override
    public Optional<EmpresaAdminSaasDetalheResult> alterarBloqueioEmpresa(
            AlterarBloqueioEmpresaAdminSaasCommand command
    ) {
        validarAcessoAdminSaas();
        return atualizarBloqueioEmpresaAdminSaasPort.atualizarBloqueioEmpresa(
                command.empresaId(),
                command.bloqueada()
        );
    }

    @Override
    public Optional<EmpresaAdminSaasObservacaoResult> observarEmpresa(UUID empresaId) {
        validarAcessoAdminSaas();
        return carregarEmpresaAdminSaasPort.carregarEmpresa(empresaId)
                .map(empresa -> new EmpresaAdminSaasObservacaoResult(
                        empresa.id(),
                        empresa.nomeFantasia(),
                        empresa.ativo(),
                        empresa.ativo() ? "ATIVA" : "BLOQUEADA",
                        contarUsuariosEmpresaAdminSaasPort.contarUsuariosVinculados(empresa.id()),
                        Instant.now(clock)
                ));
    }

    private void validarAcessoAdminSaas() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
    }
}
