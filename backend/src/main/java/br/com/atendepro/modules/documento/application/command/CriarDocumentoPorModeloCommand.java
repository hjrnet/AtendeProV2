package br.com.atendepro.modules.documento.application.command;

import java.util.UUID;

import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;

public record CriarDocumentoPorModeloCommand(
        UUID modeloId,
        UUID empresaId,
        UUID clientePacienteId,
        UUID profissionalId,
        String profissionalNome,
        String titulo,
        String conteudoComplementar,
        StatusDocumentoProfissional status
) {

    public CriarDocumentoPorModeloCommand {
        if (modeloId == null) {
            throw new IllegalArgumentException("modelo do documento profissional e obrigatorio");
        }
        if (profissionalNome == null || profissionalNome.isBlank()) {
            throw new IllegalArgumentException("nome do profissional do documento e obrigatorio");
        }
        profissionalNome = profissionalNome.trim();
        titulo = titulo == null ? null : titulo.trim();
        conteudoComplementar = conteudoComplementar == null ? null : conteudoComplementar.trim();
    }
}
