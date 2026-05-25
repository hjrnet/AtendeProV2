package br.com.atendepro.modules.documento.adapter.in.web;

import java.net.URI;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.documento.application.port.in.CriarDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.CancelarDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.DetalharDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.GerarPdfDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.ListarHistoricoDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.port.in.ListarDocumentosProfissionaisUseCase;
import br.com.atendepro.modules.documento.application.port.in.SubstituirDocumentoProfissionalUseCase;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalPdfResult;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import br.com.atendepro.shared.application.pagination.Paginacao;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/documentos-profissionais")
@Profile("!test")
public class DocumentoProfissionalController {

    private final CriarDocumentoProfissionalUseCase criarDocumentoProfissionalUseCase;
    private final DetalharDocumentoProfissionalUseCase detalharDocumentoProfissionalUseCase;
    private final ListarDocumentosProfissionaisUseCase listarDocumentosProfissionaisUseCase;
    private final GerarPdfDocumentoProfissionalUseCase gerarPdfDocumentoProfissionalUseCase;
    private final SubstituirDocumentoProfissionalUseCase substituirDocumentoProfissionalUseCase;
    private final CancelarDocumentoProfissionalUseCase cancelarDocumentoProfissionalUseCase;
    private final ListarHistoricoDocumentoProfissionalUseCase listarHistoricoDocumentoProfissionalUseCase;

    public DocumentoProfissionalController(
            CriarDocumentoProfissionalUseCase criarDocumentoProfissionalUseCase,
            DetalharDocumentoProfissionalUseCase detalharDocumentoProfissionalUseCase,
            ListarDocumentosProfissionaisUseCase listarDocumentosProfissionaisUseCase,
            GerarPdfDocumentoProfissionalUseCase gerarPdfDocumentoProfissionalUseCase,
            SubstituirDocumentoProfissionalUseCase substituirDocumentoProfissionalUseCase,
            CancelarDocumentoProfissionalUseCase cancelarDocumentoProfissionalUseCase,
            ListarHistoricoDocumentoProfissionalUseCase listarHistoricoDocumentoProfissionalUseCase
    ) {
        this.criarDocumentoProfissionalUseCase = criarDocumentoProfissionalUseCase;
        this.detalharDocumentoProfissionalUseCase = detalharDocumentoProfissionalUseCase;
        this.listarDocumentosProfissionaisUseCase = listarDocumentosProfissionaisUseCase;
        this.gerarPdfDocumentoProfissionalUseCase = gerarPdfDocumentoProfissionalUseCase;
        this.substituirDocumentoProfissionalUseCase = substituirDocumentoProfissionalUseCase;
        this.cancelarDocumentoProfissionalUseCase = cancelarDocumentoProfissionalUseCase;
        this.listarHistoricoDocumentoProfissionalUseCase = listarHistoricoDocumentoProfissionalUseCase;
    }

    @PostMapping
    public ResponseEntity<DocumentoProfissionalResponse> criarDocumento(
            @Valid @RequestBody CriarDocumentoProfissionalRequest request
    ) {
        DocumentoProfissionalResponse response = DocumentoProfissionalResponse.de(
                criarDocumentoProfissionalUseCase.criarDocumento(request.paraCommand())
        );
        return ResponseEntity.created(URI.create("/api/documentos-profissionais/" + response.id())).body(response);
    }

    @GetMapping("/{documentoId}")
    public ResponseEntity<DocumentoProfissionalResponse> detalharDocumento(@PathVariable UUID documentoId) {
        return detalharDocumentoProfissionalUseCase.detalharDocumento(documentoId)
                .map(DocumentoProfissionalResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{documentoId}/substituicoes")
    public ResponseEntity<DocumentoProfissionalResponse> substituirDocumento(
            @PathVariable UUID documentoId,
            @Valid @RequestBody SubstituirDocumentoProfissionalRequest request
    ) {
        return ResponseEntity.ok(DocumentoProfissionalResponse.de(
                substituirDocumentoProfissionalUseCase.substituirDocumento(request.paraCommand(documentoId))
        ));
    }

    @PostMapping("/{documentoId}/cancelamento")
    public ResponseEntity<DocumentoProfissionalResponse> cancelarDocumento(
            @PathVariable UUID documentoId,
            @Valid @RequestBody CancelarDocumentoProfissionalRequest request
    ) {
        return ResponseEntity.ok(DocumentoProfissionalResponse.de(
                cancelarDocumentoProfissionalUseCase.cancelarDocumento(request.paraCommand(documentoId))
        ));
    }

    @GetMapping("/{documentoId}/historico")
    public ResponseEntity<HistoricoDocumentoProfissionalPaginadoResponse> listarHistorico(
            @PathVariable UUID documentoId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho
    ) {
        return ResponseEntity.ok(HistoricoDocumentoProfissionalPaginadoResponse.de(
                listarHistoricoDocumentoProfissionalUseCase.listarHistorico(
                        documentoId,
                        new Paginacao(pagina, tamanho)
                )
        ));
    }

    @GetMapping(value = "/{documentoId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> gerarPdf(
            @PathVariable UUID documentoId,
            @RequestParam(required = false) UUID carimboId
    ) {
        DocumentoProfissionalPdfResult pdf = gerarPdfDocumentoProfissionalUseCase.gerarPdf(documentoId, carimboId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(pdf.contentType()))
                .contentLength(pdf.conteudo().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(pdf.nomeArquivo())
                        .build()
                        .toString())
                .body(pdf.conteudo());
    }

    @GetMapping
    public ResponseEntity<DocumentosProfissionaisPaginadosResponse> listarDocumentos(
            @RequestParam(required = false) UUID empresaId,
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanho,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) TipoDocumentoProfissional tipo,
            @RequestParam(required = false) StatusDocumentoProfissional status,
            @RequestParam(required = false) UUID clientePacienteId,
            @RequestParam(required = false) Boolean ativo
    ) {
        return ResponseEntity.ok(DocumentosProfissionaisPaginadosResponse.de(
                listarDocumentosProfissionaisUseCase.listarDocumentos(
                        empresaId,
                        new Paginacao(pagina, tamanho),
                        busca,
                        tipo,
                        status,
                        clientePacienteId,
                        ativo
                )
        ));
    }
}
