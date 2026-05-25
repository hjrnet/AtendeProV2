package br.com.atendepro.modules.empresa.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.command.CadastrarEmpresaCommand;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.port.in.BuscarEmpresaUseCase;
import br.com.atendepro.modules.empresa.application.port.in.CadastrarEmpresaUseCase;
import br.com.atendepro.modules.empresa.application.port.in.ListarEmpresasUseCase;
import br.com.atendepro.modules.empresa.application.port.out.CarregarEmpresaPorDocumentoPort;
import br.com.atendepro.modules.empresa.application.port.out.CarregarEmpresaPorIdPort;
import br.com.atendepro.modules.empresa.application.port.out.ListarEmpresasPort;
import br.com.atendepro.modules.empresa.application.port.out.SalvarEmpresaPort;
import br.com.atendepro.modules.empresa.application.result.EmpresaResult;
import br.com.atendepro.modules.empresa.domain.model.EmpresaTenant;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class EmpresaService implements CadastrarEmpresaUseCase, BuscarEmpresaUseCase, ListarEmpresasUseCase {

    private final CarregarEmpresaPorDocumentoPort carregarEmpresaPorDocumentoPort;
    private final CarregarEmpresaPorIdPort carregarEmpresaPorIdPort;
    private final ListarEmpresasPort listarEmpresasPort;
    private final SalvarEmpresaPort salvarEmpresaPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public EmpresaService(
            CarregarEmpresaPorDocumentoPort carregarEmpresaPorDocumentoPort,
            CarregarEmpresaPorIdPort carregarEmpresaPorIdPort,
            ListarEmpresasPort listarEmpresasPort,
            SalvarEmpresaPort salvarEmpresaPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.carregarEmpresaPorDocumentoPort = carregarEmpresaPorDocumentoPort;
        this.carregarEmpresaPorIdPort = carregarEmpresaPorIdPort;
        this.listarEmpresasPort = listarEmpresasPort;
        this.salvarEmpresaPort = salvarEmpresaPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public EmpresaResult cadastrarEmpresa(CadastrarEmpresaCommand command) {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.CADASTRAR_EMPRESA);
        tenantAccessService.validarOperacaoGlobal();
        carregarEmpresaPorDocumentoPort.carregarEmpresaPorDocumento(command.documento())
                .ifPresent(empresa -> {
                    throw new BusinessException("EMPRESA_DOCUMENTO_JA_CADASTRADO", "Documento de empresa ja cadastrado.");
                });

        EmpresaTenant empresa = new EmpresaTenant(
                UUID.randomUUID(),
                command.nomeFantasia(),
                command.razaoSocial(),
                command.documento(),
                command.email(),
                command.telefone(),
                true,
                Instant.now(clock)
        );
        salvarEmpresaPort.salvarEmpresa(empresa);
        return EmpresaResult.de(empresa);
    }

    @Override
    public Optional<EmpresaResult> buscarEmpresaPorId(UUID empresaId) {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.VISUALIZAR_EMPRESA);
        tenantAccessService.validarAcessoEmpresa(empresaId);
        return carregarEmpresaPorIdPort.carregarEmpresaPorId(empresaId)
                .map(EmpresaResult::de);
    }

    @Override
    public ResultadoPaginado<EmpresaResult> listarEmpresas(Paginacao paginacao) {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.LISTAR_EMPRESAS);
        Optional<UUID> empresaRestrita = tenantAccessService.empresaRestrita();
        if (empresaRestrita.isPresent()) {
            return listarEmpresaRestrita(empresaRestrita.get(), paginacao);
        }
        ResultadoPaginado<EmpresaTenant> empresas = listarEmpresasPort.listarEmpresas(paginacao);
        return new ResultadoPaginado<>(
                empresas.itens().stream().map(EmpresaResult::de).toList(),
                empresas.totalItens(),
                empresas.pagina(),
                empresas.tamanho()
        );
    }

    private ResultadoPaginado<EmpresaResult> listarEmpresaRestrita(UUID empresaId, Paginacao paginacao) {
        Optional<EmpresaTenant> empresa = carregarEmpresaPorIdPort.carregarEmpresaPorId(empresaId);
        long total = empresa.isPresent() ? 1 : 0;
        return new ResultadoPaginado<>(
                paginacao.pagina() == 0 ? empresa.map(EmpresaResult::de).stream().toList() : List.of(),
                total,
                paginacao.pagina(),
                paginacao.tamanho()
        );
    }
}
