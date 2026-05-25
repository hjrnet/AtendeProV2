package br.com.atendepro.modules.beauty.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.command.VincularProdutoBeautyProCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VincularProdutoBeautyProRequest(
        UUID protocoloId,
        UUID sessaoId,
        UUID produtoEstoqueId,
        String nomeProduto,
        String lote,
        LocalDate validade,
        @NotNull @DecimalMin(value = "0.001") BigDecimal quantidade,
        @NotBlank String unidade,
        String observacoes
) {
    public VincularProdutoBeautyProCommand paraCommand(UUID empresaId, UUID clienteId) {
        return new VincularProdutoBeautyProCommand(
                empresaId,
                clienteId,
                protocoloId,
                sessaoId,
                produtoEstoqueId,
                nomeProduto,
                lote,
                validade,
                quantidade,
                unidade,
                observacoes
        );
    }
}
