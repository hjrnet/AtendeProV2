package br.com.atendepro.modules.beauty.adapter.in.web;

import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.beauty.application.command.ConsultarVisaoBeautyProCommand;
import br.com.atendepro.modules.beauty.application.port.in.ConsultarVisaoBeautyProUseCase;

@RestController
@RequestMapping("/api/beauty-pro")
@Profile("!test")
public class BeautyProController {

    private final ConsultarVisaoBeautyProUseCase consultarVisaoBeautyProUseCase;

    public BeautyProController(ConsultarVisaoBeautyProUseCase consultarVisaoBeautyProUseCase) {
        this.consultarVisaoBeautyProUseCase = consultarVisaoBeautyProUseCase;
    }

    @GetMapping("/visao")
    public ResponseEntity<VisaoBeautyProResponse> consultarVisaoBeautyPro(
            @RequestParam(required = false) UUID empresaId
    ) {
        return ResponseEntity.ok(VisaoBeautyProResponse.de(
                consultarVisaoBeautyProUseCase.consultarVisaoBeautyPro(new ConsultarVisaoBeautyProCommand(empresaId))
        ));
    }
}
