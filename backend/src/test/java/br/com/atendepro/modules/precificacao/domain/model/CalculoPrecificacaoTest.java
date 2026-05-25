package br.com.atendepro.modules.precificacao.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CalculoPrecificacaoTest {

    @Test
    void deveCalcularCustoTotalBaseComItensNormalizados() {
        CalculoPrecificacao calculo = CalculoPrecificacao.calcular(
                UUID.randomUUID(),
                UUID.randomUUID(),
                " Consulta Nutricional ",
                List.of(
                        new ItemCustoPrecificacao("Insumos", CategoriaItemPrecificacao.INSUMO, new BigDecimal("35.499")),
                        new ItemCustoPrecificacao("Sala", CategoriaItemPrecificacao.SALA, new BigDecimal("50.00"))
                ),
                Instant.parse("2026-05-25T00:00:00Z")
        );

        assertThat(calculo.nomeProcedimento()).isEqualTo("Consulta Nutricional");
        assertThat(calculo.custoTotal()).isEqualByComparingTo("85.50");
        assertThat(calculo.itensCusto()).extracting("descricao").containsExactly("Insumos", "Sala");
    }

    @Test
    void naoDeveCalcularSemItensDeCusto() {
        assertThatThrownBy(() -> CalculoPrecificacao.calcular(
                UUID.randomUUID(),
                null,
                "Procedimento",
                List.of(),
                Instant.parse("2026-05-25T00:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("itens de custo da precificacao sao obrigatorios");
    }

    @Test
    void naoDeveAceitarValorNegativoNoItem() {
        assertThatThrownBy(() -> new ItemCustoPrecificacao(
                "Taxa",
                CategoriaItemPrecificacao.TAXA,
                new BigDecimal("-1.00")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("valor do item de custo nao pode ser negativo");
    }
}
