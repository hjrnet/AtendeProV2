package br.com.atendepro.modules.spaces.application.command;

import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;

public record CadastrarRecursoSpacesCommand(
        UUID empresaId,
        String nome,
        TipoRecursoSpaces tipo,
        String descricao,
        int capacidadePessoas,
        String localizacao
) {
}
