package br.com.atendepro.modules.pagamento.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.adminsaas.application.command.RegistrarEventoAuditoriaAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.application.port.out.CarregarEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.RegistrarEventoAuditoriaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.result.EmpresaAdminSaasDetalheResult;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarAssinaturaAtivaPorEmpresaPort;
import br.com.atendepro.modules.assinatura.domain.model.AssinaturaSaas;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PerfilAcesso;
import br.com.atendepro.modules.empresa.application.context.TenantContext;
import br.com.atendepro.modules.empresa.application.context.TenantContextHolder;
import br.com.atendepro.modules.pagamento.application.command.PrepararCheckoutPagamentoCommand;
import br.com.atendepro.modules.pagamento.application.command.ReconciliarDivergenciasPagamentosSandboxCommand;
import br.com.atendepro.modules.pagamento.application.command.RegistrarWebhookAsaasCommand;
import br.com.atendepro.modules.pagamento.application.port.out.AtualizarCobrancaPagamentoPort;
import br.com.atendepro.modules.pagamento.application.port.out.AtualizarPagamentoAssinaturaPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarCobrancaPagamentoPorReferenciaExternaPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarEventoPagamentoGatewayPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarPagamentoAssinaturaPorAssinaturaExternaPort;
import br.com.atendepro.modules.pagamento.application.port.out.ListarPagamentosSandboxPort;
import br.com.atendepro.modules.pagamento.application.port.out.ObterObservabilidadePagamentosSandboxPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarCobrancaPagamentoPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarEventoPagamentoGatewayPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarPagamentoAssinaturaPort;
import br.com.atendepro.modules.pagamento.application.result.ReconciliacaoDivergenciasPagamentosSandboxResult;
import br.com.atendepro.modules.pagamento.application.result.ObservabilidadePagamentosSandboxIndicadorResult;
import br.com.atendepro.modules.pagamento.application.result.ObservabilidadePagamentosSandboxDivergenciaResult;
import br.com.atendepro.modules.pagamento.domain.model.CobrancaPagamento;
import br.com.atendepro.modules.pagamento.domain.model.EventoPagamentoGateway;
import br.com.atendepro.modules.pagamento.domain.model.PagamentoAssinatura;
import br.com.atendepro.modules.pagamento.domain.model.ProvedorPagamento;
import br.com.atendepro.modules.pagamento.domain.model.StatusCobrancaPagamento;
import br.com.atendepro.modules.pagamento.domain.model.StatusPagamentoAssinatura;
import br.com.atendepro.modules.pagamento.domain.model.TipoEventoPagamentoGateway;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorIdPort;
import br.com.atendepro.modules.plano.domain.model.ModuloPlano;
import br.com.atendepro.modules.plano.domain.model.PlanoAssinatura;
import br.com.atendepro.shared.application.pagination.Paginacao;
import br.com.atendepro.shared.application.pagination.ResultadoPaginado;
import br.com.atendepro.shared.domain.exception.BusinessException;

class PagamentoServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("4156989d-3290-4a4d-a83b-3a72bdf008c3");
    private static final UUID PLANO_ID = UUID.fromString("ed7d5385-bd83-4d0e-82a4-f018c929ed7d");
    private static final UUID ASSINATURA_ID = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private static final Instant AGORA = Instant.parse("2026-06-13T10:00:00Z");

    private final FakePagamentoAdapter pagamentoAdapter = new FakePagamentoAdapter();
    private final FakeObservabilidadePort observabilidadePort = new FakeObservabilidadePort();
    private final FakeAuditoriaPort auditoriaPort = new FakeAuditoriaPort();

    @AfterEach
    void limparContextoTenant() {
        TenantContextHolder.limpar();
    }

    @Test
    void devePrepararCheckoutSandbox() {
        definirSuperAdmin();
        var service = criarService("sandbox", "segredo");

        var result = service.prepararCheckout(commandCheckout());

        assertThat(result.status()).isEqualTo(StatusPagamentoAssinatura.AGUARDANDO_PAGAMENTO);
        assertThat(result.provedor()).isEqualTo(ProvedorPagamento.ASAAS);
        assertThat(pagamentoAdapter.pagamentos).hasSize(1);
        assertThat(pagamentoAdapter.cobrancas).hasSize(1);
        assertThat(auditoriaPort.eventos).containsKey("PAGAMENTO_CHECKOUT_PREPARADO");
    }

    @Test
    void naoDevePermitirAmbienteProducao() {
        definirSuperAdmin();
        var service = criarService("producao", "segredo");

        assertThatThrownBy(() -> service.prepararCheckout(commandCheckout()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Pagamentos em producao estao bloqueados na R30.");
    }

    @Test
    void deveProcessarWebhookRecebidoComIdempotencia() {
        definirSuperAdmin();
        var service = criarService("sandbox", "segredo");
        service.prepararCheckout(commandCheckout());
        var pagamento = pagamentoAdapter.pagamentos.values().iterator().next();
        var cobranca = pagamentoAdapter.cobrancas.values().iterator().next();

        var command = new RegistrarWebhookAsaasCommand(
                "segredo",
                "PAYMENT_RECEIVED",
                cobranca.cobrancaExternaId(),
                pagamento.assinaturaExternaId(),
                "{\"event\":\"PAYMENT_RECEIVED\"}"
        );
        var primeiro = service.registrarWebhook(command);
        var segundo = service.registrarWebhook(command);

        assertThat(primeiro.processado()).isTrue();
        assertThat(segundo.duplicado()).isTrue();
        assertThat(pagamentoAdapter.pagamentos.get(pagamento.id()).status()).isEqualTo(StatusPagamentoAssinatura.ATIVA);
        assertThat(pagamentoAdapter.cobrancas.get(cobranca.id()).status()).isEqualTo(StatusCobrancaPagamento.RECEBIDO);
    }

    @Test
    void deveListarPagamentosSandbox() {
        definirSuperAdmin();
        var service = criarService("sandbox", "segredo");
        service.prepararCheckout(commandCheckout());

        var result = service.listarPagamentosSandbox(Paginacao.primeiraPagina(20), EMPRESA_ID, null);

        assertThat(result.totalItens()).isEqualTo(1);
        assertThat(result.itens()).first().satisfies(item -> {
            assertThat(item.empresaId()).isEqualTo(EMPRESA_ID);
            assertThat(item.statusAssinatura()).isEqualTo(StatusPagamentoAssinatura.AGUARDANDO_PAGAMENTO.name());
            assertThat(item.statusCobranca()).isEqualTo(StatusCobrancaPagamento.PENDENTE.name());
        });
    }

    @Test
    void deveConsultarObservabilidadePagamentos() {
        definirSuperAdmin();
        var service = criarService("sandbox", "segredo");
        service.prepararCheckout(commandCheckout());

        var result = service.consultarObservabilidadePagamentosSandbox(EMPRESA_ID, null, null, null, null);

        assertThat(result.indicadores().totalCheckoutsPreparados()).isEqualTo(1);
    }

    @Test
    void deveExportarObservabilidadePagamentosSandboxCsv() {
        definirSuperAdmin();
        var service = criarService("sandbox", "segredo");
        service.prepararCheckout(commandCheckout());

        byte[] conteudoCsv = service.exportarObservabilidadePagamentosSandboxCsv(
                EMPRESA_ID,
                null,
                null,
                null,
                null
        );

        var conteudo = new String(conteudoCsv, StandardCharsets.UTF_8);

        assertThat(conteudo).contains("metrica,valor");
        assertThat(conteudo).contains("total_checkouts_preparados,1");
        assertThat(conteudo).contains("total_divergencias,1");
        assertThat(conteudo).contains("pagamento_assinatura_id,empresa_id,plano_id,assinatura_interna_id");
        assertThat(conteudo).contains("ASSINATURA_SEM_COBRANCA");
    }

    @Test
    void deveExecutarReconciliacaoEmLoteDasDivergencias() {
        definirSuperAdmin();
        var preparador = criarService("sandbox", "segredo");
        preparador.prepararCheckout(commandCheckout());
        var pagamento = pagamentoAdapter.pagamentos.values().iterator().next();
        var cobranca = pagamentoAdapter.cobrancas.values().iterator().next();
        var divergencias = List.of(
                new ObservabilidadePagamentosSandboxDivergenciaResult(
                        pagamento.id(),
                        EMPRESA_ID,
                        PLANO_ID,
                        ASSINATURA_ID,
                        "COBRANCA_RECEBIDA_SEM_WEBHOOK",
                        "ALTA",
                        "Cobrança recebida sem webhook registrado.",
                        pagamento.status().name(),
                        pagamentoAdapter.cobrancas.get(cobranca.id()).status().name(),
                        pagamento.assinaturaExternaId(),
                        cobranca.cobrancaExternaId(),
                        "CHECKOUT_PREPARADO",
                        true,
                        AGORA,
                        AGORA
                ),
                new ObservabilidadePagamentosSandboxDivergenciaResult(
                        pagamento.id(),
                        EMPRESA_ID,
                        PLANO_ID,
                        ASSINATURA_ID,
                        "COBRANCA_RECEBIDA_SEM_WEBHOOK",
                        "ALTA",
                        "Duplicata de webhook recebido sem processamento prévio.",
                        pagamento.status().name(),
                        pagamentoAdapter.cobrancas.get(cobranca.id()).status().name(),
                        pagamento.assinaturaExternaId(),
                        cobranca.cobrancaExternaId(),
                        "CHECKOUT_PREPARADO",
                        true,
                        AGORA,
                        AGORA
                ),
                new ObservabilidadePagamentosSandboxDivergenciaResult(
                        pagamento.id(),
                        EMPRESA_ID,
                        PLANO_ID,
                        ASSINATURA_ID,
                        "ASSINATURA_SEM_COBRANCA",
                        "MEDIA",
                        "Assinatura sem cobranca para conciliar.",
                        pagamento.status().name(),
                        null,
                        pagamento.assinaturaExternaId(),
                        cobranca.cobrancaExternaId(),
                        "CHECKOUT_PREPARADO",
                        true,
                        AGORA,
                        AGORA
                ),
                new ObservabilidadePagamentosSandboxDivergenciaResult(
                        pagamento.id(),
                        EMPRESA_ID,
                        PLANO_ID,
                        ASSINATURA_ID,
                        "ASSINATURA_CANCELADA_COM_EVENTO_ATIVO",
                        "ALTA",
                        "Sem identificador externo para validação.",
                        pagamento.status().name(),
                        pagamentoAdapter.cobrancas.get(cobranca.id()).status().name(),
                        null,
                        null,
                        "CHECKOUT_PREPARADO",
                        null,
                        AGORA,
                        AGORA
                )
        );
        var service = criarService("sandbox", "segredo", new FakeObservabilidadePort(divergencias));

        ReconciliacaoDivergenciasPagamentosSandboxResult result = service.reconciliarDivergenciasPagamentosSandbox(
                new ReconciliarDivergenciasPagamentosSandboxCommand(
                        "segredo",
                        EMPRESA_ID,
                        null,
                        null,
                        null,
                        null
                )
        );

        assertThat(result.totalEncontradas()).isEqualTo(4);
        assertThat(result.totalProcessadas()).isEqualTo(1);
        assertThat(result.totalDuplicadas()).isEqualTo(1);
        assertThat(result.totalIgnoradas()).isEqualTo(2);
        assertThat(result.totalFalhas()).isEqualTo(0);
        assertThat(result.itens()).hasSize(4);
    }

    private PagamentoService criarService(String ambiente, String webhookToken) {
        return criarService(ambiente, webhookToken, observabilidadePort);
    }

    private PagamentoService criarService(String ambiente, String webhookToken, ObterObservabilidadePagamentosSandboxPort observabilidadePort) {
        return new PagamentoService(
                new PermissaoAcessoService(),
                new FakeEmpresaPort(),
                new FakePlanoPort(),
                new FakeAssinaturaPort(),
                pagamentoAdapter,
                pagamentoAdapter,
                pagamentoAdapter,
                pagamentoAdapter,
                pagamentoAdapter,
                pagamentoAdapter,
                pagamentoAdapter,
                pagamentoAdapter,
                pagamentoAdapter,
                observabilidadePort,
                auditoriaPort,
                new PagamentosProperties(false, "asaas", ambiente, "https://sandbox.asaas.com/api/v3", "", webhookToken),
                Clock.fixed(AGORA, ZoneOffset.UTC)
        );
    }

    private PrepararCheckoutPagamentoCommand commandCheckout() {
        return new PrepararCheckoutPagamentoCommand(
                EMPRESA_ID,
                PLANO_ID,
                "admin@clinica.local",
                "Admin Clinica",
                "12345678000190",
                "11999990000",
                "PIX"
        );
    }

    private void definirSuperAdmin() {
        TenantContextHolder.definir(new TenantContext(UUID.randomUUID(), UUID.randomUUID(), Set.of(PerfilAcesso.SUPER_ADMIN)));
    }

    private static class FakeEmpresaPort implements CarregarEmpresaAdminSaasPort {

        @Override
        public Optional<EmpresaAdminSaasDetalheResult> carregarEmpresa(UUID empresaId) {
            return Optional.of(new EmpresaAdminSaasDetalheResult(
                    EMPRESA_ID,
                    "Clinica Modelo",
                    "Clinica Modelo LTDA",
                    "12345678000190",
                    "contato@clinica.local",
                    "11999990000",
                    true,
                    AGORA
            ));
        }
    }

    private static class FakePlanoPort implements CarregarPlanoPorIdPort {

        @Override
        public Optional<PlanoAssinatura> carregarPlanoPorId(UUID planoId) {
            return Optional.of(new PlanoAssinatura(
                    PLANO_ID,
                    "START",
                    "Start",
                    "Plano Start",
                    new BigDecimal("79.90"),
                    2,
                    100,
                    1,
                    true,
                    false,
                    null,
                    Set.of(ModuloPlano.CLIENTES, ModuloPlano.AGENDA),
                    AGORA,
                    AGORA
            ));
        }
    }

    private static class FakeAssinaturaPort implements CarregarAssinaturaAtivaPorEmpresaPort {

        @Override
        public Optional<AssinaturaSaas> carregarAssinaturaAtivaPorEmpresa(UUID empresaId) {
            return Optional.of(new AssinaturaSaas(
                    ASSINATURA_ID,
                    EMPRESA_ID,
                    PLANO_ID,
                    br.com.atendepro.modules.assinatura.domain.model.AssinaturaStatus.ATIVA,
                    AGORA,
                    null,
                    null,
                    AGORA,
                    AGORA
            ));
        }
    }

    private static class FakeAuditoriaPort implements RegistrarEventoAuditoriaAdminSaasPort {

        private final Map<String, RegistrarEventoAuditoriaAdminSaasCommand> eventos = new LinkedHashMap<>();

        @Override
        public void registrarEvento(RegistrarEventoAuditoriaAdminSaasCommand command) {
            eventos.put(command.tipo(), command);
        }
    }

    private static class FakePagamentoAdapter implements
            SalvarPagamentoAssinaturaPort,
            AtualizarPagamentoAssinaturaPort,
            SalvarCobrancaPagamentoPort,
            AtualizarCobrancaPagamentoPort,
            SalvarEventoPagamentoGatewayPort,
            CarregarEventoPagamentoGatewayPort,
            CarregarPagamentoAssinaturaPorAssinaturaExternaPort,
            CarregarCobrancaPagamentoPorReferenciaExternaPort,
            ListarPagamentosSandboxPort {

        private final Map<UUID, PagamentoAssinatura> pagamentos = new LinkedHashMap<>();
        private final Map<UUID, CobrancaPagamento> cobrancas = new LinkedHashMap<>();
        private final Map<String, EventoPagamentoGateway> eventos = new LinkedHashMap<>();

        @Override
        public void salvarPagamentoAssinatura(PagamentoAssinatura pagamento) {
            pagamentos.put(pagamento.id(), pagamento);
        }

        @Override
        public void atualizarPagamentoAssinatura(PagamentoAssinatura pagamento) {
            pagamentos.put(pagamento.id(), pagamento);
        }

        @Override
        public void salvarCobrancaPagamento(CobrancaPagamento cobranca) {
            cobrancas.put(cobranca.id(), cobranca);
        }

        @Override
        public void atualizarCobrancaPagamento(CobrancaPagamento cobranca) {
            cobrancas.put(cobranca.id(), cobranca);
        }

        @Override
        public void salvarEventoPagamentoGateway(EventoPagamentoGateway evento) {
            eventos.put(chave(evento.provedor(), evento.tipo(), evento.eventoExternoId()), evento);
        }

        @Override
        public Optional<EventoPagamentoGateway> carregarEvento(
                ProvedorPagamento provedor,
                TipoEventoPagamentoGateway tipo,
                String eventoExternoId
        ) {
            return Optional.ofNullable(eventos.get(chave(provedor, tipo, eventoExternoId)));
        }

        @Override
        public Optional<PagamentoAssinatura> carregarPorAssinaturaExterna(String assinaturaExternaId) {
            return pagamentos.values().stream()
                    .filter(pagamento -> pagamento.assinaturaExternaId().equals(assinaturaExternaId))
                    .findFirst();
        }

        @Override
        public Optional<CobrancaPagamento> carregarPorCobrancaExterna(String cobrancaExternaId) {
            return cobrancas.values().stream()
                    .filter(cobranca -> cobranca.cobrancaExternaId().equals(cobrancaExternaId))
                    .findFirst();
        }

        private String chave(ProvedorPagamento provedor, TipoEventoPagamentoGateway tipo, String eventoExternoId) {
            return provedor + ":" + tipo + ":" + eventoExternoId;
        }

        @Override
        public ResultadoPaginado<br.com.atendepro.modules.pagamento.application.result.PagamentoSandboxResumoResult> listarPagamentosSandbox(
                Paginacao paginacao,
                UUID empresaId,
                String status
        ) {
            var itens = pagamentos.values().stream()
                    .filter(pagamento -> empresaId == null || pagamento.empresaId().equals(empresaId))
                    .filter(pagamento -> status == null || status.isBlank() || pagamento.status().name().equals(status))
                    .map(pagamento -> {
                        var cobranca = cobrancas.values().stream()
                                .filter(item -> item.pagamentoAssinaturaId().equals(pagamento.id()))
                                .findFirst()
                                .orElse(null);
                        return new br.com.atendepro.modules.pagamento.application.result.PagamentoSandboxResumoResult(
                                pagamento.id(),
                                pagamento.empresaId(),
                                pagamento.planoId(),
                                pagamento.assinaturaInternaId(),
                                pagamento.provedor().name(),
                                pagamento.ambiente().name(),
                                pagamento.status().name(),
                                pagamento.clienteExternoId(),
                                pagamento.assinaturaExternaId(),
                                pagamento.checkoutExternoId(),
                                cobranca == null ? null : cobranca.id(),
                                cobranca == null ? null : cobranca.cobrancaExternaId(),
                                cobranca == null ? null : cobranca.status().name(),
                                cobranca == null ? null : cobranca.valor(),
                                cobranca == null ? null : cobranca.vencimento(),
                                cobranca == null ? null : cobranca.formaPagamento(),
                                null,
                                null,
                                false,
                                null,
                                pagamento.criadoEm(),
                                pagamento.atualizadoEm()
                        );
                    })
                    .toList();
            return new ResultadoPaginado<>(itens, itens.size(), paginacao.pagina(), paginacao.tamanho());
        }
    }

    private static class FakeObservabilidadePort implements ObterObservabilidadePagamentosSandboxPort {
        private final List<ObservabilidadePagamentosSandboxDivergenciaResult> divergencias;

        private FakeObservabilidadePort() {
            this(List.of(new ObservabilidadePagamentosSandboxDivergenciaResult(
                    UUID.fromString("11111111-1111-4111-8111-111111111111"),
                    EMPRESA_ID,
                    PLANO_ID,
                    ASSINATURA_ID,
                    "ASSINATURA_SEM_COBRANCA",
                    "ALTA",
                    "Teste",
                    "AGUARDANDO_PAGAMENTO",
                    null,
                    "assinatura-test",
                    "cobranca-test",
                    "CHECKOUT_PREPARADO",
                    true,
                    AGORA,
                    AGORA
            )));
        }

        private FakeObservabilidadePort(List<ObservabilidadePagamentosSandboxDivergenciaResult> divergencias) {
            this.divergencias = divergencias;
        }

        @Override
        public br.com.atendepro.modules.pagamento.application.result.PagamentosSandboxObservabilidadeResult consultarObservabilidadePagamentosSandbox(
                UUID empresaId,
                String statusAssinatura,
                String eventoTipo,
                String tipoDivergencia,
                String severidade
        ) {
            return new br.com.atendepro.modules.pagamento.application.result.PagamentosSandboxObservabilidadeResult(
                    new ObservabilidadePagamentosSandboxIndicadorResult(
                            1L,
                            1L,
                            0L,
                            0L,
                            0L,
                            0L,
                            0L,
                            0L,
                            divergencias.size()
                    ),
                    divergencias
            );
        }
    }
}
