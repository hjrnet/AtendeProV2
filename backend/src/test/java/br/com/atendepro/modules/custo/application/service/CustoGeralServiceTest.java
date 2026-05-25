package br.com.atendepro.modules.custo.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.YearMonth;
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
import br.com.atendepro.modules.custo.application.command.CadastrarCustoGeralCommand;
import br.com.atendepro.modules.custo.application.port.out.SalvarCustoGeralPort;
import br.com.atendepro.modules.custo.domain.model.CustoGeral;
import br.com.atendepro.modules.custo.domain.model.TipoCustoGeral;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

class CustoGeralServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCadastrarCustoNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarCustoFake salvarCustoFake = new SalvarCustoFake();
        CustoGeralService service = service(salvarCustoFake);

        var result = service.cadastrarCustoGeral(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.tipo()).isEqualTo(TipoCustoGeral.FIXO);
        assertThat(salvarCustoFake.custoSalvo.valor()).isEqualByComparingTo("1200.00");
    }

    @Test
    void deveListarCustosPorTipoECategoria() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        CustoGeral custo = custo();
        CustoGeralService service = new CustoGeralService(
                custoSalvo -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, busca, tipo, categoria, ativo) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(tipo).isEqualTo(TipoCustoGeral.FIXO);
                    assertThat(categoria).isEqualTo("Estrutura");
                    return new ResultadoPaginado<>(List.of(custo), 1, paginacao.pagina(), paginacao.tamanho());
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarCustosGerais(null, new Paginacao(0, 20), null, TipoCustoGeral.FIXO, "Estrutura", true);

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("descricao").containsExactly("Aluguel");
    }

    @Test
    void naoDeveOperarCustosSemPermissao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        CustoGeralService service = service(custo -> {
        });

        assertThatThrownBy(() -> service.listarCustosGerais(null, new Paginacao(0, 20), null, null, null, true))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    private CustoGeralService service(SalvarCustoGeralPort salvarPort) {
        return new CustoGeralService(
                salvarPort,
                id -> Optional.empty(),
                (empresaId, paginacao, busca, tipo, categoria, ativo) -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CadastrarCustoGeralCommand command(UUID empresaId) {
        return new CadastrarCustoGeralCommand(
                empresaId,
                "Aluguel",
                TipoCustoGeral.FIXO,
                "Estrutura",
                new BigDecimal("1200.00"),
                YearMonth.parse("2026-05")
        );
    }

    private CustoGeral custo() {
        return CustoGeral.cadastrar(
                EMPRESA_ID,
                "Aluguel",
                TipoCustoGeral.FIXO,
                "Estrutura",
                new BigDecimal("1200.00"),
                YearMonth.parse("2026-05"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarCustoFake implements SalvarCustoGeralPort {

        private CustoGeral custoSalvo;

        @Override
        public void salvarCustoGeral(CustoGeral custo) {
            this.custoSalvo = custo;
        }
    }
}
