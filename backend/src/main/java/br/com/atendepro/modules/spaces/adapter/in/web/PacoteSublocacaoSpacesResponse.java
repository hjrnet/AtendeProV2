package br.com.atendepro.modules.spaces.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.application.result.PacoteSublocacaoSpacesResult;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;

public record PacoteSublocacaoSpacesResponse(
        UUID id,
        UUID empresaId,
        UUID recursoId,
        String nome,
        TipoPacoteSublocacaoSpaces tipo,
        String descricao,
        BigDecimal duracaoHoras,
        BigDecimal valorFixo,
        BigDecimal percentualReceita,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    static PacoteSublocacaoSpacesResponse de(PacoteSublocacaoSpacesResult result) {
        return new PacoteSublocacaoSpacesResponse(
                result.id(),
                result.empresaId(),
                result.recursoId(),
                result.nome(),
                result.tipo(),
                result.descricao(),
                result.duracaoHoras(),
                result.valorFixo(),
                result.percentualReceita(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
