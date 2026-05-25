package br.com.atendepro.modules.documento.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.documento.application.command.CriarCarimboProfissionalCommand;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CriarCarimboProfissionalRequest(
        UUID empresaId,
        UUID profissionalId,
        @NotBlank @Size(max = 160) String profissionalNome,
        @NotNull ConselhoProfissional conselho,
        @NotBlank @Size(min = 2, max = 2) String uf,
        @NotBlank @Size(max = 40) String numeroRegistro,
        @NotBlank @Size(max = 500) String assinaturaTexto,
        @NotBlank @Size(max = 160) String clinicaNome
) {

    public CriarCarimboProfissionalCommand paraCommand() {
        return new CriarCarimboProfissionalCommand(
                empresaId,
                profissionalId,
                profissionalNome,
                conselho,
                uf,
                numeroRegistro,
                assinaturaTexto,
                clinicaNome
        );
    }
}
