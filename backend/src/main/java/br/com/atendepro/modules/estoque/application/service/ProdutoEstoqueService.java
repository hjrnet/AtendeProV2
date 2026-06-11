package br.com.atendepro.modules.estoque.application.service;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.estoque.application.command.CadastrarProdutoEstoqueCommand;
import br.com.atendepro.modules.estoque.application.port.in.BuscarProdutoEstoqueUseCase;
import br.com.atendepro.modules.estoque.application.port.in.CadastrarProdutoEstoqueUseCase;
import br.com.atendepro.modules.estoque.application.port.in.ListarProdutosEstoqueUseCase;
import br.com.atendepro.modules.estoque.application.port.out.CarregarProdutoEstoquePorIdPort;
import br.com.atendepro.modules.estoque.application.port.out.ListarProdutosEstoquePort;
import br.com.atendepro.modules.estoque.application.port.out.SalvarProdutoEstoquePort;
import br.com.atendepro.modules.estoque.application.result.ProdutoEstoqueResult;
import br.com.atendepro.modules.estoque.domain.model.ProdutoEstoque;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
public class ProdutoEstoqueService implements
        CadastrarProdutoEstoqueUseCase,
        BuscarProdutoEstoqueUseCase,
        ListarProdutosEstoqueUseCase {

    private final SalvarProdutoEstoquePort salvarProdutoEstoquePort;
    private final CarregarProdutoEstoquePorIdPort carregarProdutoEstoquePorIdPort;
    private final ListarProdutosEstoquePort listarProdutosEstoquePort;
    private final TenantAccessService tenantAccessService;
    private final PermissaoAcessoService permissaoAcessoService;
    private final Clock clock;

    public ProdutoEstoqueService(
            SalvarProdutoEstoquePort salvarProdutoEstoquePort,
            CarregarProdutoEstoquePorIdPort carregarProdutoEstoquePorIdPort,
            ListarProdutosEstoquePort listarProdutosEstoquePort,
            TenantAccessService tenantAccessService,
            PermissaoAcessoService permissaoAcessoService,
            Clock clock
    ) {
        this.salvarProdutoEstoquePort = salvarProdutoEstoquePort;
        this.carregarProdutoEstoquePorIdPort = carregarProdutoEstoquePorIdPort;
        this.listarProdutosEstoquePort = listarProdutosEstoquePort;
        this.tenantAccessService = tenantAccessService;
        this.permissaoAcessoService = permissaoAcessoService;
        this.clock = clock;
    }

    @Override
    public ProdutoEstoqueResult cadastrarProdutoEstoque(CadastrarProdutoEstoqueCommand command) {
        validarPermissao();
        UUID empresaId = resolverEmpresaId(command.empresaId());
        ProdutoEstoque produto = ProdutoEstoque.cadastrar(
                empresaId,
                command.nome(),
                command.categoria(),
                command.lote(),
                command.validade(),
                command.fornecedorNome(),
                command.fornecedorDocumento(),
                command.numeroPedidoCompra(),
                command.dataCompra(),
                command.statusCompra(),
                command.unidade(),
                command.quantidadeAtual(),
                command.custoUnitario(),
                command.estoqueMinimo(),
                Instant.now(clock)
        );
        salvarProdutoEstoquePort.salvarProdutoEstoque(produto);
        return ProdutoEstoqueResult.de(produto);
    }

    @Override
    public Optional<ProdutoEstoqueResult> buscarProdutoEstoquePorId(UUID produtoId) {
        validarPermissao();
        return carregarProdutoEstoquePorIdPort.carregarProdutoEstoquePorId(produtoId)
                .filter(produto -> {
                    tenantAccessService.validarAcessoEmpresa(produto.empresaId());
                    return true;
                })
                .map(ProdutoEstoqueResult::de);
    }

    @Override
    public ResultadoPaginado<ProdutoEstoqueResult> listarProdutosEstoque(
            UUID empresaId,
            Paginacao paginacao,
            String busca,
            String categoria,
            Boolean ativo,
            LocalDate vencendoAte
    ) {
        validarPermissao();
        UUID empresaResolvida = resolverEmpresaId(empresaId);
        var produtos = listarProdutosEstoquePort.listarProdutosEstoque(
                empresaResolvida,
                paginacao,
                busca,
                categoria,
                ativo,
                vencendoAte
        );
        return new ResultadoPaginado<>(
                produtos.itens().stream().map(ProdutoEstoqueResult::de).toList(),
                produtos.totalItens(),
                produtos.pagina(),
                produtos.tamanho()
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
            throw new BusinessException("ESTOQUE_EMPRESA_OBRIGATORIA", "Empresa e obrigatoria para operar estoque.");
        }
        return empresaIdSolicitada;
    }

    private void validarPermissao() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.GERENCIAR_ESTOQUE);
    }
}
