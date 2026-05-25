package br.com.atendepro.modules.documento.application.command;

import java.util.UUID;

public record CancelarDocumentoProfissionalCommand(UUID documentoId, String motivo) {

    public CancelarDocumentoProfissionalCommand {
        if (documentoId == null) {
            throw new IllegalArgumentException("documento profissional e obrigatorio");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("motivo do cancelamento do documento profissional e obrigatorio");
        }
        motivo = motivo.trim();
    }
}
