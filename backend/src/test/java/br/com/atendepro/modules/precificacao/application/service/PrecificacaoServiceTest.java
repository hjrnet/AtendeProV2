package br.com.atendepro.modules.precificacao.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
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
import br.com.atendepro.modules.precificacao.application.command.CalcularCustoRealCommand;
import br.com.atendepro.modules.precificacao.application.command.CalcularMargemLucroCommand;
import br.com.atendepro.modules.precificacao.application.command.CalcularPrecoMinimoCommand;
import br.com.atendepro.modules.precificacao.application.command.CalcularPrecoRecomendadoCommand;
import br.com.atendepro.modules.precificacao.application.command.CalcularPrecificacaoBaseCommand;
import br.com.atendepro.modules.precificacao.application.command.ItemCustoPrecificacaoCommand;
import br.com.atendepro.modules.precificacao.application.command.SalvarSimulacaoPrecificacaoCommand;
import br.com.atendepro.modules.precificacao.application.port.out.CarregarServicoParaPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.port.out.GerarRelatorioPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.port.out.SalvarSimulacaoPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.result.RelatorioPrecificacaoResult;
import br.com.atendepro.modules.precificacao.application.result.ServicoPrecificacaoResult;
import br.com.atendepro.modules.precificacao.domain.model.AnaliseMargemLucroPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.CategoriaItemPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.CustoRealPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.PrecoMinimoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.PrecoRecomendadoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class PrecificacaoServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("7b869806-f196-488c-bc11-937c4f5b2f38");
    private static final UUID SERVICO_ID = UUID.fromString("e59739a0-7a21-4720-8ac0-65ba4a2bb548");
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-05-25T00:00:00Z"), ZoneOffset.UTC);

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void deveCalcularPrecificacaoBaseNaEmpresaDoContexto() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> {
            assertThat(empresaId).isEqualTo(EMPRESA_ID);
            assertThat(servicoProcedimentoId).isEqualTo(SERVICO_ID);
            return Optional.of(new ServicoPrecificacaoResult(
                    SERVICO_ID,
                    EMPRESA_ID,
                    "Consulta Nutricional",
                    60,
                    new BigDecimal("250.00")
            ));
        });

        var result = service.calcularPrecificacaoBase(command(null, SERVICO_ID, null));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.nomeProcedimento()).isEqualTo("Consulta Nutricional");
        assertThat(result.precoBaseServico()).isEqualByComparingTo("250.00");
        assertThat(result.custoTotal()).isEqualByComparingTo("90.00");
        assertThat(result.calculadoEm()).isEqualTo(Instant.parse("2026-05-25T00:00:00Z"));
    }

    @Test
    void deveExigirEmpresaParaUsuarioGlobal() {
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        assertThatThrownBy(() -> service.calcularPrecificacaoBase(command(null, null, "Consulta")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Empresa e obrigatoria para operar precificacao.");
    }

    @Test
    void naoDeveOperarSemPermissaoDePrecificacao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.CLIENTE)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        assertThatThrownBy(() -> service.calcularPrecificacaoBase(command(null, null, "Consulta")))
                .isInstanceOf(PermissaoNegadaException.class)
                .hasMessage("Usuario nao possui permissao para executar esta acao.");
    }

    @Test
    void naoDeveCalcularComServicoInexistente() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        assertThatThrownBy(() -> service.calcularPrecificacaoBase(command(null, SERVICO_ID, null)))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Servico ou procedimento nao encontrado para precificacao.");
    }

    @Test
    void deveCalcularCustoRealUsandoDuracaoDoServico() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.PROFISSIONAL)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.of(new ServicoPrecificacaoResult(
                SERVICO_ID,
                EMPRESA_ID,
                "Consulta Nutricional",
                90,
                new BigDecimal("350.00")
        )));

        var result = service.calcularCustoReal(new CalcularCustoRealCommand(
                null,
                SERVICO_ID,
                null,
                null,
                new BigDecimal("45.00"),
                new BigDecimal("60.00"),
                new BigDecimal("120.00"),
                new BigDecimal("25.00"),
                new BigDecimal("15.00"),
                new BigDecimal("10.00")
        ));

        assertThat(result.nomeProcedimento()).isEqualTo("Consulta Nutricional");
        assertThat(result.duracaoMinutos()).isEqualTo(90);
        assertThat(result.custoSala()).isEqualByComparingTo("90.00");
        assertThat(result.custoTempoProfissional()).isEqualByComparingTo("180.00");
        assertThat(result.custoTotal()).isEqualByComparingTo("365.00");
    }

    @Test
    void deveExigirDuracaoParaCustoRealSemServico() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        assertThatThrownBy(() -> service.calcularCustoReal(new CalcularCustoRealCommand(
                null,
                null,
                "Consulta",
                null,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        ))).isInstanceOf(BusinessException.class)
                .hasMessage("Duracao do procedimento e obrigatoria para calcular custo real.");
    }

    @Test
    void deveCalcularPrecoMinimoIgualAoCustoTotal() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        var result = service.calcularPrecoMinimo(new CalcularPrecoMinimoCommand(
                null,
                null,
                "Procedimento",
                60,
                new BigDecimal("30.00"),
                new BigDecimal("40.00"),
                new BigDecimal("80.00"),
                new BigDecimal("10.00"),
                new BigDecimal("5.00"),
                new BigDecimal("3.00")
        ));

        assertThat(result.custoTotal()).isEqualByComparingTo("168.00");
        assertThat(result.precoMinimo()).isEqualByComparingTo("168.00");
    }

    @Test
    void deveCalcularPrecoRecomendadoPorMargemDesejada() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        var result = service.calcularPrecoRecomendado(new CalcularPrecoRecomendadoCommand(
                null,
                null,
                "Procedimento",
                60,
                new BigDecimal("168.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("30.00")
        ));

        assertThat(result.custoTotal()).isEqualByComparingTo("168.00");
        assertThat(result.precoMinimo()).isEqualByComparingTo("168.00");
        assertThat(result.precoRecomendado()).isEqualByComparingTo("240.00");
        assertThat(result.margemDesejadaPercentual()).isEqualByComparingTo("30.00");
    }

    @Test
    void deveCalcularMargemLucroEAlertas() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty());

        var result = service.calcularMargemLucro(new CalcularMargemLucroCommand(
                null,
                null,
                "Procedimento",
                60,
                new BigDecimal("168.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("200.00")
        ));

        assertThat(result.lucroEstimado()).isEqualByComparingTo("32.00");
        assertThat(result.margemRealPercentual()).isEqualByComparingTo("16.00");
        assertThat(result.status()).isEqualTo(StatusMargemPrecificacao.MARGEM_BAIXA);
        assertThat(result.alertas()).extracting("codigo").containsExactly("MARGEM_BAIXA");
    }

    @Test
    void deveSalvarSimulacaoPrecificacao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        SalvarSimulacaoFake salvarFake = new SalvarSimulacaoFake();
        PrecificacaoService service = service((empresaId, servicoProcedimentoId) -> Optional.empty(), salvarFake);

        var result = service.salvarSimulacao(simulacaoCommand("Simulacao inicial", new BigDecimal("240.00")));

        assertThat(result.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(result.nomeProcedimento()).isEqualTo("Simulacao inicial");
        assertThat(result.precoRecomendado()).isEqualByComparingTo("240.00");
        assertThat(salvarFake.simulacaoSalva.id()).isEqualTo(result.id());
    }

    @Test
    void deveAtualizarSimulacaoPrecificacaoExistente() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        UUID simulacaoId = UUID.fromString("96df0c0f-0d09-432f-9b51-805d1f9698d2");
        SimulacaoPrecificacao simulacaoAtual = simulacao(simulacaoId, "Simulacao atual");
        AtualizarSimulacaoFake atualizarFake = new AtualizarSimulacaoFake();
        PrecificacaoService service = new PrecificacaoService(
                (empresaId, servicoProcedimentoId) -> Optional.empty(),
                simulacao -> {
                },
                atualizarFake::atualizarSimulacao,
                id -> Optional.of(simulacaoAtual),
                (empresaId, paginacao, busca) -> new ResultadoPaginado<>(List.of(simulacaoAtual), 1, paginacao.pagina(), paginacao.tamanho()),
                simulacao -> new RelatorioPrecificacaoResult("precificacao.pdf", "application/pdf", "%PDF".getBytes()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.atualizarSimulacao(simulacaoId, simulacaoCommand("Simulacao editada", new BigDecimal("250.00")));

        assertThat(result.id()).isEqualTo(simulacaoId);
        assertThat(result.nomeProcedimento()).isEqualTo("Simulacao editada");
        assertThat(result.precoVenda()).isEqualByComparingTo("250.00");
        assertThat(atualizarFake.simulacaoAtualizada.id()).isEqualTo(simulacaoId);
    }

    @Test
    void deveGerarRelatorioPrecificacaoDaSimulacao() {
        TenantContextHolder.definir(new TenantContext(EMPRESA_ID, UUID.randomUUID(), Set.of(PerfilAcesso.EMPRESA_ADMIN)));
        UUID simulacaoId = UUID.fromString("5ec445de-0947-4c97-99f3-03ff7bb368af");
        SimulacaoPrecificacao simulacaoAtual = simulacao(simulacaoId, "Simulacao relatorio");
        GerarRelatorioFake gerarRelatorioFake = new GerarRelatorioFake();
        PrecificacaoService service = new PrecificacaoService(
                (empresaId, servicoProcedimentoId) -> Optional.empty(),
                simulacao -> {
                },
                simulacao -> {
                },
                id -> Optional.of(simulacaoAtual),
                (empresaId, paginacao, busca) -> new ResultadoPaginado<>(List.of(simulacaoAtual), 1, paginacao.pagina(), paginacao.tamanho()),
                gerarRelatorioFake,
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );

        var result = service.gerarRelatorio(simulacaoId);

        assertThat(result.nomeArquivo()).isEqualTo("precificacao.pdf");
        assertThat(result.contentType()).isEqualTo("application/pdf");
        assertThat(result.conteudo()).startsWith("%PDF".getBytes());
        assertThat(gerarRelatorioFake.simulacaoRecebida.id()).isEqualTo(simulacaoId);
    }

    private PrecificacaoService service(CarregarServicoParaPrecificacaoPort carregarPort) {
        return service(carregarPort, simulacao -> {
        });
    }

    private PrecificacaoService service(
            CarregarServicoParaPrecificacaoPort carregarPort,
            SalvarSimulacaoPrecificacaoPort salvarPort
    ) {
        return new PrecificacaoService(
                carregarPort,
                salvarPort,
                simulacao -> {
                },
                id -> Optional.empty(),
                (empresaId, paginacao, busca) -> new ResultadoPaginado<>(List.of(), 0, paginacao.pagina(), paginacao.tamanho()),
                simulacao -> new RelatorioPrecificacaoResult("precificacao.pdf", "application/pdf", "%PDF".getBytes()),
                new TenantAccessService(),
                new PermissaoAcessoService(),
                CLOCK
        );
    }

    private CalcularPrecificacaoBaseCommand command(UUID empresaId, UUID servicoId, String nomeProcedimento) {
        return new CalcularPrecificacaoBaseCommand(
                empresaId,
                servicoId,
                nomeProcedimento,
                List.of(
                        new ItemCustoPrecificacaoCommand(
                                "Insumos",
                                CategoriaItemPrecificacao.INSUMO,
                                new BigDecimal("40.00")
                        ),
                        new ItemCustoPrecificacaoCommand(
                                "Sala",
                                CategoriaItemPrecificacao.SALA,
                                new BigDecimal("50.00")
                        )
                )
        );
    }

    private SalvarSimulacaoPrecificacaoCommand simulacaoCommand(String nomeProcedimento, BigDecimal precoVenda) {
        return new SalvarSimulacaoPrecificacaoCommand(
                null,
                null,
                nomeProcedimento,
                60,
                new BigDecimal("168.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("30.00"),
                precoVenda
        );
    }

    private SimulacaoPrecificacao simulacao(UUID id, String nomeProcedimento) {
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                EMPRESA_ID,
                null,
                nomeProcedimento,
                60,
                new BigDecimal("168.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.parse("2026-05-25T00:00:00Z")
        );
        PrecoMinimoPrecificacao precoMinimo = PrecoMinimoPrecificacao.calcular(custoReal);
        PrecoRecomendadoPrecificacao precoRecomendado = PrecoRecomendadoPrecificacao.calcular(
                precoMinimo,
                new BigDecimal("30.00")
        );
        AnaliseMargemLucroPrecificacao analise = AnaliseMargemLucroPrecificacao.analisar(
                precoMinimo,
                new BigDecimal("240.00")
        );
        return new SimulacaoPrecificacao(
                id,
                EMPRESA_ID,
                null,
                nomeProcedimento,
                60,
                new BigDecimal("168.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("30.00"),
                new BigDecimal("240.00"),
                custoReal.custoTotal(),
                precoMinimo.precoMinimo(),
                precoRecomendado.precoRecomendado(),
                analise.lucroEstimado(),
                analise.margemRealPercentual(),
                analise.status(),
                true,
                Instant.parse("2026-05-25T00:00:00Z"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }

    private static class SalvarSimulacaoFake implements SalvarSimulacaoPrecificacaoPort {

        private SimulacaoPrecificacao simulacaoSalva;

        @Override
        public void salvarSimulacao(SimulacaoPrecificacao simulacao) {
            this.simulacaoSalva = simulacao;
        }
    }

    private static class AtualizarSimulacaoFake {

        private SimulacaoPrecificacao simulacaoAtualizada;

        private void atualizarSimulacao(SimulacaoPrecificacao simulacao) {
            this.simulacaoAtualizada = simulacao;
        }
    }

    private static class GerarRelatorioFake implements GerarRelatorioPrecificacaoPort {

        private SimulacaoPrecificacao simulacaoRecebida;

        @Override
        public RelatorioPrecificacaoResult gerarRelatorio(SimulacaoPrecificacao simulacao) {
            this.simulacaoRecebida = simulacao;
            return new RelatorioPrecificacaoResult("precificacao.pdf", "application/pdf", "%PDF".getBytes());
        }
    }
}
