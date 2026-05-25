package br.com.atendepro.modules.spaces.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.DisponibilidadeSpacesResult;

public record DisponibilidadeSpacesResponse(
        UUID empresaId,
        UUID recursoId,
        Instant inicioEm,
        Instant fimEm,
        boolean disponivel,
        String motivo
) {

    static DisponibilidadeSpacesResponse de(DisponibilidadeSpacesResult result) {
        return new DisponibilidadeSpacesResponse(
                result.empresaId(),
                result.recursoId(),
                result.inicioEm(),
                result.fimEm(),
                result.disponivel(),
                result.motivo()
        );
    }
}
