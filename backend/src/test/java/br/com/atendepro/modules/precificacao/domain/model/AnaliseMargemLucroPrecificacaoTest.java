package br.com.atendepro.modules.precificacao.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class AnaliseMargemLucroPrecificacaoTest {

    @Test
    void deveCalcularLucroMargemEStatusSaudavel() {
        AnaliseMargemLucroPrecificacao analise = AnaliseMargemLucroPrecificacao.analisar(
                precoMinimo("168.00"),
                new BigDecimal("240.00")
        );

        assertThat(analise.lucroEstimado()).isEqualByComparingTo("72.00");
        assertThat(analise.margemRealPercentual()).isEqualByComparingTo("30.00");
        assertThat(analise.status()).isEqualTo(StatusMargemPrecificacao.SAUDAVEL);
        assertThat(analise.alertas()).isEmpty();
    }

    @Test
    void deveAlertarPrecoAbaixoDoMinimo() {
        AnaliseMargemLucroPrecificacao analise = AnaliseMargemLucroPrecificacao.analisar(
                precoMinimo("168.00"),
                new BigDecimal("150.00")
        );

        assertThat(analise.lucroEstimado()).isEqualByComparingTo("-18.00");
        assertThat(analise.status()).isEqualTo(StatusMargemPrecificacao.PREJUIZO);
        assertThat(analise.alertas()).extracting("codigo").containsExactly("PRECO_ABAIXO_DO_MINIMO");
    }

    @Test
    void deveAlertarMargemBaixa() {
        AnaliseMargemLucroPrecificacao analise = AnaliseMargemLucroPrecificacao.analisar(
                precoMinimo("168.00"),
                new BigDecimal("200.00")
        );

        assertThat(analise.margemRealPercentual()).isEqualByComparingTo("16.00");
        assertThat(analise.status()).isEqualTo(StatusMargemPrecificacao.MARGEM_BAIXA);
        assertThat(analise.alertas()).extracting("codigo").containsExactly("MARGEM_BAIXA");
    }

    @Test
    void naoDeveCalcularComPrecoVendaZero() {
        assertThatThrownBy(() -> AnaliseMargemLucroPrecificacao.analisar(
                precoMinimo("168.00"),
                BigDecimal.ZERO
        )).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("preco de venda deve ser positivo");
    }

    private PrecoMinimoPrecificacao precoMinimo(String custo) {
        CustoRealPrecificacao custoReal = CustoRealPrecificacao.calcular(
                UUID.randomUUID(),
                null,
                "Procedimento",
                60,
                new BigDecimal(custo),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                Instant.parse("2026-05-25T00:00:00Z")
        );
        return PrecoMinimoPrecificacao.calcular(custoReal);
    }
}
