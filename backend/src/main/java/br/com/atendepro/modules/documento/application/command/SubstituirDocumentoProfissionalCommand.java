package br.com.atendepro.modules.documento.application.command;

import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;

public record SubstituirDocumentoProfissionalCommand(
        UUID documentoId,
        String titulo,
        String conteudo,
        StatusDocumentoProfissional status,
        String motivo
) {

    public SubstituirDocumentoProfissionalCommand {
        if (documentoId == null) {
            throw new IllegalArgumentException("documento profissional e obrigatorio");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("titulo do documento profissional e obrigatorio");
        }
        if (conteudo == null || conteudo.isBlank()) {
            throw new IllegalArgumentException("conteudo do documento profissional e obrigatorio");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("motivo da substituicao do documento profissional e obrigatorio");
        }
        titulo = titulo.trim();
        conteudo = conteudo.trim();
        motivo = motivo.trim();
    }
}
