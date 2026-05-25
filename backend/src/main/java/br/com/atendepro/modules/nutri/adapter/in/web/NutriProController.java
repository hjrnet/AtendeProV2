package br.com.atendepro.modules.nutri.adapter.in.web;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.nutri.application.command.ConsultarVisaoNutriProCommand;
import br.com.atendepro.modules.nutri.application.port.in.ConsultarVisaoNutriProUseCase;

@RestController
@RequestMapping("/api/nutri-pro")
@Profile("!test")
public class NutriProController {

    private final ConsultarVisaoNutriProUseCase consultarVisaoNutriProUseCase;

    public NutriProController(ConsultarVisaoNutriProUseCase consultarVisaoNutriProUseCase) {
        this.consultarVisaoNutriProUseCase = consultarVisaoNutriProUseCase;
    }

    @GetMapping("/visao")
    public ResponseEntity<VisaoNutriProResponse> consultarVisaoNutriPro(
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(VisaoNutriProResponse.de(
                consultarVisaoNutriProUseCase.consultarVisaoNutriPro(new ConsultarVisaoNutriProCommand(empresaId))
        ));
    }
}
