package br.com.atendepro.modules.pagamento.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
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
import br.com.atendepro.modules.pagamento.application.command.RegistrarWebhookAsaasCommand;
import br.com.atendepro.modules.pagamento.application.port.out.AtualizarCobrancaPagamentoPort;
import br.com.atendepro.modules.pagamento.application.port.out.AtualizarPagamentoAssinaturaPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarCobrancaPagamentoPorReferenciaExternaPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarEventoPagamentoGatewayPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarPagamentoAssinaturaPorAssinaturaExternaPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarCobrancaPagamentoPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarEventoPagamentoGatewayPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarPagamentoAssinaturaPort;
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
import br.com.atendepro.shared.domain.exception.BusinessException;

class PagamentoServiceTest {

    private static final UUID EMPRESA_ID = UUID.fromString("4156989d-3290-4a4d-a83b-3a72bdf008c3");
    private static final UUID PLANO_ID = UUID.fromString("ed7d5385-bd83-4d0e-82a4-f018c929ed7d");
    private static final UUID ASSINATURA_ID = UUID.fromString("11111111-1111-4111-8111-111111111111");
    private static final Instant AGORA = Instant.parse("2026-06-13T10:00:00Z");

    private final FakePagamentoAdapter pagamentoAdapter = new FakePagamentoAdapter();
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

    private PagamentoService criarService(String ambiente, String webhookToken) {
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
            CarregarCobrancaPagamentoPorReferenciaExternaPort {

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
    }
}
