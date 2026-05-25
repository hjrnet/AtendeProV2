package br.com.atendepro.modules.beauty.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.beauty.application.result.ProdutoBeautyEstoqueResult;

public record ProdutoBeautyEstoqueResponse(
        UUID id,
        String nome,
        String categoria,
        String lote,
        LocalDate validade,
        String unidade,
        BigDecimal quantidadeAtual,
        BigDecimal estoqueMinimo,
        boolean estoqueBaixo,
        boolean validadeEmAlerta
) {
    public static ProdutoBeautyEstoqueResponse de(ProdutoBeautyEstoqueResult result) {
        return new ProdutoBeautyEstoqueResponse(
                result.id(),
                result.nome(),
                result.categoria(),
                result.lote(),
                result.validade(),
                result.unidade(),
                result.quantidadeAtual(),
                result.estoqueMinimo(),
                result.estoqueBaixo(),
                result.validadeEmAlerta()
        );
    }
}
