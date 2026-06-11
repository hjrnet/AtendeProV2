package br.com.atendepro.modules.estoque.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CadastrarProdutoEstoqueCommand(
        UUID empresaId,
        String nome,
        String categoria,
        String lote,
        LocalDate validade,
        String fornecedorNome,
        String fornecedorDocumento,
        String numeroPedidoCompra,
        LocalDate dataCompra,
        String statusCompra,
        String unidade,
        BigDecimal quantidadeAtual,
        BigDecimal custoUnitario,
        BigDecimal estoqueMinimo
) {
}
