package br.com.atendepro.modules.custo.application.command;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.custo.domain.model.PeriodicidadeCustoPessoal;
import br.com.atendepro.modules.custo.domain.model.TipoCustoPessoal;

public record CadastrarCustoAlimentacaoTransporteCommand(
        UUID empresaId,
        UUID profissionalId,
        String descricao,
        TipoCustoPessoal tipo,
        PeriodicidadeCustoPessoal periodicidade,
        BigDecimal valor
) {
}
