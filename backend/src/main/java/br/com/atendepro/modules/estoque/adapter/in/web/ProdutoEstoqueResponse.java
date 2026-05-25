package br.com.atendepro.modules.estoque.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.estoque.application.result.ProdutoEstoqueResult;

public record ProdutoEstoqueResponse(
        UUID id,
        UUID empresaId,
        String nome,
        String categoria,
        String lote,
        LocalDate validade,
        String unidade,
        BigDecimal quantidadeAtual,
        BigDecimal custoUnitario,
        BigDecimal estoqueMinimo,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public static ProdutoEstoqueResponse de(ProdutoEstoqueResult result) {
        return new ProdutoEstoqueResponse(
                result.id(),
                result.empresaId(),
                result.nome(),
                result.categoria(),
                result.lote(),
                result.validade(),
                result.unidade(),
                result.quantidadeAtual(),
                result.custoUnitario(),
                result.estoqueMinimo(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
