package br.com.atendepro.modules.estoque.adapter.in.web;

import java.net.URI;
import java.time.LocalDate;
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

import br.com.atendepro.modules.estoque.application.port.in.BuscarProdutoEstoqueUseCase;
import br.com.atendepro.modules.estoque.application.port.in.CadastrarProdutoEstoqueUseCase;
import br.com.atendepro.modules.estoque.application.port.in.ListarProdutosEstoqueUseCase;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estoque/produtos")
@Profile("!test")
public class ProdutoEstoqueController {

    private final CadastrarProdutoEstoqueUseCase cadastrarProdutoEstoqueUseCase;
    private final BuscarProdutoEstoqueUseCase buscarProdutoEstoqueUseCase;
    private final ListarProdutosEstoqueUseCase listarProdutosEstoqueUseCase;

    public ProdutoEstoqueController(
            CadastrarProdutoEstoqueUseCase cadastrarProdutoEstoqueUseCase,
            BuscarProdutoEstoqueUseCase buscarProdutoEstoqueUseCase,
            ListarProdutosEstoqueUseCase listarProdutosEstoqueUseCase
    ) {
        this.cadastrarProdutoEstoqueUseCase = cadastrarProdutoEstoqueUseCase;
        this.buscarProdutoEstoqueUseCase = buscarProdutoEstoqueUseCase;
        this.listarProdutosEstoqueUseCase = listarProdutosEstoqueUseCase;
    }

    @PostMapping
    public ResponseEntity<ProdutoEstoqueResponse> cadastrarProdutoEstoque(
            @Valid @RequestBody CadastrarProdutoEstoqueRequest request
    ) {
        ProdutoEstoqueResponse response = ProdutoEstoqueResponse.de(
                cadastrarProdutoEstoqueUseCase.cadastrarProdutoEstoque(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/estoque/produtos/" + response.id())).body(response);
    }

    @GetMapping("/{produtoId}")
    public ResponseEntity<ProdutoEstoqueResponse> buscarProdutoEstoque(@PathVariable UUID produtoId) {
        return buscarProdutoEstoqueUseCase.buscarProdutoEstoquePorId(produtoId)
                .map(ProdutoEstoqueResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<ProdutosEstoquePaginadosResponse> listarProdutosEstoque(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) LocalDate vencendoAte
    ) {
        return ResponseEntity.ok(ProdutosEstoquePaginadosResponse.de(
                listarProdutosEstoqueUseCase.listarProdutosEstoque(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        categoria,
                        ativo,
                        vencendoAte
                )
        ));
    }
}
