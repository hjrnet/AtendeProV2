package br.com.atendepro.modules.documento.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.documento.application.command.CancelarDocumentoProfissionalCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelarDocumentoProfissionalRequest(@NotBlank @Size(max = 500) String motivo) {

    public CancelarDocumentoProfissionalCommand paraCommand(UUID documentoId) {
        return new CancelarDocumentoProfissionalCommand(documentoId, motivo);
    }
}
