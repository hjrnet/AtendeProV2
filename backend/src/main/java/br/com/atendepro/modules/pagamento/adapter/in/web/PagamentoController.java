package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.pagamento.application.port.in.ObterObservabilidadePagamentosSandboxUseCase;
import br.com.atendepro.modules.pagamento.application.port.in.ListarPagamentosSandboxUseCase;
import br.com.atendepro.modules.pagamento.application.port.in.PrepararCheckoutPagamentoUseCase;
import br.com.atendepro.modules.pagamento.application.port.in.ReconciliarDivergenciasPagamentosSandboxUseCase;
import br.com.atendepro.modules.pagamento.application.port.in.RegistrarWebhookAsaasUseCase;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin-saas/pagamentos")
@Profile("!test")
public class PagamentoController {

    private final PrepararCheckoutPagamentoUseCase prepararCheckoutPagamentoUseCase;
    private final RegistrarWebhookAsaasUseCase registrarWebhookAsaasUseCase;
    private final ListarPagamentosSandboxUseCase listarPagamentosSandboxUseCase;
    private final ObterObservabilidadePagamentosSandboxUseCase obterObservabilidadePagamentosSandboxUseCase;
    private final ReconciliarDivergenciasPagamentosSandboxUseCase reconciliarDivergenciasPagamentosSandboxUseCase;

    public PagamentoController(
            PrepararCheckoutPagamentoUseCase prepararCheckoutPagamentoUseCase,
            RegistrarWebhookAsaasUseCase registrarWebhookAsaasUseCase,
            ListarPagamentosSandboxUseCase listarPagamentosSandboxUseCase,
            ObterObservabilidadePagamentosSandboxUseCase obterObservabilidadePagamentosSandboxUseCase,
            ReconciliarDivergenciasPagamentosSandboxUseCase reconciliarDivergenciasPagamentosSandboxUseCase
    ) {
        this.prepararCheckoutPagamentoUseCase = prepararCheckoutPagamentoUseCase;
        this.registrarWebhookAsaasUseCase = registrarWebhookAsaasUseCase;
        this.listarPagamentosSandboxUseCase = listarPagamentosSandboxUseCase;
        this.obterObservabilidadePagamentosSandboxUseCase = obterObservabilidadePagamentosSandboxUseCase;
        this.reconciliarDivergenciasPagamentosSandboxUseCase = reconciliarDivergenciasPagamentosSandboxUseCase;
    }

    @GetMapping("/assinaturas")
    public ResponseEntity<PagamentosSandboxPaginadosResponse> listarPagamentosSandbox(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(PagamentosSandboxPaginadosResponse.de(
                listarPagamentosSandboxUseCase.listarPagamentosSandbox(new Paginacao(pagina, tamanho), empresaId, status)
        ));
    }

    @GetMapping("/observabilidade")
    public ResponseEntity<PagamentosSandboxObservabilidadeResponse> consultarObservabilidadePagamentos(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(required = false) String statusAssinatura,
            @RequestParam(required = false) String eventoTipo,
            @RequestParam(required = false) String tipoDivergencia,
            @RequestParam(required = false) String severidade
    ) {
        return ResponseEntity.ok(PagamentosSandboxObservabilidadeResponse.de(
                obterObservabilidadePagamentosSandboxUseCase.consultarObservabilidadePagamentosSandbox(
                empresaId,
                statusAssinatura,
                eventoTipo,
                tipoDivergencia,
                severidade
                )
        ));
    }

    @PostMapping("/checkout/sandbox")
    public ResponseEntity<CheckoutPagamentoResponse> prepararCheckout(
            @Valid @RequestBody PrepararCheckoutPagamentoRequest request
    ) {
        CheckoutPagamentoResponse response = CheckoutPagamentoResponse.de(
                prepararCheckoutPagamentoUseCase.prepararCheckout(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/admin-saas/pagamentos/assinaturas/" + response.pagamentoAssinaturaId()))
                .body(response);
    }

    @PostMapping("/webhooks/asaas")
    public ResponseEntity<WebhookPagamentoResponse> registrarWebhookAsaas(
            @RequestHeader(value = "X-AtendePro-Webhook-Token", required = false) String token,
            @Valid @RequestBody WebhookAsaasRequest request
    ) {
        return ResponseEntity.ok(WebhookPagamentoResponse.de(
                registrarWebhookAsaasUseCase.registrarWebhook(request.paraCommand(token))
        ));
    }

    @PostMapping("/observabilidade/reconciliar")
    public ResponseEntity<ReconciliarDivergenciasPagamentosSandboxResponse> reconciliarDivergenciasPagamentosSandbox(
            @RequestHeader(value = "X-AtendePro-Webhook-Token", required = false) String token,
            @Valid @RequestBody ReconciliarDivergenciasPagamentosSandboxRequest request
    ) {
        return ResponseEntity.ok(ReconciliarDivergenciasPagamentosSandboxResponse.de(
                reconciliarDivergenciasPagamentosSandboxUseCase.reconciliarDivergenciasPagamentosSandbox(
                        request.paraCommand(token)
                )
        ));
    }
}
