package br.com.atendepro.modules.custo.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.custo.application.command.CadastrarCustoAlimentacaoTransporteCommand;
import br.com.atendepro.modules.custo.application.port.out.SalvarCustoAlimentacaoTransportePort;
import br.com.atendepro.modules.custo.domain.model.CustoAlimentacaoTransporte;
import br.com.atendepro.modules.custo.domain.model.PeriodicidadeCustoPessoal;
import br.com.atendepro.modules.custo.domain.model.TipoCustoPessoal;
import br.com.atendepro.modules.empresa.application.context.TenantAccessService;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;

class CustoAlimentacaoTransporteServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("d6ff9cb4-8478-47ad-97f8-a45ea1047baf");
    private static final UUID PROFISSIONAL_ID = UUID.fromString("4b8d2b0d-f226-42b3-a645-b91e36e5b77b");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCadastrarCustoPessoalNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        SalvarCustoPessoalFake salvarCustoPessoalFake = new SalvarCustoPessoalFake();
        CustoAlimentacaoTransporteService service = service(salvarCustoPessoalFake);

        var result = service.cadastrarCustoAlimentacaoTransporte(command(null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.tipo()).isEqualTo(TipoCustoPessoal.TRANSPORTE);
        assertThat(salvarCustoPessoalFake.custoSalvo.valor()).isEqualByComparingTo("40.00");
    }

    @Test
    void deveListarCustosPessoaisPorTipoEProfissional() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        CustoAlimentacaoTransporte custo = custo();
        CustoAlimentacaoTransporteService service = new CustoAlimentacaoTransporteService(
                custoSalvo -> {
                },
                (empresaId, paginacao, tipo, profissionalId, ativo) -> {
                    assertThat(empresaId).isEqualTo(EMPRESA_ID);
                    assertThat(tipo).isEqualTo(TipoCustoPessoal.TRANSPORTE);
                    assertThat(profissionalId).isEqualTo(PROFISSIONAL_ID);
                    return new ResultadoPaginado<>(List.of(custo), 1, paginacao.pagina(), paginacao.tamanho());
                },
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.listarCustosAlimentacaoTransporte(null, new Paginacao(0, 20), TipoCustoPessoal.TRANSPORTE, PROFISSIONAL_ID, true);

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).extracting("descricao").containsExactly("Deslocamento urbano");
    }

    private CustoAlimentacaoTransporteService service(SalvarCustoAlimentacaoTransportePort salvarPort) {
        return new CustoAlimentacaoTransporteService(
                salvarPort,
                (empresaId, paginacao, tipo, profissionalId, ativo) -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CadastrarCustoAlimentacaoTransporteCommand command(UUID empresaId) {
        return new CadastrarCustoAlimentacaoTransporteCommand(
                empresaId,
                PROFISSIONAL_ID,
                "Deslocamento urbano",
                TipoCustoPessoal.TRANSPORTE,
                PeriodicidadeCustoPessoal.POR_ATENDIMENTO,
                new BigDecimal("40.00")
        );
    }

    private CustoAlimentacaoTransporte custo() {
        return CustoAlimentacaoTransporte.cadastrar(
                EMPRESA_ID,
                PROFISSIONAL_ID,
                "Deslocamento urbano",
                TipoCustoPessoal.TRANSPORTE,
                PeriodicidadeCustoPessoal.POR_ATENDIMENTO,
                new BigDecimal("40.00"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarCustoPessoalFake implements SalvarCustoAlimentacaoTransportePort {

        private CustoAlimentacaoTransporte custoSalvo;

        @Override
        public void salvarCustoAlimentacaoTransporte(CustoAlimentacaoTransporte custo) {
            this.custoSalvo = custo;
        }
    }
}
