package br.com.atendepro.modules.documento.adapter.in.web;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.atendepro.modules.documento.application.port.in.ValidarDocumentoProfissionalUseCase;

@RestController
@RequestMapping("/api/documentos-profissionais/validacao")
@Profile("!test")
public class ValidacaoDocumentoProfissionalController {

    private final ValidarDocumentoProfissionalUseCase validarDocumentoProfissionalUseCase;

    public ValidacaoDocumentoProfissionalController(
            ValidarDocumentoProfissionalUseCase validarDocumentoProfissionalUseCase
    ) {
        this.validarDocumentoProfissionalUseCase = validarDocumentoProfissionalUseCase;
    }

    @GetMapping("/{codigoValidacao}")
    public ResponseEntity<ValidacaoDocumentoProfissionalResponse> validarDocumento(
            @PathVariable String codigoValidacao
    ) {
        return ResponseEntity.ok(ValidacaoDocumentoProfissionalResponse.de(
                validarDocumentoProfissionalUseCase.validarDocumento(codigoValidacao)
        ));
    }
}
