package br.com.atendepro.modules.documento.adapter.in.web;

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

import br.com.atendepro.modules.documento.application.port.in.CriarCarimboProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.DetalharCarimboProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.ListarCarimbosProfissionaisUseCase;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/documentos-profissionais/carimbos")
@Profile("!test")
public class CarimboProfissionalController {

    private final CriarCarimboProfissionalUseCase criarCarimboProfissionalUseCase;
    private final DetalharCarimboProfissionalUseCase detalharCarimboProfissionalUseCase;
    private final ListarCarimbosProfissionaisUseCase listarCarimbosProfissionaisUseCase;

    public CarimboProfissionalController(
            CriarCarimboProfissionalUseCase criarCarimboProfissionalUseCase,
            DetalharCarimboProfissionalUseCase detalharCarimboProfissionalUseCase,
            ListarCarimbosProfissionaisUseCase listarCarimbosProfissionaisUseCase
    ) {
        this.criarCarimboProfissionalUseCase = criarCarimboProfissionalUseCase;
        this.detalharCarimboProfissionalUseCase = detalharCarimboProfissionalUseCase;
        this.listarCarimbosProfissionaisUseCase = listarCarimbosProfissionaisUseCase;
    }

    @PostMapping
    public ResponseEntity<CarimboProfissionalResponse> criarCarimbo(
            @Valid @RequestBody CriarCarimboProfissionalRequest request
    ) {
        CarimboProfissionalResponse response = CarimboProfissionalResponse.de(
                criarCarimboProfissionalUseCase.criarCarimbo(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/documentos-profissionais/carimbos/" + response.id())).body(response);
    }

    @GetMapping("/{carimboId}")
    public ResponseEntity<CarimboProfissionalResponse> detalharCarimbo(@PathVariable UUID carimboId) {
        return detalharCarimboProfissionalUseCase.detalharCarimbo(carimboId)
                .map(CarimboProfissionalResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<CarimbosProfissionaisPaginadosResponse> listarCarimbos(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) ConselhoProfissional conselho,
            @RequestParam(required = false) String uf,
            @RequestParam(required = false) UUID profissionalId,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(CarimbosProfissionaisPaginadosResponse.de(
                listarCarimbosProfissionaisUseCase.listarCarimbos(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        conselho,
                        uf,
                        profissionalId,
                        ativo
                )
        ));
    }
}
