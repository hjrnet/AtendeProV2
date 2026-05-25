package br.com.atendepro.modules.spaces.application.result;

import java.util.List;

import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;

public record SpacesStatusResult(
        String produto,
        String release,
        String status,
        List<TipoRecursoSpaces> tiposRecurso,
        List<String> capacidades
) {
}
