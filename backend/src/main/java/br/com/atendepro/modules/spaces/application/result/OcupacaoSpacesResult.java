package br.com.atendepro.modules.spaces.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.OcupacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.StatusOcupacaoSpaces;

public record OcupacaoSpacesResult(
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

    public static OcupacaoSpacesResult de(OcupacaoSpaces ocupacao) {
        return new OcupacaoSpacesResult(
                ocupacao.id(),
                ocupacao.empresaId(),
                ocupacao.recursoId(),
                ocupacao.pacoteId(),
                ocupacao.nomeParceiro(),
                ocupacao.inicioEm(),
                ocupacao.fimEm(),
                ocupacao.status(),
                ocupacao.observacao(),
                ocupacao.criadoEm(),
                ocupacao.atualizadoEm()
        );
    }
}
