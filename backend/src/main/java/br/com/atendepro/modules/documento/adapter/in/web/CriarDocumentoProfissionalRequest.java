package br.com.atendepro.modules.documento.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.documento.application.command.CriarDocumentoProfissionalCommand;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarDocumentoProfissionalRequest(
        UUID empresaId,
        UUID clientePacienteId,
        UUID profissionalId,
        @NotBlank @Size(max = 160) String profissionalNome,
        @NotBlank @Size(max = 180) String titulo,
        @NotNull TipoDocumentoProfissional tipo,
        @NotBlank @Size(max = 12000) String conteudo,
        StatusDocumentoProfissional status
) {

    public CriarDocumentoProfissionalCommand paraCommand() {
        return new CriarDocumentoProfissionalCommand(
                empresaId,
                clientePacienteId,
                profissionalId,
                profissionalNome,
                titulo,
                tipo,
                conteudo,
                status
        );
    }
}
