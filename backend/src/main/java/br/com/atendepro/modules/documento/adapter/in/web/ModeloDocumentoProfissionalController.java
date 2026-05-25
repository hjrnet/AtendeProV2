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

import br.com.atendepro.modules.documento.application.port.in.CriarDocumentoProfissionalPorModeloUseCase;
import br.com.atendepro.modules.documento.application.port.in.DetalharModeloDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.ListarModelosDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/documentos-profissionais/modelos")
@Profile("!test")
public class ModeloDocumentoProfissionalController {

    private final ListarModelosDocumentoProfissionalUseCase listarModelosDocumentoProfissionalUseCase;
    private final DetalharModeloDocumentoProfissionalUseCase detalharModeloDocumentoProfissionalUseCase;
    private final CriarDocumentoProfissionalPorModeloUseCase criarDocumentoProfissionalPorModeloUseCase;

    public ModeloDocumentoProfissionalController(
            ListarModelosDocumentoProfissionalUseCase listarModelosDocumentoProfissionalUseCase,
            DetalharModeloDocumentoProfissionalUseCase detalharModeloDocumentoProfissionalUseCase,
            CriarDocumentoProfissionalPorModeloUseCase criarDocumentoProfissionalPorModeloUseCase
    ) {
        this.listarModelosDocumentoProfissionalUseCase = listarModelosDocumentoProfissionalUseCase;
        this.detalharModeloDocumentoProfissionalUseCase = detalharModeloDocumentoProfissionalUseCase;
        this.criarDocumentoProfissionalPorModeloUseCase = criarDocumentoProfissionalPorModeloUseCase;
    }

    @GetMapping
    public ResponseEntity<ModelosDocumentoProfissionalPaginadosResponse> listarModelos(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoDocumentoProfissional tipo,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(ModelosDocumentoProfissionalPaginadosResponse.de(
                listarModelosDocumentoProfissionalUseCase.listarModelos(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        tipo,
                        ativo
                )
        ));
    }

    @GetMapping("/{modeloId}")
    public ResponseEntity<ModeloDocumentoProfissionalResponse> detalharModelo(
            @PathVariable UUID modeloId,
            @RequestParam(required = false) UUID empresaId
    ) {
        return detalharModeloDocumentoProfissionalUseCase.detalharModelo(modeloId, empresaId)
                .map(ModeloDocumentoProfissionalResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{modeloId}/documentos")
    public ResponseEntity<DocumentoProfissionalResponse> criarDocumentoPorModelo(
            @PathVariable UUID modeloId,
            @Valid @RequestBody CriarDocumentoPorModeloRequest request
    ) {
        DocumentoProfissionalResponse response = DocumentoProfissionalResponse.de(
                criarDocumentoProfissionalPorModeloUseCase.criarDocumentoPorModelo(request.paraCommand(modeloId))
        );
        return ResponseEntity.created(URI.create("/api/documentos-profissionais/" + response.id())).body(response);
    }
}
