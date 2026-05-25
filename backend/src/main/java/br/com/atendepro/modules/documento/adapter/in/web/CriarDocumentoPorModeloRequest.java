package br.com.atendepro.modules.documento.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.documento.application.command.CriarDocumentoPorModeloCommand;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarDocumentoPorModeloRequest(
        UUID empresaId,
        UUID clientePacienteId,
        UUID profissionalId,
        @NotBlank @Size(max = 160) String profissionalNome,
        @Size(max = 180) String titulo,
        @Size(max = 8000) String conteudoComplementar,
        StatusDocumentoProfissional status
) {

    public CriarDocumentoPorModeloCommand paraCommand(UUID modeloId) {
        return new CriarDocumentoPorModeloCommand(
                modeloId,
                empresaId,
                clientePacienteId,
                profissionalId,
                profissionalNome,
                titulo,
                conteudoComplementar,
                status
        );
    }
}
