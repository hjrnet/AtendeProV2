package br.com.atendepro.modules.spaces.adapter.in.web;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.RecursoSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;

public record RecursoSpacesResponse(
        UUID id,
        UUID empresaId,
        String nome,
        TipoRecursoSpaces tipo,
        String descricao,
        int capacidadePessoas,
        String localizacao,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    static RecursoSpacesResponse de(RecursoSpacesResult result) {
        return new RecursoSpacesResponse(
                result.id(),
                result.empresaId(),
                result.nome(),
                result.tipo(),
                result.descricao(),
                result.capacidadePessoas(),
                result.localizacao(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
