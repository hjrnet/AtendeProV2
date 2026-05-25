package br.com.atendepro.modules.spaces.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class SimulacaoParceiroSpacesTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b79ef2fd-bd18-4788-a565-62fd50901982");
    private static final Instant AGORA = Instant.parse("2026-05-25T00:00:00Z");

    @Test
    void deveCalcularLucroDoParceiroComPacoteFixo() {
        var simulacao = SimulacaoParceiroSpaces.calcular(
                pacote(TipoPacoteSublocacaoSpaces.HORA, new BigDecimal("80.00"), BigDecimal.ZERO),
                40,
                80,
                new BigDecimal("180.00"),
                new BigDecimal("2500.00")
        );

        assertThat(simulacao.receitaBrutaMensal()).isEqualByComparingTo("14400.00");
        assertThat(simulacao.custoFixoSublocacao()).isEqualByComparingTo("3200.00");
        assertThat(simulacao.custoTotalSublocacao()).isEqualByComparingTo("3200.00");
        assertThat(simulacao.lucroEstimadoParceiro()).isEqualByComparingTo("8700.00");
        assertThat(simulacao.margemParceiroPercentual()).isEqualByComparingTo("60.42");
        assertThat(simulacao.status()).isEqualTo(StatusSimulacaoParceiroSpaces.SAUDAVEL);
    }

    @Test
    void deveCalcularCustoPercentualDaReceita() {
        var simulacao = SimulacaoParceiroSpaces.calcular(
                pacote(TipoPacoteSublocacaoSpaces.FIXO_PERCENTUAL, new BigDecimal("900.00"), new BigDecimal("10.00")),
                1,
                40,
                new BigDecimal("200.00"),
                new BigDecimal("1200.00")
        );

        assertThat(simulacao.custoFixoSublocacao()).isEqualByComparingTo("900.00");
        assertThat(simulacao.custoPercentualSublocacao()).isEqualByComparingTo("800.00");
        assertThat(simulacao.custoTotalSublocacao()).isEqualByComparingTo("1700.00");
    }

    @Test
    void naoDeveSimularSemAtendimentos() {
        assertThatThrownBy(() -> SimulacaoParceiroSpaces.calcular(
                pacote(TipoPacoteSublocacaoSpaces.HORA, new BigDecimal("80.00"), BigDecimal.ZERO),
                1,
                0,
                new BigDecimal("180.00"),
                BigDecimal.ZERO
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("atendimentos no mes devem ser positivos");
    }

    private PacoteSublocacaoSpaces pacote(TipoPacoteSublocacaoSpaces tipo, BigDecimal valorFixo, BigDecimal percentual) {
        return PacoteSublocacaoSpaces.cadastrar(
                EMPRESA_ID,
                null,
                "Pacote teste",
                tipo,
                null,
                new BigDecimal("1.00"),
                valorFixo,
                percentual,
                AGORA
        );
    }
}
