package br.com.atendepro.modules.estoque.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.estoque.domain.model.ProdutoEstoque;

public record ProdutoEstoqueResult(
        UUID id,
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
        BigDecimal estoqueMinimo,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static ProdutoEstoqueResult de(ProdutoEstoque produto) {
        return new ProdutoEstoqueResult(
                produto.id(),
                produto.empresaId(),
                produto.nome(),
                produto.categoria(),
                produto.lote(),
                produto.validade(),
                produto.fornecedorNome(),
                produto.fornecedorDocumento(),
                produto.numeroPedidoCompra(),
                produto.dataCompra(),
                produto.statusCompra(),
                produto.unidade(),
                produto.quantidadeAtual(),
                produto.custoUnitario(),
                produto.estoqueMinimo(),
                produto.ativo(),
                produto.criadoEm(),
                produto.atualizadoEm()
        );
    }
}
