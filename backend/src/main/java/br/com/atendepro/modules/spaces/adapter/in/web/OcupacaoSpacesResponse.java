package br.com.atendepro.modules.spaces.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.OcupacaoSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.StatusOcupacaoSpaces;

public record OcupacaoSpacesResponse(
        UUID id,
        UUID empresaId,
        UUID recursoId,
        UUID pacoteId,
        String nomeParceiro,
        Instant inicioEm,
        Instant fimEm,
        StatusOcupacaoSpaces status,
        String observacao,
        Instant criadoEm,
        Instant atualizadoEm
) {

    static OcupacaoSpacesResponse de(OcupacaoSpacesResult result) {
        return new OcupacaoSpacesResponse(
                result.id(),
                result.empresaId(),
                result.recursoId(),
                result.pacoteId(),
                result.nomeParceiro(),
                result.inicioEm(),
                result.fimEm(),
                result.status(),
                result.observacao(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
