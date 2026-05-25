package br.com.atendepro.modules.custo.adapter.in.web;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

import br.com.atendepro.modules.custo.application.command.CadastrarCustoGeralCommand;
import br.com.atendepro.modules.custo.domain.model.TipoCustoGeral;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastrarCustoGeralRequest(
        UUID empresaId,
        @NotBlank @Size(max = 180) String descricao,
        @NotNull TipoCustoGeral tipo,
        @Size(max = 120) String categoria,
        @NotNull @DecimalMin("0.00") BigDecimal valor,
        YearMonth competencia
) {

    public CadastrarCustoGeralCommand paraCommand() {
        return new CadastrarCustoGeralCommand(empresaId, descricao, tipo, categoria, valor, competencia);
    }
}
