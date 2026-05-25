package br.com.atendepro.modules.estoque.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.exception.PermissaoNegadaException;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.estoque.application.command.CadastrarProdutoEstoqueCommand;
import br.com.atendepro.modules.estoque.application.port.out.SalvarProdutoEstoquePort;
import br.com.atendepro.modules.estoque.domain.model.ProdutoEstoque;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

class ProdutoEstoqueServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("ebad8b8f-88d8-44b7-9ddf-5753f55f3f3b");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCadastrarProdutoNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarProdutoFake salvarProdutoFake = new SalvarProdutoFake();
        ProdutoEstoqueService service = service(salvarProdutoFake);

        var result = service.cadastrarProdutoEstoque(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.nome()).isEqualTo("Seringa descartavel");
        assertThat(salvarProdutoFake.produtoSalvo.quantidadeAtual()).isEqualByComparingTo("50.000");
    }

    @Test
    void deveListarProdutosDaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        ProdutoEstoque produto = produto();
        ProdutoEstoqueService service = new ProdutoEstoqueService(
                produtoSalvo -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, busca, categoria, ativo, vencendoAte) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(busca).isEqualTo("seringa");
                    assertThat(categoria).isEqualTo("Insumos");
                    assertThat(vencendoAte).isEqualTo(LocalDate.parse("2026-12-31"));
                    return new ResultadoPaginado<>(List.of(produto), 1, paginacao.pagina(), paginacao.tamanho());
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarProdutosEstoque(
                null,
                new Paginacao(0, 20),
                "seringa",
                "Insumos",
                true,
                LocalDate.parse("2026-12-31")
        );

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("nome").containsExactly("Seringa descartavel");
    }

    @Test
    void naoDeveOperarEstoqueSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        ProdutoEstoqueService service = service(produto -> {
        });

        assertThatThrownBy(() -> service.listarProdutosEstoque(null, new Paginacao(0, 20), null, null, true, null))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private ProdutoEstoqueService service(SalvarProdutoEstoquePort salvarPort) {
        return new ProdutoEstoqueService(
                salvarPort,
                id -> Optional.empty(),
                (empresaId, paginacao, busca, categoria, ativo, vencendoAte) ->
                        new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CadastrarProdutoEstoqueCommand command(UUID empresaId) {
        return new CadastrarProdutoEstoqueCommand(
                empresaId,
                "Seringa descartavel",
                "Insumos",
                "LOTE-001",
                LocalDate.parse("2026-12-31"),
                "UN",
                new BigDecimal("50.000"),
                new BigDecimal("1.25"),
                new BigDecimal("10.000")
        );
    }

    private ProdutoEstoque produto() {
        return ProdutoEstoque.cadastrar(
                EMPRESA_ID,
                "Seringa descartavel",
                "Insumos",
                "LOTE-001",
                LocalDate.parse("2026-12-31"),
                "UN",
                new BigDecimal("50.000"),
                new BigDecimal("1.25"),
                new BigDecimal("10.000"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarProdutoFake implements SalvarProdutoEstoquePort {

        private ProdutoEstoque produtoSalvo;

        @Override
        public void salvarProdutoEstoque(ProdutoEstoque produto) {
            this.produtoSalvo = produto;
        }
    }
}
