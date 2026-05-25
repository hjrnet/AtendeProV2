package br.com.atendepro.modules.estoque.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProdutoEstoque(
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

    public ProdutoEstoque {
        if (id == null) {
            throw new IllegalArgumentException("id do produto de estoque e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do produto de estoque e obrigatoria");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome do produto de estoque e obrigatorio");
        }
        if (unidade == null || unidade.isBlank()) {
            throw new IllegalArgumentException("unidade do produto de estoque e obrigatoria");
        }
        if (quantidadeAtual == null || quantidadeAtual.signum() < 0) {
            throw new IllegalArgumentException("quantidade atual do produto de estoque nao pode ser negativa");
        }
        if (custoUnitario == null || custoUnitario.signum() < 0) {
            throw new IllegalArgumentException("custo unitario do produto de estoque nao pode ser negativo");
        }
        if (estoqueMinimo == null) {
            estoqueMinimo = BigDecimal.ZERO;
        }
        if (estoqueMinimo.signum() < 0) {
            throw new IllegalArgumentException("estoque minimo do produto de estoque nao pode ser negativo");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do produto de estoque sao obrigatorias");
        }
        nome = nome.trim();
        categoria = normalizarTextoOpcional(categoria);
        lote = normalizarTextoOpcional(lote);
        unidade = unidade.trim().toUpperCase();
        quantidadeAtual = quantidadeAtual.setScale(3, RoundingMode.HALF_EVEN);
        custoUnitario = custoUnitario.setScale(2, RoundingMode.HALF_EVEN);
        estoqueMinimo = estoqueMinimo.setScale(3, RoundingMode.HALF_EVEN);
    }

    public static ProdutoEstoque cadastrar(
            UUID empresaId,
            String nome,
            String categoria,
            String lote,
            LocalDate validade,
            String unidade,
            BigDecimal quantidadeAtual,
            BigDecimal custoUnitario,
            BigDecimal estoqueMinimo,
            Instant agora
    ) {
        return new ProdutoEstoque(
                UUID.randomUUID(),
                empresaId,
                nome,
                categoria,
                lote,
                validade,
                unidade,
                quantidadeAtual,
                custoUnitario,
                estoqueMinimo,
                true,
                agora,
                agora
        );
    }

    private static String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
