package br.com.atendepro.modules.custo.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.custo.application.command.CadastrarCustoAlimentacaoTransporteCommand;
import br.com.atendepro.modules.custo.domain.model.PeriodicidadeCustoPessoal;
import br.com.atendepro.modules.custo.domain.model.TipoCustoPessoal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastrarCustoAlimentacaoTransporteRequest(
        UUID empresaId,
        UUID profissionalId,
        @NotBlank @Size(max = 180) String descricao,
        @NotNull TipoCustoPessoal tipo,
        @NotNull PeriodicidadeCustoPessoal periodicidade,
        @NotNull @DecimalMin("0.00") BigDecimal valor
) {

    public CadastrarCustoAlimentacaoTransporteCommand paraCommand() {
        return new CadastrarCustoAlimentacaoTransporteCommand(
                empresaId,
                profissionalId,
                descricao,
                tipo,
                periodicidade,
                valor
        );
    }
}
