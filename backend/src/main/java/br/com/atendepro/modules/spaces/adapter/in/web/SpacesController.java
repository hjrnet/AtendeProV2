package br.com.atendepro.modules.spaces.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.spaces.application.port.in.CadastrarRecursoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ConsultarSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.DetalharRecursoSpacesUseCase;
import br.com.atendepro.modules.spaces.application.port.in.ListarRecursosSpacesUseCase;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/spaces")
@Profile("!test")
public class SpacesController {

    private final ConsultarSpacesUseCase consultarSpacesUseCase;
    private final CadastrarRecursoSpacesUseCase cadastrarRecursoSpacesUseCase;
    private final DetalharRecursoSpacesUseCase detalharRecursoSpacesUseCase;
    private final ListarRecursosSpacesUseCase listarRecursosSpacesUseCase;

    public SpacesController(
            ConsultarSpacesUseCase consultarSpacesUseCase,
            CadastrarRecursoSpacesUseCase cadastrarRecursoSpacesUseCase,
            DetalharRecursoSpacesUseCase detalharRecursoSpacesUseCase,
            ListarRecursosSpacesUseCase listarRecursosSpacesUseCase
    ) {
        this.consultarSpacesUseCase = consultarSpacesUseCase;
        this.cadastrarRecursoSpacesUseCase = cadastrarRecursoSpacesUseCase;
        this.detalharRecursoSpacesUseCase = detalharRecursoSpacesUseCase;
        this.listarRecursosSpacesUseCase = listarRecursosSpacesUseCase;
    }

    @GetMapping("/status")
    public ResponseEntity<SpacesStatusResponse> consultarStatus() {
        return ResponseEntity.ok(SpacesStatusResponse.de(consultarSpacesUseCase.consultarStatus()));
    }

    @PostMapping("/recursos")
    public ResponseEntity<RecursoSpacesResponse> cadastrarRecurso(@Valid @RequestBody CadastrarRecursoSpacesRequest request) {
        RecursoSpacesResponse response = RecursoSpacesResponse.de(
                cadastrarRecursoSpacesUseCase.cadastrarRecurso(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/spaces/recursos/" + response.id())).body(response);
    }

    @GetMapping("/recursos/{recursoId}")
    public ResponseEntity<RecursoSpacesResponse> detalharRecurso(@PathVariable UUID recursoId) {
        return detalharRecursoSpacesUseCase.detalharRecurso(recursoId)
                .map(RecursoSpacesResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/recursos")
    public ResponseEntity<RecursosSpacesPaginadosResponse> listarRecursos(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoRecursoSpaces tipo,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(RecursosSpacesPaginadosResponse.de(
                listarRecursosSpacesUseCase.listarRecursos(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        tipo,
                        ativo
                )
        ));
    }
}
