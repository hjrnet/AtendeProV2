package br.com.atendepro.modules.integracao.adapter.in.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.integracao.application.port.in.ConsultarStatusAssinaturaDigitalUseCase;
import br.com.atendepro.modules.integracao.application.port.in.ConsultarStatusPagamentosUseCase;
import br.com.atendepro.modules.integracao.application.port.in.ConsultarStatusWhatsAppUseCase;

@RestController
@RequestMapping("/api/integracoes")
@Profile("!test")
public class IntegracaoFuturaController {

    private final ConsultarStatusWhatsAppUseCase consultarStatusWhatsAppUseCase;
    private final ConsultarStatusPagamentosUseCase consultarStatusPagamentosUseCase;
    private final ConsultarStatusAssinaturaDigitalUseCase consultarStatusAssinaturaDigitalUseCase;

    public IntegracaoFuturaController(
            ConsultarStatusWhatsAppUseCase consultarStatusWhatsAppUseCase,
            ConsultarStatusPagamentosUseCase consultarStatusPagamentosUseCase,
            ConsultarStatusAssinaturaDigitalUseCase consultarStatusAssinaturaDigitalUseCase
    ) {
        this.consultarStatusWhatsAppUseCase = consultarStatusWhatsAppUseCase;
        this.consultarStatusPagamentosUseCase = consultarStatusPagamentosUseCase;
        this.consultarStatusAssinaturaDigitalUseCase = consultarStatusAssinaturaDigitalUseCase;
    }

    @GetMapping("/whatsapp/status")
    public ResponseEntity<IntegracaoFuturaStatusResponse> consultarStatusWhatsApp() {
        return ResponseEntity.ok(IntegracaoFuturaStatusResponse.de(
                consultarStatusWhatsAppUseCase.consultarStatusWhatsApp()
        ));
    }

    @GetMapping("/pagamentos/status")
    public ResponseEntity<IntegracaoFuturaStatusResponse> consultarStatusPagamentos() {
        return ResponseEntity.ok(IntegracaoFuturaStatusResponse.de(
                consultarStatusPagamentosUseCase.consultarStatusPagamentos()
        ));
    }

    @GetMapping("/assinatura-digital/status")
    public ResponseEntity<IntegracaoFuturaStatusResponse> consultarStatusAssinaturaDigital() {
        return ResponseEntity.ok(IntegracaoFuturaStatusResponse.de(
                consultarStatusAssinaturaDigitalUseCase.consultarStatusAssinaturaDigital()
        ));
    }
}
