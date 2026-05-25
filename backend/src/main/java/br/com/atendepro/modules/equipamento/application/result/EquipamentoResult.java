package br.com.atendepro.modules.equipamento.application.result;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.equipamento.domain.model.Equipamento;

public record EquipamentoResult(
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

    public static EquipamentoResult de(Equipamento equipamento) {
        return new EquipamentoResult(
                equipamento.id(),
                equipamento.empresaId(),
                equipamento.nome(),
                equipamento.categoria(),
                equipamento.marca(),
                equipamento.modelo(),
                equipamento.numeroSerie(),
                equipamento.valorAquisicao(),
                equipamento.dataAquisicao(),
                equipamento.vidaUtilMeses(),
                equipamento.proximaManutencaoEm(),
                equipamento.descricaoManutencao(),
                equipamento.ativo(),
                equipamento.criadoEm(),
                equipamento.atualizadoEm()
        );
    }
}
