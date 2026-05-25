package br.com.atendepro.modules.beauty.application.result;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ProdutoBeautyEstoqueResult(
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
}
