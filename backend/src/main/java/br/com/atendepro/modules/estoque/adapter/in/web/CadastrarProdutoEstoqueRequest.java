package br.com.atendepro.modules.estoque.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.estoque.application.command.CadastrarProdutoEstoqueCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastrarProdutoEstoqueRequest(
        UUID empresaId,
        @NotBlank @Size(max = 160) String nome,
        @Size(max = 120) String categoria,
        @Size(max = 120) String lote,
        LocalDate validade,
        @NotBlank @Size(max = 30) String unidade,
        @NotNull @DecimalMin("0.000") BigDecimal quantidadeAtual,
        @NotNull @DecimalMin("0.00") BigDecimal custoUnitario,
        @DecimalMin("0.000") BigDecimal estoqueMinimo
) {

    public CadastrarProdutoEstoqueCommand paraCommand() {
        return new CadastrarProdutoEstoqueCommand(
                empresaId,
                nome,
                categoria,
                lote,
                validade,
                unidade,
                quantidadeAtual,
                custoUnitario,
                estoqueMinimo == null ? BigDecimal.ZERO : estoqueMinimo
        );
    }
}
