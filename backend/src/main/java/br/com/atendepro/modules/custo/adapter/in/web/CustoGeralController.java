package br.com.atendepro.modules.custo.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.custo.application.port.in.BuscarCustoGeralUseCase;
import br.com.atendepro.modules.custo.application.port.in.CadastrarCustoGeralUseCase;
import br.com.atendepro.modules.custo.application.port.in.ListarCustosGeraisUseCase;
import br.com.atendepro.modules.custo.domain.model.TipoCustoGeral;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/custos/gerais")
@Profile("!test")
public class CustoGeralController {

    private final CadastrarCustoGeralUseCase cadastrarCustoGeralUseCase;
    private final BuscarCustoGeralUseCase buscarCustoGeralUseCase;
    private final ListarCustosGeraisUseCase listarCustosGeraisUseCase;

    public CustoGeralController(
            CadastrarCustoGeralUseCase cadastrarCustoGeralUseCase,
            BuscarCustoGeralUseCase buscarCustoGeralUseCase,
            ListarCustosGeraisUseCase listarCustosGeraisUseCase
    ) {
        this.cadastrarCustoGeralUseCase = cadastrarCustoGeralUseCase;
        this.buscarCustoGeralUseCase = buscarCustoGeralUseCase;
        this.listarCustosGeraisUseCase = listarCustosGeraisUseCase;
    }

    @PostMapping
    public ResponseEntity<CustoGeralResponse> cadastrarCustoGeral(@Valid @RequestBody CadastrarCustoGeralRequest request) {
        CustoGeralResponse response = CustoGeralResponse.de(cadastrarCustoGeralUseCase.cadastrarCustoGeral(request.paraCommand()));
        return ResponseEntity.created(URI.create("/api/custos/gerais/" + response.id())).body(response);
    }

    @GetMapping("/{custoId}")
    public ResponseEntity<CustoGeralResponse> buscarCustoGeral(@PathVariable UUID custoId) {
        return buscarCustoGeralUseCase.buscarCustoGeralPorId(custoId)
                .map(CustoGeralResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<CustosGeraisPaginadosResponse> listarCustosGerais(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoCustoGeral tipo,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(CustosGeraisPaginadosResponse.de(
                listarCustosGeraisUseCase.listarCustosGerais(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        tipo,
                        categoria,
                        ativo
                )
        ));
    }
}
