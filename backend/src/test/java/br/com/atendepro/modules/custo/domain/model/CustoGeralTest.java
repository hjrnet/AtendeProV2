package br.com.atendepro.modules.custo.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class CustoGeralTest {

    @Test
    void deveCadastrarCustoGeral() {
        CustoGeral custo = CustoGeral.cadastrar(
                UUID.randomUUID(),
                " Aluguel ",
                TipoCustoGeral.FIXO,
                "Estrutura",
                new BigDecimal("1000.005"),
                YearMonth.parse("2026-05"),
                Instant.parse("2026-05-25T00:00:00Z")
        );

        assertThat(custo.descricao()).isEqualTo("Aluguel");
        assertThat(custo.valor()).isEqualByComparingTo("1000.00");
        assertThat(custo.ativo()).isTrue();
    }

    @Test
    void naoDeveCadastrarValorNegativo() {
        assertThatThrownBy(() -> CustoGeral.cadastrar(
                UUID.randomUUID(),
                "Taxa",
                TipoCustoGeral.EVENTUAL,
                null,
                new BigDecimal("-1.00"),
                null,
                Instant.parse("2026-05-25T00:00:00Z")
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("valor do custo nao pode ser negativo");
    }
}
