package br.com.atendepro.modules.precificacao.adapter.in.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.precificacao.application.port.in.CalcularCustoRealUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularPrecoMinimoUseCase;
import br.com.atendepro.modules.precificacao.application.port.in.CalcularPrecificacaoBaseUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/precificacao")
@Profile("!test")
public class PrecificacaoController {

    private final CalcularPrecificacaoBaseUseCase calcularPrecificacaoBaseUseCase;
    private final CalcularCustoRealUseCase calcularCustoRealUseCase;
    private final CalcularPrecoMinimoUseCase calcularPrecoMinimoUseCase;

    public PrecificacaoController(
            CalcularPrecificacaoBaseUseCase calcularPrecificacaoBaseUseCase,
            CalcularCustoRealUseCase calcularCustoRealUseCase,
            CalcularPrecoMinimoUseCase calcularPrecoMinimoUseCase
    ) {
        this.calcularPrecificacaoBaseUseCase = calcularPrecificacaoBaseUseCase;
        this.calcularCustoRealUseCase = calcularCustoRealUseCase;
        this.calcularPrecoMinimoUseCase = calcularPrecoMinimoUseCase;
    }

    @PostMapping("/calculos/base")
    public ResponseEntity<CalculoPrecificacaoBaseResponse> calcularPrecificacaoBase(
            @Valid @RequestBody CalcularPrecificacaoBaseRequest request
    ) {
        return ResponseEntity.ok(CalculoPrecificacaoBaseResponse.de(
                calcularPrecificacaoBaseUseCase.calcularPrecificacaoBase(request.paraCommand())
        ));
    }

    @PostMapping("/calculos/custo-real")
    public ResponseEntity<CustoRealPrecificacaoResponse> calcularCustoReal(
            @Valid @RequestBody CalcularCustoRealRequest request
    ) {
        return ResponseEntity.ok(CustoRealPrecificacaoResponse.de(
                calcularCustoRealUseCase.calcularCustoReal(request.paraCommand())
        ));
    }

    @PostMapping("/calculos/preco-minimo")
    public ResponseEntity<PrecoMinimoPrecificacaoResponse> calcularPrecoMinimo(
            @Valid @RequestBody CalcularPrecoMinimoRequest request
    ) {
        return ResponseEntity.ok(PrecoMinimoPrecificacaoResponse.de(
                calcularPrecoMinimoUseCase.calcularPrecoMinimo(request.paraCommand())
        ));
    }
}
