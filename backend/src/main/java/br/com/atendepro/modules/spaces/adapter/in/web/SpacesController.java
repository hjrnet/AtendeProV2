package br.com.atendepro.modules.spaces.adapter.in.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.spaces.application.port.in.ConsultarSpacesUseCase;

@RestController
@RequestMapping("/api/spaces")
@Profile("!test")
public class SpacesController {

    private final ConsultarSpacesUseCase consultarSpacesUseCase;

    public SpacesController(ConsultarSpacesUseCase consultarSpacesUseCase) {
        this.consultarSpacesUseCase = consultarSpacesUseCase;
    }

    @GetMapping("/status")
    public ResponseEntity<SpacesStatusResponse> consultarStatus() {
        return ResponseEntity.ok(SpacesStatusResponse.de(consultarSpacesUseCase.consultarStatus()));
    }
}
