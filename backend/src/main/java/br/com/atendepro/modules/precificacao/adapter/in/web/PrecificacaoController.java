package br.com.atendepro.modules.precificacao.adapter.in.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.precificacao.application.port.in.CalcularPrecificacaoBaseUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/precificacao")
@Profile("!test")
public class PrecificacaoController {

    private final CalcularPrecificacaoBaseUseCase calcularPrecificacaoBaseUseCase;

    public PrecificacaoController(CalcularPrecificacaoBaseUseCase calcularPrecificacaoBaseUseCase) {
        this.calcularPrecificacaoBaseUseCase = calcularPrecificacaoBaseUseCase;
    }

    @PostMapping("/calculos/base")
    public ResponseEntity<CalculoPrecificacaoBaseResponse> calcularPrecificacaoBase(
            @Valid @RequestBody CalcularPrecificacaoBaseRequest request
    ) {
        return ResponseEntity.ok(CalculoPrecificacaoBaseResponse.de(
                calcularPrecificacaoBaseUseCase.calcularPrecificacaoBase(request.paraCommand())
        ));
    }
}
