package br.com.atendepro.modules.equipamento.adapter.in.web;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import br.com.atendepro.modules.equipamento.application.command.CadastrarEquipamentoCommand;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CadastrarEquipamentoRequest(
        UUID empresaId,
        @NotBlank @Size(max = 160) String nome,
        @Size(max = 120) String categoria,
        @Size(max = 120) String marca,
        @Size(max = 120) String modelo,
        @Size(max = 120) String numeroSerie,
        @NotNull @DecimalMin("0.00") BigDecimal valorAquisicao,
        LocalDate dataAquisicao,
        @NotNull @Min(1) Integer vidaUtilMeses,
        LocalDate proximaManutencaoEm,
        @Size(max = 1000) String descricaoManutencao
) {

    public CadastrarEquipamentoCommand paraCommand() {
        return new CadastrarEquipamentoCommand(
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
                descricaoManutencao
        );
    }
}
