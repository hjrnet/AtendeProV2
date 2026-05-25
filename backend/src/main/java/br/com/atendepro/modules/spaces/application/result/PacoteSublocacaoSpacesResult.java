package br.com.atendepro.modules.spaces.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.PacoteSublocacaoSpaces;
import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;

public record PacoteSublocacaoSpacesResult(
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

    public static PacoteSublocacaoSpacesResult de(PacoteSublocacaoSpaces pacote) {
        return new PacoteSublocacaoSpacesResult(
                pacote.id(),
                pacote.empresaId(),
                pacote.recursoId(),
                pacote.nome(),
                pacote.tipo(),
                pacote.descricao(),
                pacote.duracaoHoras(),
                pacote.valorFixo(),
                pacote.percentualReceita(),
                pacote.ativo(),
                pacote.criadoEm(),
                pacote.atualizadoEm()
        );
    }
}
