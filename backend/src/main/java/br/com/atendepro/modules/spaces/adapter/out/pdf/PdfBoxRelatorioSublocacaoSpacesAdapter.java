package br.com.atendepro.modules.spaces.adapter.out.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.com.atendepro.modules.spaces.application.port.out.GerarPdfRelatorioSublocacaoSpacesPort;
import br.com.atendepro.modules.spaces.application.result.IndicadoresSublocacaoSpacesResult;
import br.com.atendepro.modules.spaces.application.result.RelatorioSublocacaoSpacesResult;

@Component
@Profile("!test")
public class PdfBoxRelatorioSublocacaoSpacesAdapter implements GerarPdfRelatorioSublocacaoSpacesPort {

    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZONE_ID);
    private static final DateTimeFormatter DATA_ARQUIVO = DateTimeFormatter.ofPattern("yyyyMM").withZone(ZONE_ID);
    private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");

    @Override
    public RelatorioSublocacaoSpacesResult gerarPdf(IndicadoresSublocacaoSpacesResult indicadores) {
        try (PDDocument documento = new PDDocument(); ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {
                escreverRelatorio(conteudo, indicadores);
            }

            documento.save(saida);
            return new RelatorioSublocacaoSpacesResult(nomeArquivo(indicadores), CONTENT_TYPE_PDF, saida.toByteArray());
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel gerar o relatorio de sublocacao Spaces.", ex);
        }
    }

    private void escreverRelatorio(PDPageContentStream conteudo, IndicadoresSublocacaoSpacesResult indicadores) throws IOException {
        float y = 790;
        y = escreverTexto(conteudo, "AtendePro", 48, y, 18, PDType1Font.HELVETICA_BOLD);
        y = escreverTexto(conteudo, "Relatorio de sublocacao Spaces", 48, y - 4, 14, PDType1Font.HELVETICA_BOLD);
        y = escreverTexto(conteudo, "Periodo: " + DATA.format(indicadores.periodoInicio()) + " a " + DATA.format(indicadores.periodoFim()), 48, y - 4, 9, PDType1Font.HELVETICA);
        y = linha(conteudo, y - 12);

        y = escreverSecao(conteudo, "Recursos e pacotes", y - 18);
        y = escreverCampo(conteudo, "Total de recursos", Long.toString(indicadores.totalRecursos()), y);
        y = escreverCampo(conteudo, "Recursos ativos", Long.toString(indicadores.recursosAtivos()), y);
        y = escreverCampo(conteudo, "Pacotes ativos", Long.toString(indicadores.pacotesAtivos()), y);

        y = escreverSecao(conteudo, "Ocupacao do mes", y - 12);
        y = escreverCampo(conteudo, "Reservadas", Long.toString(indicadores.ocupacoesReservadas()), y);
        y = escreverCampo(conteudo, "Confirmadas", Long.toString(indicadores.ocupacoesConfirmadas()), y);
        y = escreverCampo(conteudo, "Canceladas", Long.toString(indicadores.ocupacoesCanceladas()), y);
        y = escreverCampo(conteudo, "Horas ocupadas", numero(indicadores.horasOcupadasMes()) + " h", y);
        y = escreverCampo(conteudo, "Taxa de ocupacao", numero(indicadores.taxaOcupacaoMesPercentual()) + "%", y);

        y = escreverSecao(conteudo, "Receita prevista", y - 12);
        y = escreverCampo(conteudo, "Receita fixa prevista", moeda(indicadores.receitaFixaPrevistaMes()), y);
        y = linha(conteudo, y - 8);
        escreverTexto(conteudo, "Documento gerado pelo AtendePro para acompanhamento operacional de sublocacao.", 48, y - 18, 9, PDType1Font.HELVETICA);
    }

    private float escreverSecao(PDPageContentStream conteudo, String texto, float y) throws IOException {
        return escreverTexto(conteudo, texto, 48, y, 12, PDType1Font.HELVETICA_BOLD) - 6;
    }

    private float escreverCampo(PDPageContentStream conteudo, String rotulo, String valor, float y) throws IOException {
        escreverTexto(conteudo, rotulo + ":", 60, y, 10, PDType1Font.HELVETICA_BOLD);
        return escreverTexto(conteudo, valor, 230, y, 10, PDType1Font.HELVETICA) - 14;
    }

    private float escreverTexto(
            PDPageContentStream conteudo,
            String texto,
            float x,
            float y,
            int tamanho,
            PDType1Font fonte
    ) throws IOException {
        conteudo.beginText();
        conteudo.setFont(fonte, tamanho);
        conteudo.newLineAtOffset(x, y);
        conteudo.showText(textoPdf(texto));
        conteudo.endText();
        return y - tamanho;
    }

    private float linha(PDPageContentStream conteudo, float y) throws IOException {
        conteudo.moveTo(48, y);
        conteudo.lineTo(548, y);
        conteudo.stroke();
        return y;
    }

    private String nomeArquivo(IndicadoresSublocacaoSpacesResult indicadores) {
        return "spaces-sublocacao-" + DATA_ARQUIVO.format(indicadores.periodoInicio()) + ".pdf";
    }

    private String moeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor);
    }

    private String numero(BigDecimal valor) {
        return NumberFormat.getNumberInstance(LOCALE_BR).format(valor);
    }

    private String textoPdf(String texto) {
        if (texto == null) {
            return "";
        }
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalizado
                .replaceAll("[^\\x20-\\x7E]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
