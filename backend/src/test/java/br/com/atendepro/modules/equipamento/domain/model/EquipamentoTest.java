package br.com.atendepro.modules.equipamento.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class EquipamentoTest {

    @Test
    void deveCadastrarEquipamentoComValorVidaUtilEManutencao() {
        Equipamento equipamento = Equipamento.cadastrar(
                UUID.randomUUID(),
                " Autoclave ",
                " Esterilizacao ",
                "Marca A",
                "Modelo B",
                " SN-123 ",
                new BigDecimal("3500.995"),
                LocalDate.parse("2026-01-10"),
                60,
                LocalDate.parse("2026-06-30"),
                " Revisao preventiva ",
                Instant.parse("2026-05-25T00:00:00Z")
        );

        assertThat(equipamento.nome()).isEqualTo("Autoclave");
        assertThat(equipamento.categoria()).isEqualTo("Esterilizacao");
        assertThat(equipamento.numeroSerie()).isEqualTo("SN-123");
        assertThat(equipamento.valorAquisicao()).isEqualByComparingTo("3501.00");
        assertThat(equipamento.vidaUtilMeses()).isEqualTo(60);
        assertThat(equipamento.descricaoManutencao()).isEqualTo("Revisao preventiva");
        assertThat(equipamento.ativo()).isTrue();
    }

    @Test
    void naoDeveCadastrarVidaUtilZerada() {
        assertThatThrownBy(() -> Equipamento.cadastrar(
                UUID.randomUUID(),
                "Autoclave",
                "Esterilizacao",
                "Marca A",
                "Modelo B",
                "SN-123",
                new BigDecimal("3500.00"),
                LocalDate.parse("2026-01-10"),
                0,
                LocalDate.parse("2026-06-30"),
                "Revisao",
                Instant.parse("2026-05-25T00:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("vida util do equipamento deve ser positiva");
    }
}
