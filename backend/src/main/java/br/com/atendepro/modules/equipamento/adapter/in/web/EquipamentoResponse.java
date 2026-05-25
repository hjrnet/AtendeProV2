package br.com.atendepro.modules.equipamento.adapter.in.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.equipamento.application.result.EquipamentoResult;

public record EquipamentoResponse(
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

    public static EquipamentoResponse de(EquipamentoResult result) {
        return new EquipamentoResponse(
                result.id(),
                result.empresaId(),
                result.nome(),
                result.categoria(),
                result.marca(),
                result.modelo(),
                result.numeroSerie(),
                result.valorAquisicao(),
                result.dataAquisicao(),
                result.vidaUtilMeses(),
                result.proximaManutencaoEm(),
                result.descricaoManutencao(),
                result.ativo(),
                result.criadoEm(),
                result.atualizadoEm()
        );
    }
}
