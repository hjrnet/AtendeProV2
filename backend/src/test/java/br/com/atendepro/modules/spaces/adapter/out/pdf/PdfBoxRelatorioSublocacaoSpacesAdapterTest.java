package br.com.atendepro.modules.spaces.adapter.out.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.spaces.application.result.IndicadoresSublocacaoSpacesResult;

class PdfBoxRelatorioSublocacaoSpacesAdapterTest {

    @Test
    void deveGerarPdfDoRelatorioDeSublocacao() {
        PdfBoxRelatorioSublocacaoSpacesAdapter adapter = new PdfBoxRelatorioSublocacaoSpacesAdapter();

        var result = adapter.gerarPdf(indicadores());

        assertThat(result.nomeArquivo()).isEqualTo("spaces-sublocacao-202605.pdf");
        assertThat(result.contentType()).isEqualTo("application/pdf");
        assertThat(new String(result.conteudo(), 0, 4)).isEqualTo("%PDF");
        assertThat(result.conteudo().length).isGreaterThan(800);
    }

    private IndicadoresSublocacaoSpacesResult indicadores() {
        return new IndicadoresSublocacaoSpacesResult(
                UUID.fromString("0e24d9e4-0450-4324-8993-6d34aa96f5b9"),
                Instant.parse("2026-05-01T03:00:00Z"),
                Instant.parse("2026-06-01T03:00:00Z"),
                3,
                2,
                4,
                1,
                2,
                0,
                new BigDecimal("24.00"),
                new BigDecimal("255.00"),
                new BigDecimal("6.82")
        );
    }
}
