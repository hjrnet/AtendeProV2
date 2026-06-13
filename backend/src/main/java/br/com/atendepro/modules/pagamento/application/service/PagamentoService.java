package br.com.atendepro.modules.pagamento.application.service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import br.com.atendepro.modules.adminsaas.application.command.RegistrarEventoAuditoriaAdminSaasCommand;
import br.com.atendepro.modules.adminsaas.application.port.out.CarregarEmpresaAdminSaasPort;
import br.com.atendepro.modules.adminsaas.application.port.out.RegistrarEventoAuditoriaAdminSaasPort;
import br.com.atendepro.modules.assinatura.application.port.out.CarregarAssinaturaAtivaPorEmpresaPort;
import br.com.atendepro.modules.auth.application.permission.PermissaoAcessoService;
import br.com.atendepro.modules.auth.domain.model.PermissaoAcesso;
import br.com.atendepro.modules.pagamento.application.command.PrepararCheckoutPagamentoCommand;
import br.com.atendepro.modules.pagamento.application.command.RegistrarWebhookAsaasCommand;
import br.com.atendepro.modules.pagamento.application.port.in.PrepararCheckoutPagamentoUseCase;
import br.com.atendepro.modules.pagamento.application.port.in.RegistrarWebhookAsaasUseCase;
import br.com.atendepro.modules.pagamento.application.port.out.AtualizarCobrancaPagamentoPort;
import br.com.atendepro.modules.pagamento.application.port.out.AtualizarPagamentoAssinaturaPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarCobrancaPagamentoPorReferenciaExternaPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarEventoPagamentoGatewayPort;
import br.com.atendepro.modules.pagamento.application.port.out.CarregarPagamentoAssinaturaPorAssinaturaExternaPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarCobrancaPagamentoPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarEventoPagamentoGatewayPort;
import br.com.atendepro.modules.pagamento.application.port.out.SalvarPagamentoAssinaturaPort;
import br.com.atendepro.modules.pagamento.application.result.CheckoutPagamentoResult;
import br.com.atendepro.modules.pagamento.application.result.WebhookPagamentoResult;
import br.com.atendepro.modules.pagamento.domain.model.AmbientePagamento;
import br.com.atendepro.modules.pagamento.domain.model.CobrancaPagamento;
import br.com.atendepro.modules.pagamento.domain.model.EventoPagamentoGateway;
import br.com.atendepro.modules.pagamento.domain.model.PagamentoAssinatura;
import br.com.atendepro.modules.pagamento.domain.model.ProvedorPagamento;
import br.com.atendepro.modules.pagamento.domain.model.StatusCobrancaPagamento;
import br.com.atendepro.modules.pagamento.domain.model.TipoEventoPagamentoGateway;
import br.com.atendepro.modules.plano.application.port.out.CarregarPlanoPorIdPort;
import br.com.atendepro.shared.domain.exception.BusinessException;

@Service
@Profile("!test")
@EnableConfigurationProperties(PagamentosProperties.class)
public class PagamentoService implements PrepararCheckoutPagamentoUseCase, RegistrarWebhookAsaasUseCase {

    private final PermissaoAcessoService permissaoAcessoService;
    private final CarregarEmpresaAdminSaasPort carregarEmpresaAdminSaasPort;
    private final CarregarPlanoPorIdPort carregarPlanoPorIdPort;
    private final CarregarAssinaturaAtivaPorEmpresaPort carregarAssinaturaAtivaPorEmpresaPort;
    private final SalvarPagamentoAssinaturaPort salvarPagamentoAssinaturaPort;
    private final AtualizarPagamentoAssinaturaPort atualizarPagamentoAssinaturaPort;
    private final SalvarCobrancaPagamentoPort salvarCobrancaPagamentoPort;
    private final AtualizarCobrancaPagamentoPort atualizarCobrancaPagamentoPort;
    private final SalvarEventoPagamentoGatewayPort salvarEventoPagamentoGatewayPort;
    private final CarregarEventoPagamentoGatewayPort carregarEventoPagamentoGatewayPort;
    private final CarregarPagamentoAssinaturaPorAssinaturaExternaPort carregarPagamentoPorAssinaturaExternaPort;
    private final CarregarCobrancaPagamentoPorReferenciaExternaPort carregarCobrancaPorReferenciaExternaPort;
    private final RegistrarEventoAuditoriaAdminSaasPort registrarEventoAuditoriaAdminSaasPort;
    private final PagamentosProperties properties;
    private final Clock clock;

    public PagamentoService(
            PermissaoAcessoService permissaoAcessoService,
            CarregarEmpresaAdminSaasPort carregarEmpresaAdminSaasPort,
            CarregarPlanoPorIdPort carregarPlanoPorIdPort,
            CarregarAssinaturaAtivaPorEmpresaPort carregarAssinaturaAtivaPorEmpresaPort,
            SalvarPagamentoAssinaturaPort salvarPagamentoAssinaturaPort,
            AtualizarPagamentoAssinaturaPort atualizarPagamentoAssinaturaPort,
            SalvarCobrancaPagamentoPort salvarCobrancaPagamentoPort,
            AtualizarCobrancaPagamentoPort atualizarCobrancaPagamentoPort,
            SalvarEventoPagamentoGatewayPort salvarEventoPagamentoGatewayPort,
            CarregarEventoPagamentoGatewayPort carregarEventoPagamentoGatewayPort,
            CarregarPagamentoAssinaturaPorAssinaturaExternaPort carregarPagamentoPorAssinaturaExternaPort,
            CarregarCobrancaPagamentoPorReferenciaExternaPort carregarCobrancaPorReferenciaExternaPort,
            RegistrarEventoAuditoriaAdminSaasPort registrarEventoAuditoriaAdminSaasPort,
            PagamentosProperties properties,
            Clock clock
    ) {
        this.permissaoAcessoService = permissaoAcessoService;
        this.carregarEmpresaAdminSaasPort = carregarEmpresaAdminSaasPort;
        this.carregarPlanoPorIdPort = carregarPlanoPorIdPort;
        this.carregarAssinaturaAtivaPorEmpresaPort = carregarAssinaturaAtivaPorEmpresaPort;
        this.salvarPagamentoAssinaturaPort = salvarPagamentoAssinaturaPort;
        this.atualizarPagamentoAssinaturaPort = atualizarPagamentoAssinaturaPort;
        this.salvarCobrancaPagamentoPort = salvarCobrancaPagamentoPort;
        this.atualizarCobrancaPagamentoPort = atualizarCobrancaPagamentoPort;
        this.salvarEventoPagamentoGatewayPort = salvarEventoPagamentoGatewayPort;
        this.carregarEventoPagamentoGatewayPort = carregarEventoPagamentoGatewayPort;
        this.carregarPagamentoPorAssinaturaExternaPort = carregarPagamentoPorAssinaturaExternaPort;
        this.carregarCobrancaPorReferenciaExternaPort = carregarCobrancaPorReferenciaExternaPort;
        this.registrarEventoAuditoriaAdminSaasPort = registrarEventoAuditoriaAdminSaasPort;
        this.properties = properties;
        this.clock = clock;
    }

    @Override
    public CheckoutPagamentoResult prepararCheckout(PrepararCheckoutPagamentoCommand command) {
        validarAcessoAdminSaas();
        validarAmbienteSeguro();
        validarEmpresaPlano(command.empresaId(), command.planoId());

        var assinatura = carregarAssinaturaAtivaPorEmpresaPort.carregarAssinaturaAtivaPorEmpresa(command.empresaId())
                .orElseThrow(() -> new BusinessException(
                        "ASSINATURA_INTERNA_NAO_ENCONTRADA",
                        "Empresa precisa de assinatura interna antes de preparar pagamento sandbox."
                ));
        Instant agora = Instant.now(clock);
        String sufixo = UUID.randomUUID().toString();
        var pagamento = PagamentoAssinatura.prepararSandbox(
                command.empresaId(),
                command.planoId(),
                assinatura.id(),
                "cus_sandbox_" + sufixo,
                "sub_sandbox_" + sufixo,
                "chk_sandbox_" + sufixo,
                agora
        );
        var cobranca = CobrancaPagamento.pendente(
                pagamento.id(),
                "pay_sandbox_" + sufixo,
                BigDecimal.ZERO,
                LocalDate.now(clock).plusDays(7),
                normalizarFormaPagamento(command.formaPagamentoPreferida()),
                agora
        );
        var evento = EventoPagamentoGateway.recebido(
                pagamento.id(),
                AmbientePagamento.SANDBOX,
                TipoEventoPagamentoGateway.CHECKOUT_PREPARADO,
                pagamento.checkoutExternoId(),
                pagamento.assinaturaExternaId(),
                "{\"sandbox\":true}",
                true,
                agora
        );

        salvarPagamentoAssinaturaPort.salvarPagamentoAssinatura(pagamento);
        salvarCobrancaPagamentoPort.salvarCobrancaPagamento(cobranca);
        salvarEventoPagamentoGatewayPort.salvarEventoPagamentoGateway(evento);
        registrarAuditoria(
                "PAGAMENTO_CHECKOUT_PREPARADO",
                "INFO",
                "Checkout de pagamento sandbox preparado.",
                pagamento,
                evento.tipo().name()
        );

        return CheckoutPagamentoResult.de(pagamento, assinatura.id());
    }

    @Override
    public WebhookPagamentoResult registrarWebhook(RegistrarWebhookAsaasCommand command) {
        validarAmbienteSeguro();
        validarWebhookToken(command.token());
        TipoEventoPagamentoGateway tipo = mapearTipo(command.event());
        String eventoExternoId = eventoExternoId(command);

        var eventoExistente = carregarEventoPagamentoGatewayPort.carregarEvento(
                ProvedorPagamento.ASAAS,
                tipo,
                eventoExternoId
        );
        if (eventoExistente.isPresent()) {
            return new WebhookPagamentoResult(eventoExistente.get().id(), tipo, eventoExistente.get().processado(), true, "Evento ja registrado.");
        }

        Instant agora = Instant.now(clock);
        var pagamento = carregarPagamentoPorAssinaturaExternaPort.carregarPorAssinaturaExterna(command.subscriptionId());
        boolean processado = pagamento.isPresent();
        var evento = EventoPagamentoGateway.recebido(
                pagamento.map(PagamentoAssinatura::id).orElse(null),
                AmbientePagamento.SANDBOX,
                tipo,
                eventoExternoId,
                command.paymentId(),
                sanitizarPayload(command.payload()),
                processado,
                agora
        );
        salvarEventoPagamentoGatewayPort.salvarEventoPagamentoGateway(evento);

        pagamento.ifPresent(pagamentoAssinatura -> reconciliarPagamento(tipo, command.paymentId(), pagamentoAssinatura, agora));
        registrarAuditoria(
                processado ? "PAGAMENTO_WEBHOOK_PROCESSADO" : "PAGAMENTO_WEBHOOK_SEM_ASSINATURA",
                processado ? "INFO" : "WARN",
                processado ? "Webhook Asaas sandbox processado." : "Webhook Asaas sandbox registrado sem assinatura interna correspondente.",
                pagamento.orElse(null),
                tipo.name()
        );

        return new WebhookPagamentoResult(evento.id(), tipo, processado, false, processado ? "Webhook processado." : "Webhook registrado sem reconciliacao.");
    }

    private void reconciliarPagamento(
            TipoEventoPagamentoGateway tipo,
            String cobrancaExternaId,
            PagamentoAssinatura pagamento,
            Instant agora
    ) {
        carregarCobrancaPorReferenciaExternaPort.carregarPorCobrancaExterna(cobrancaExternaId)
                .ifPresent(cobranca -> atualizarCobrancaPagamentoPort.atualizarCobrancaPagamento(
                        cobranca.alterarStatus(mapearStatusCobranca(tipo), agora)
                ));

        if (tipo == TipoEventoPagamentoGateway.PAYMENT_RECEIVED) {
            atualizarPagamentoAssinaturaPort.atualizarPagamentoAssinatura(pagamento.ativar(agora));
        } else if (tipo == TipoEventoPagamentoGateway.PAYMENT_OVERDUE) {
            atualizarPagamentoAssinaturaPort.atualizarPagamentoAssinatura(pagamento.marcarFalhaPagamento(agora));
        } else if (tipo == TipoEventoPagamentoGateway.PAYMENT_DELETED || tipo == TipoEventoPagamentoGateway.PAYMENT_REFUNDED) {
            atualizarPagamentoAssinaturaPort.atualizarPagamentoAssinatura(pagamento.cancelar(agora));
        }
    }

    private StatusCobrancaPagamento mapearStatusCobranca(TipoEventoPagamentoGateway tipo) {
        return switch (tipo) {
            case PAYMENT_RECEIVED -> StatusCobrancaPagamento.RECEBIDO;
            case PAYMENT_OVERDUE -> StatusCobrancaPagamento.ATRASADO;
            case PAYMENT_DELETED -> StatusCobrancaPagamento.CANCELADO;
            case PAYMENT_REFUNDED -> StatusCobrancaPagamento.ESTORNADO;
            default -> StatusCobrancaPagamento.PENDENTE;
        };
    }

    private TipoEventoPagamentoGateway mapearTipo(String evento) {
        try {
            return TipoEventoPagamentoGateway.valueOf(evento);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new BusinessException("PAGAMENTO_WEBHOOK_EVENTO_INVALIDO", "Evento de pagamento nao suportado nesta release.");
        }
    }

    private void validarEmpresaPlano(UUID empresaId, UUID planoId) {
        if (carregarEmpresaAdminSaasPort.carregarEmpresa(empresaId).isEmpty()) {
            throw new BusinessException("EMPRESA_NAO_ENCONTRADA", "Empresa nao encontrada para pagamento.");
        }
        if (carregarPlanoPorIdPort.carregarPlanoPorId(planoId).isEmpty()) {
            throw new BusinessException("PLANO_NAO_ENCONTRADO", "Plano nao encontrado para pagamento.");
        }
    }

    private void validarAmbienteSeguro() {
        if (properties.producaoSolicitada()) {
            throw new BusinessException("PAGAMENTOS_PRODUCAO_BLOQUEADA", "Pagamentos em producao estao bloqueados na R30.");
        }
    }

    private void validarWebhookToken(String token) {
        String esperado = properties.asaasWebhookToken();
        if (esperado != null && !esperado.isBlank() && !esperado.equals(token)) {
            throw new BusinessException("PAGAMENTO_WEBHOOK_TOKEN_INVALIDO", "Token de webhook invalido.");
        }
    }

    private String normalizarFormaPagamento(String formaPagamento) {
        return formaPagamento == null || formaPagamento.isBlank() ? "PIX" : formaPagamento.trim().toUpperCase();
    }

    private String eventoExternoId(RegistrarWebhookAsaasCommand command) {
        return command.event() + ":" + command.paymentId();
    }

    private String sanitizarPayload(String payload) {
        if (payload == null || payload.isBlank()) {
            return "{}";
        }
        return payload
                .replaceAll("(?i)\"apiKey\"\\s*:\\s*\"[^\"]+\"", "\"apiKey\":\"***\"")
                .replaceAll("(?i)\"access_token\"\\s*:\\s*\"[^\"]+\"", "\"access_token\":\"***\"");
    }

    private void registrarAuditoria(
            String tipo,
            String severidade,
            String descricao,
            PagamentoAssinatura pagamento,
            String evento
    ) {
        registrarEventoAuditoriaAdminSaasPort.registrarEvento(new RegistrarEventoAuditoriaAdminSaasCommand(
                tipo,
                severidade,
                descricao,
                pagamento == null ? null : pagamento.empresaId(),
                null,
                "PAGAMENTO",
                pagamento == null ? null : pagamento.id(),
                Map.of("evento", evento, "provedor", ProvedorPagamento.ASAAS.name(), "ambiente", AmbientePagamento.SANDBOX.name())
        ));
    }

    private void validarAcessoAdminSaas() {
        permissaoAcessoService.validarPermissao(PermissaoAcesso.ACESSAR_ADMIN_SAAS);
    }
}
