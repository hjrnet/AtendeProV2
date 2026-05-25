package br.com.atendepro.modules.precificacao.adapter.out.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;

class PdfBoxRelatorioPrecificacaoAdapterTest {

    @Test
    void deveGerarPdfDaSimulacaoPrecificacao() {
        PdfBoxRelatorioPrecificacaoAdapter adapter = new PdfBoxRelatorioPrecificacaoAdapter();

        var result = adapter.gerarRelatorio(simulacao());

        assertThat(result.nomeArquivo()).startsWith("precificacao-consulta-profissional-");
        assertThat(result.nomeArquivo()).endsWith(".pdf");
        assertThat(result.contentType()).isEqualTo("application/pdf");
        assertThat(new String(result.conteudo(), 0, 4)).isEqualTo("%PDF");
        assertThat(result.conteudo().length).isGreaterThan(800);
    }

    private SimulacaoPrecificacao simulacao() {
        return new SimulacaoPrecificacao(
                UUID.fromString("3ba8d338-ce0d-4c4c-850c-6960d81fe82e"),
                UUID.fromString("7b869806-f196-488c-bc11-937c4f5b2f38"),
                null,
                "Consulta profissional",
                60,
                new BigDecimal("168.00"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("30.00"),
                new BigDecimal("240.00"),
                new BigDecimal("168.00"),
                new BigDecimal("168.00"),
                new BigDecimal("240.00"),
                new BigDecimal("72.00"),
                new BigDecimal("30.00"),
                StatusMargemPrecificacao.SAUDAVEL,
                true,
                Instant.parse("2026-05-25T00:00:00Z"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }
}
