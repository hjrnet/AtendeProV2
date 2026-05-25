package br.com.atendepro.modules.vertical.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.vertical.application.port.in.DetalharVerticalProfissionalUseCase;
import br.com.atendepro.modules.vertical.application.port.in.ListarVerticaisProfissionaisUseCase;
import br.com.atendepro.modules.vertical.domain.model.CodigoVerticalProfissional;

@RestController
@RequestMapping("/api/verticais-profissionais")
public class VerticalProfissionalController {

    private final ListarVerticaisProfissionaisUseCase listarVerticaisProfissionaisUseCase;
    private final DetalharVerticalProfissionalUseCase detalharVerticalProfissionalUseCase;

    public VerticalProfissionalController(
            ListarVerticaisProfissionaisUseCase listarVerticaisProfissionaisUseCase,
            DetalharVerticalProfissionalUseCase detalharVerticalProfissionalUseCase
    ) {
        this.listarVerticaisProfissionaisUseCase = listarVerticaisProfissionaisUseCase;
        this.detalharVerticalProfissionalUseCase = detalharVerticalProfissionalUseCase;
    }

    @GetMapping
    public ResponseEntity<VerticaisProfissionaisResponse> listarVerticais() {
        return ResponseEntity.ok(VerticaisProfissionaisResponse.de(
                listarVerticaisProfissionaisUseCase.listarVerticais()
        ));
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<VerticalProfissionalResponse> detalharVertical(@PathVariable CodigoVerticalProfissional codigo) {
        return detalharVerticalProfissionalUseCase.detalharVertical(codigo)
                .map(VerticalProfissionalResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
