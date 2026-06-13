package br.com.atendepro.modules.pagamento.adapter.in.web;

import java.net.URI;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.pagamento.application.port.in.PrepararCheckoutPagamentoUseCase;
import br.com.atendepro.modules.pagamento.application.port.in.RegistrarWebhookAsaasUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin-saas/pagamentos")
@Profile("!test")
public class PagamentoController {

    private final PrepararCheckoutPagamentoUseCase prepararCheckoutPagamentoUseCase;
    private final RegistrarWebhookAsaasUseCase registrarWebhookAsaasUseCase;

    public PagamentoController(
            PrepararCheckoutPagamentoUseCase prepararCheckoutPagamentoUseCase,
            RegistrarWebhookAsaasUseCase registrarWebhookAsaasUseCase
    ) {
        this.prepararCheckoutPagamentoUseCase = prepararCheckoutPagamentoUseCase;
        this.registrarWebhookAsaasUseCase = registrarWebhookAsaasUseCase;
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
}
