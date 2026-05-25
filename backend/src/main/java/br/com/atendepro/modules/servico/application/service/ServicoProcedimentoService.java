package br.com.atendepro.modules.servico.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.servico.application.command.CadastrarServicoProcedimentoCommand;
import br.com.atendepro.modules.servico.application.port.in.BuscarServicoProcedimentoUseCase;
import br.com.atendepro.modules.servico.application.port.in.CadastrarServicoProcedimentoUseCase;
import br.com.atendepro.modules.servico.application.port.in.ListarServicosProcedimentosUseCase;
import br.com.atendepro.modules.servico.application.port.out.CarregarServicoProcedimentoPorIdPort;
import br.com.atendepro.modules.servico.application.port.out.ListarServicosProcedimentosPort;
import br.com.atendepro.modules.servico.application.port.out.SalvarServicoProcedimentoPort;
import br.com.atendepro.modules.servico.application.result.ServicoProcedimentoResult;
import br.com.atendepro.modules.servico.domain.model.ServicoProcedimento;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class ServicoProcedimentoService implements
        CadastrarServicoProcedimentoUseCase,
        BuscarServicoProcedimentoUseCase,
        ListarServicosProcedimentosUseCase {

    private final SalvarServicoProcedimentoPort salvarServicoProcedimentoPort;
    private final CarregarServicoProcedimentoPorIdPort carregarServicoProcedimentoPorIdPort;
    private final ListarServicosProcedimentosPort listarServicosProcedimentosPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public ServicoProcedimentoService(
            SalvarServicoProcedimentoPort salvarServicoProcedimentoPort,
            CarregarServicoProcedimentoPorIdPort carregarServicoProcedimentoPorIdPort,
            ListarServicosProcedimentosPort listarServicosProcedimentosPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarServicoProcedimentoPort = salvarServicoProcedimentoPort;
        this.carregarServicoProcedimentoPorIdPort = carregarServicoProcedimentoPorIdPort;
        this.listarServicosProcedimentosPort = listarServicosProcedimentosPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public ServicoProcedimentoResult cadastrarServicoProcedimento(CadastrarServicoProcedimentoCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        ServicoProcedimento servico = ServicoProcedimento.cadastrar(
                empresaId,
                command.nome(),
                command.descricao(),
                command.area(),
                command.duracaoMinutos(),
                command.precoBase(),
                Instant.now(clock)
        );
        salvarServicoProcedimentoPort.salvarServicoProcedimento(servico);
        return ServicoProcedimentoResult.de(servico);
    }

    @Override
    public Optional<ServicoProcedimentoResult> buscarServicoProcedimentoPorId(UUID servicoId) {
        validarPermissao();
        return carregarServicoProcedimentoPorIdPort.carregarServicoProcedimentoPorId(servicoId)
                .filter(servico -> {
                    tenantAccessService.validarAcessoEmpresa(servico.empresaId());
                    return true;
                })
                .map(ServicoProcedimentoResult::de);
    }

    @Override
    public ResultadoPaginado<ServicoProcedimentoResult> listarServicosProcedimentos(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            AreaCliente area,
            Boolean ativo
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        var servicos = listarServicosProcedimentosPort.listarServicosProcedimentos(empresaResolvida, paginacao, busca, area, ativo);
        return new ResultadoPaginado<>(
                servicos.itens().stream().map(ServicoProcedimentoResult::de).toList(),
                servicos.totalItens(),
                servicos.pagina(),
                servicos.tamanho()
        );
    }

    private UUID resolverEmpresaId(UUID empresaIdSolicitada) {
        Optional<UUID> empresaRestrita = tenantAccessService.empresaRestrita();
        if (empresaRestrita.isPresent()) {
            if (empresaIdSolicitada != null) {
                tenantAccessService.validarAcessoEmpresa(empresaIdSolicitada);
            }
            return empresaRestrita.get();
        }
        if (empresaIdSolicitada == null) {
            throw new BusinessException("SERVICO_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar servicos.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_SERVICOS);
    }
}
