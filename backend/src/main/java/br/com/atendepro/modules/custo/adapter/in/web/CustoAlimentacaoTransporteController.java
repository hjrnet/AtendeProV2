package br.com.atendepro.modules.custo.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.custo.application.port.in.CadastrarCustoAlimentacaoTransporteUseCase;
import br.com.atendepro.modules.custo.application.port.in.ListarCustosAlimentacaoTransporteUseCase;
import br.com.atendepro.modules.custo.domain.model.TipoCustoPessoal;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/custos/alimentacao-transporte")
@Profile("!test")
public class CustoAlimentacaoTransporteController {

    private final CadastrarCustoAlimentacaoTransporteUseCase cadastrarCustoAlimentacaoTransporteUseCase;
    private final ListarCustosAlimentacaoTransporteUseCase listarCustosAlimentacaoTransporteUseCase;

    public CustoAlimentacaoTransporteController(
            CadastrarCustoAlimentacaoTransporteUseCase cadastrarCustoAlimentacaoTransporteUseCase,
            ListarCustosAlimentacaoTransporteUseCase listarCustosAlimentacaoTransporteUseCase
    ) {
        this.cadastrarCustoAlimentacaoTransporteUseCase = cadastrarCustoAlimentacaoTransporteUseCase;
        this.listarCustosAlimentacaoTransporteUseCase = listarCustosAlimentacaoTransporteUseCase;
    }

    @PostMapping
    public ResponseEntity<CustoAlimentacaoTransporteResponse> cadastrarCusto(
            @Valid @RequestBody CadastrarCustoAlimentacaoTransporteRequest request
    ) {
        CustoAlimentacaoTransporteResponse response = CustoAlimentacaoTransporteResponse.de(
                cadastrarCustoAlimentacaoTransporteUseCase.cadastrarCustoAlimentacaoTransporte(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/custos/alimentacao-transporte/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<CustosAlimentacaoTransportePaginadosResponse> listarCustos(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) TipoCustoPessoal tipo,
            @RequestParam(required = false) UUID profissionalId,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(CustosAlimentacaoTransportePaginadosResponse.de(
                listarCustosAlimentacaoTransporteUseCase.listarCustosAlimentacaoTransporte(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        tipo,
                        profissionalId,
                        ativo
                )
        ));
    }
}
