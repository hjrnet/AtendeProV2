package br.com.atendepro.modules.beauty.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProdutoUtilizadoBeautyPro(
        UUID id,
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
        boolean alertaValidade,
        boolean alertaEstoqueBaixo,
        String observacoes,
        Instant criadoEm
) {

    public ProdutoUtilizadoBeautyPro {
        if (id == null) {
            throw new IllegalArgumentException("id do produto Beauty e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do produto Beauty e obrigatoria");
        }
        if (clienteId == null) {
            throw new IllegalArgumentException("cliente do produto Beauty e obrigatorio");
        }
        if (nomeProduto == null || nomeProduto.isBlank()) {
            throw new IllegalArgumentException("nome do produto Beauty e obrigatorio");
        }
        if (quantidade == null || quantidade.signum() <= 0) {
            throw new IllegalArgumentException("quantidade do produto Beauty deve ser positiva");
        }
        if (unidade == null || unidade.isBlank()) {
            throw new IllegalArgumentException("unidade do produto Beauty e obrigatoria");
        }
        if (criadoEm == null) {
            throw new IllegalArgumentException("data do produto Beauty e obrigatoria");
        }
        nomeProduto = nomeProduto.trim();
        lote = normalizarTextoOpcional(lote);
        unidade = unidade.trim().toUpperCase();
        quantidade = quantidade.setScale(3, RoundingMode.HALF_EVEN);
        observacoes = normalizarTextoOpcional(observacoes);
    }

    public static ProdutoUtilizadoBeautyPro vincular(
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
            boolean estoqueBaixo,
            String observacoes,
            LocalDate hoje,
            Instant agora
    ) {
        boolean validadeEmAlerta = validade != null && !validade.isAfter(hoje.plusDays(30));
        return new ProdutoUtilizadoBeautyPro(
                UUID.randomUUID(),
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
                validadeEmAlerta,
                estoqueBaixo,
                observacoes,
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
