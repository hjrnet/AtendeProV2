package br.com.atendepro.modules.spaces.adapter.in.web;

import java.util.List;

import br.com.atendepro.modules.spaces.application.result.SpacesStatusResult;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;

public record SpacesStatusResponse(
        String produto,
        String release,
        String status,
        List<TipoRecursoSpaces> tiposRecurso,
        List<String> capacidades
) {

    static SpacesStatusResponse de(SpacesStatusResult result) {
        return new SpacesStatusResponse(
                result.produto(),
                result.release(),
                result.status(),
                result.tiposRecurso(),
                result.capacidades()
        );
    }
}
