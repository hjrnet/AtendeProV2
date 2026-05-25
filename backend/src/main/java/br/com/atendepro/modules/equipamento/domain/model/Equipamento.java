package br.com.atendepro.modules.equipamento.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Equipamento(
        UUID id,
        UUID empresaId,
        String nome,
        String categoria,
        String marca,
        String modelo,
        String numeroSerie,
        BigDecimal valorAquisicao,
        LocalDate dataAquisicao,
        int vidaUtilMeses,
        LocalDate proximaManutencaoEm,
        String descricaoManutencao,
        boolean ativo,
        Instant criadoEm,
        Instant atualizadoEm
) {

    public Equipamento {
        if (id == null) {
            throw new IllegalArgumentException("id do equipamento e obrigatorio");
        }
        if (empresaId == null) {
            throw new IllegalArgumentException("empresa do equipamento e obrigatoria");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome do equipamento e obrigatorio");
        }
        if (valorAquisicao == null || valorAquisicao.signum() < 0) {
            throw new IllegalArgumentException("valor de aquisicao do equipamento nao pode ser negativo");
        }
        if (vidaUtilMeses <= 0) {
            throw new IllegalArgumentException("vida util do equipamento deve ser positiva");
        }
        if (criadoEm == null || atualizadoEm == null) {
            throw new IllegalArgumentException("datas do equipamento sao obrigatorias");
        }
        nome = nome.trim();
        categoria = normalizarTextoOpcional(categoria);
        marca = normalizarTextoOpcional(marca);
        modelo = normalizarTextoOpcional(modelo);
        numeroSerie = normalizarTextoOpcional(numeroSerie);
        descricaoManutencao = normalizarTextoOpcional(descricaoManutencao);
        valorAquisicao = valorAquisicao.setScale(2, RoundingMode.HALF_EVEN);
    }

    public static Equipamento cadastrar(
            UUID empresaId,
            String nome,
            String categoria,
            String marca,
            String modelo,
            String numeroSerie,
            BigDecimal valorAquisicao,
            LocalDate dataAquisicao,
            int vidaUtilMeses,
            LocalDate proximaManutencaoEm,
            String descricaoManutencao,
            Instant agora
    ) {
        return new Equipamento(
                UUID.randomUUID(),
                empresaId,
                nome,
                categoria,
                marca,
                modelo,
                numeroSerie,
                valorAquisicao,
                dataAquisicao,
                vidaUtilMeses,
                proximaManutencaoEm,
                descricaoManutencao,
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
