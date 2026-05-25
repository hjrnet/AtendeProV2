package br.com.atendepro.modules.custo.application.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.custo.application.command.CadastrarCustoGeralCommand;
import br.com.atendepro.modules.custo.application.port.in.BuscarCustoGeralUseCase;
import br.com.atendepro.modules.custo.application.port.in.CadastrarCustoGeralUseCase;
import br.com.atendepro.modules.custo.application.port.in.ListarCustosGeraisUseCase;
import br.com.atendepro.modules.custo.application.port.out.CarregarCustoGeralPorIdPort;
import br.com.atendepro.modules.custo.application.port.out.ListarCustosGeraisPort;
import br.com.atendepro.modules.custo.application.port.out.SalvarCustoGeralPort;
import br.com.atendepro.modules.custo.application.result.CustoGeralResult;
import br.com.atendepro.modules.custo.domain.model.CustoGeral;
import br.com.atendepro.modules.custo.domain.model.TipoCustoGeral;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class CustoGeralService implements CadastrarCustoGeralUseCase, BuscarCustoGeralUseCase, ListarCustosGeraisUseCase {

    private final SalvarCustoGeralPort salvarCustoGeralPort;
    private final CarregarCustoGeralPorIdPort carregarCustoGeralPorIdPort;
    private final ListarCustosGeraisPort listarCustosGeraisPort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public CustoGeralService(
            SalvarCustoGeralPort salvarCustoGeralPort,
            CarregarCustoGeralPorIdPort carregarCustoGeralPorIdPort,
            ListarCustosGeraisPort listarCustosGeraisPort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarCustoGeralPort = salvarCustoGeralPort;
        this.carregarCustoGeralPorIdPort = carregarCustoGeralPorIdPort;
        this.listarCustosGeraisPort = listarCustosGeraisPort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public CustoGeralResult cadastrarCustoGeral(CadastrarCustoGeralCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        CustoGeral custo = CustoGeral.cadastrar(
                empresaId,
                command.descricao(),
                command.tipo(),
                command.categoria(),
                command.valor(),
                command.competencia(),
                Instant.now(clock)
        );
        salvarCustoGeralPort.salvarCustoGeral(custo);
        return CustoGeralResult.de(custo);
    }

    @Override
    public Optional<CustoGeralResult> buscarCustoGeralPorId(UUID custoId) {
        validarPermissao();
        return carregarCustoGeralPorIdPort.carregarCustoGeralPorId(custoId)
                .filter(custo -> {
                    tenantAccessService.validarAcessoEmpresa(custo.empresaId());
                    return true;
                })
                .map(CustoGeralResult::de);
    }

    @Override
    public ResultadoPaginado<CustoGeralResult> listarCustosGerais(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            TipoCustoGeral tipo,
            String categoria,
            Boolean ativo
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        var custos = listarCustosGeraisPort.listarCustosGerais(empresaResolvida, paginacao, busca, tipo, categoria, ativo);
        return new ResultadoPaginado<>(
                custos.itens().stream().map(CustoGeralResult::de).toList(),
                custos.totalItens(),
                custos.pagina(),
                custos.tamanho()
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
            throw new BusinessException("CUSTO_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar custos.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_CUSTOS);
    }
}
