package br.com.atendepro.modules.marketing.adapter.in.web;

import java.net.URI;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.marketing.application.port.in.RegistrarLeadMarketingUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/marketing/leads")
@Profile("!test")
public class LeadMarketingController {

    private final RegistrarLeadMarketingUseCase registrarLeadMarketingUseCase;

    public LeadMarketingController(RegistrarLeadMarketingUseCase registrarLeadMarketingUseCase) {
        this.registrarLeadMarketingUseCase = registrarLeadMarketingUseCase;
    }

    @PostMapping
    public ResponseEntity<LeadMarketingResponse> registrarLead(
            @Valid @RequestBody RegistrarLeadMarketingRequest request
    ) {
        LeadMarketingResponse response = LeadMarketingResponse.de(registrarLeadMarketingUseCase.registrarLead(request.paraCommand()));
        return ResponseEntity.created(URI.create("/api/marketing/leads/" + response.id())).body(response);
    }
}
