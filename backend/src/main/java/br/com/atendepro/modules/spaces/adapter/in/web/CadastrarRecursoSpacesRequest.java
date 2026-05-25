package br.com.atendepro.modules.spaces.adapter.in.web;

import java.util.UUID;

import br.com.atendepro.modules.spaces.application.command.CadastrarRecursoSpacesCommand;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastrarRecursoSpacesRequest(
        UUID empresaId,
        @NotBlank @Size(max = 160) String nome,
        @NotNull TipoRecursoSpaces tipo,
        @Size(max = 1000) String descricao,
        @Min(1) int capacidadePessoas,
        @Size(max = 160) String localizacao
) {

    CadastrarRecursoSpacesCommand paraCommand() {
        return new CadastrarRecursoSpacesCommand(
                empresaId,
                nome,
                tipo,
                descricao,
                capacidadePessoas,
                localizacao
        );
    }
}
