package br.com.atendepro.modules.equipamento.application.command;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CadastrarEquipamentoCommand(
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
        String descricaoManutencao
) {
}
