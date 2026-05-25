package br.com.atendepro.modules.custo.application.command;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

import br.com.atendepro.modules.custo.domain.model.TipoCustoGeral;

public record CadastrarCustoGeralCommand(
        UUID empresaId,
        String descricao,
        TipoCustoGeral tipo,
        String categoria,
        BigDecimal valor,
        YearMonth competencia
) {
}
