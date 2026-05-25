package br.com.atendepro.modules.spaces.application.result;

import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.RecursoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoRecursoSpaces;

public record RecursoSpacesResult(
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

    public static RecursoSpacesResult de(RecursoSpaces recurso) {
        return new RecursoSpacesResult(
                recurso.id(),
                recurso.empresaId(),
                recurso.nome(),
                recurso.tipo(),
                recurso.descricao(),
                recurso.capacidadePessoas(),
                recurso.localizacao(),
                recurso.ativo(),
                recurso.criadoEm(),
                recurso.atualizadoEm()
        );
    }
}
