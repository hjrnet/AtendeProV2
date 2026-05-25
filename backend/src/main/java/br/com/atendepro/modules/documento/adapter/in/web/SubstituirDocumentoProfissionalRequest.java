package br.com.atendepro.modules.documento.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.documento.application.command.SubstituirDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubstituirDocumentoProfissionalRequest(
        @NotBlank @Size(max = 180) String titulo,
        @NotBlank @Size(max = 12000) String conteudo,
        StatusDocumentoProfissional status,
        @NotBlank @Size(max = 500) String motivo
) {

    public SubstituirDocumentoProfissionalCommand paraCommand(UUID documentoId) {
        return new SubstituirDocumentoProfissionalCommand(documentoId, titulo, conteudo, status, motivo);
    }
}
