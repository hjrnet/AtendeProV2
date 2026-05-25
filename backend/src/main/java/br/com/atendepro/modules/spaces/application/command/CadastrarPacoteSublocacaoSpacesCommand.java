package br.com.atendepro.modules.spaces.application.command;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.spaces.domain.model.TipoPacoteSublocacaoSpaces;

public record CadastrarPacoteSublocacaoSpacesCommand(
        UUID empresaId,
        UUID recursoId,
        String nome,
        TipoPacoteSublocacaoSpaces tipo,
        String descricao,
        BigDecimal duracaoHoras,
        BigDecimal valorFixo,
        BigDecimal percentualReceita
) {
}
