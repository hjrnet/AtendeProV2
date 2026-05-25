package br.com.atendepro.modules.custo.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CustoAlimentacaoTransporteTest {

    @Test
    void deveCadastrarCustoDeTransporte() {
        CustoAlimentacaoTransporte custo = CustoAlimentacaoTransporte.cadastrar(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Deslocamento urbano",
                TipoCustoPessoal.TRANSPORTE,
                PeriodicidadeCustoPessoal.POR_ATENDIMENTO,
                new BigDecimal("39.995"),
                Instant.parse("2026-05-25T00:00:00Z")
        );

        assertThat(custo.valor()).isEqualByComparingTo("40.00");
        assertThat(custo.ativo()).isTrue();
    }

    @Test
    void naoDeveCadastrarValorNegativo() {
        assertThatThrownBy(() -> CustoAlimentacaoTransporte.cadastrar(
                UUID.randomUUID(),
                null,
                "Almoco",
                TipoCustoPessoal.ALIMENTACAO,
                PeriodicidadeCustoPessoal.DIARIO,
                new BigDecimal("-1.00"),
                Instant.parse("2026-05-25T00:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("valor do custo pessoal nao pode ser negativo");
    }
}
