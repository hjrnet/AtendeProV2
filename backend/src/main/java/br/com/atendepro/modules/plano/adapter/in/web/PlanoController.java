package br.com.atendepro.modules.plano.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.plano.application.port.in.AtualizarPlanoUseCase;
import br.com.atendepro.modules.plano.application.port.in.BuscarPlanoUseCase;
import br.com.atendepro.modules.plano.application.port.in.CriarPlanoUseCase;
import br.com.atendepro.modules.plano.application.port.in.ListarPlanosUseCase;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin-saas/planos")
@Profile("!test")
public class PlanoController {

    private final CriarPlanoUseCase criarPlanoUseCase;
    private final AtualizarPlanoUseCase atualizarPlanoUseCase;
    private final BuscarPlanoUseCase buscarPlanoUseCase;
    private final ListarPlanosUseCase listarPlanosUseCase;

    public PlanoController(
            CriarPlanoUseCase criarPlanoUseCase,
            AtualizarPlanoUseCase atualizarPlanoUseCase,
            BuscarPlanoUseCase buscarPlanoUseCase,
            ListarPlanosUseCase listarPlanosUseCase
    ) {
        this.criarPlanoUseCase = criarPlanoUseCase;
        this.atualizarPlanoUseCase = atualizarPlanoUseCase;
        this.buscarPlanoUseCase = buscarPlanoUseCase;
        this.listarPlanosUseCase = listarPlanosUseCase;
    }

    @PostMapping
    public ResponseEntity<PlanoResponse> criarPlano(@Valid @RequestBody CriarPlanoRequest request) {
        PlanoResponse response = PlanoResponse.de(criarPlanoUseCase.criarPlano(request.paraCommand()));
        return ResponseEntity.created(URI.create("/api/admin-saas/planos/" + response.id())).body(response);
    }

    @GetMapping
    public ResponseEntity<PlanosPaginadosResponse> listarPlanos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(PlanosPaginadosResponse.de(
                listarPlanosUseCase.listarPlanos(new Paginacao(pagina, tamanho), busca, ativo)
        ));
    }

    @GetMapping("/{planoId}")
    public ResponseEntity<PlanoResponse> buscarPlano(@PathVariable UUID planoId) {
        return buscarPlanoUseCase.buscarPlanoPorId(planoId)
                .map(PlanoResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{planoId}")
    public ResponseEntity<PlanoResponse> atualizarPlano(
            @PathVariable UUID planoId,
            @Valid @RequestBody AtualizarPlanoRequest request
    ) {
        return atualizarPlanoUseCase.atualizarPlano(request.paraCommand(planoId))
                .map(PlanoResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
