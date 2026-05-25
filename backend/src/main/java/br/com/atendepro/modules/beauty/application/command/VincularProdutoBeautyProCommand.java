package br.com.atendepro.modules.beauty.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record VincularProdutoBeautyProCommand(
        UUID empresaId,
        UUID clienteId,
        UUID protocoloId,
        UUID sessaoId,
        UUID produtoEstoqueId,
        String nomeProduto,
        String lote,
        LocalDate validade,
        BigDecimal quantidade,
        String unidade,
        String observacoes
) {
}
