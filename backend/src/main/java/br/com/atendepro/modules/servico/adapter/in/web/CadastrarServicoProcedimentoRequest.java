package br.com.atendepro.modules.servico.adapter.in.web;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.atendepro.modules.cliente.domain.model.AreaCliente;
import br.com.atendepro.modules.servico.application.command.CadastrarServicoProcedimentoCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastrarServicoProcedimentoRequest(
        UUID empresaId,
        @NotBlank @Size(max = 160) String nome,
        @Size(max = 1000) String descricao,
        AreaCliente area,
        @Min(1) int duracaoMinutos,
        @NotNull @DecimalMin("0.00") BigDecimal precoBase
) {

    public CadastrarServicoProcedimentoCommand paraCommand() {
        return new CadastrarServicoProcedimentoCommand(
                empresaId,
                nome,
                descricao,
                area == null ? AreaCliente.GERAL : area,
                duracaoMinutos,
                precoBase
        );
    }
}
