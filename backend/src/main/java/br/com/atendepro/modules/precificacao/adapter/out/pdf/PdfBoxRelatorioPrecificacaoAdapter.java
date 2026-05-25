package br.com.atendepro.modules.precificacao.adapter.out.pdf;

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

import br.com.atendepro.modules.precificacao.application.port.out.GerarRelatorioPrecificacaoPort;
import br.com.atendepro.modules.precificacao.application.result.RelatorioPrecificacaoResult;
import br.com.atendepro.modules.precificacao.domain.model.SimulacaoPrecificacao;
import br.com.atendepro.modules.precificacao.domain.model.StatusMargemPrecificacao;

@Component
@Profile("!test")
public class PdfBoxRelatorioPrecificacaoAdapter implements GerarRelatorioPrecificacaoPort {

    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZONE_ID);
    private static final DateTimeFormatter DATA_ARQUIVO = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(ZONE_ID);
    private static final Locale LOCALE_BR = Locale.forLanguageTag("pt-BR");

    @Override
    public RelatorioPrecificacaoResult gerarRelatorio(SimulacaoPrecificacao simulacao) {
        try (PDDocument documento = new PDDocument(); ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);

            try (PDPageContentStream conteudo = new PDPageContentStream(documento, pagina)) {
                escreverRelatorio(conteudo, simulacao);
            }

            documento.save(saida);
            return new RelatorioPrecificacaoResult(nomeArquivo(simulacao), CONTENT_TYPE_PDF, saida.toByteArray());
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel gerar o relatorio de precificacao.", ex);
        }
    }

    private void escreverRelatorio(PDPageContentStream conteudo, SimulacaoPrecificacao simulacao) throws IOException {
        float y = 790;
        y = escreverTexto(conteudo, "AtendePro", 48, y, 18, PDType1Font.HELVETICA_BOLD);
        y = escreverTexto(conteudo, "Relatorio de precificacao", 48, y - 4, 14, PDType1Font.HELVETICA_BOLD);
        y = escreverTexto(conteudo, "Gerado em " + DATA_HORA.format(simulacao.atualizadoEm()), 48, y - 4, 9, PDType1Font.HELVETICA);
        y = linha(conteudo, y - 12);

        y = escreverSecao(conteudo, "Resumo", y - 18);
        y = escreverCampo(conteudo, "Procedimento", simulacao.nomeProcedimento(), y);
        y = escreverCampo(conteudo, "Duracao", simulacao.duracaoMinutos() + " minutos", y);
        y = escreverCampo(conteudo, "Status da margem", rotuloStatus(simulacao.statusMargem()), y);
        y = escreverCampo(conteudo, "Criado em", DATA_HORA.format(simulacao.criadoEm()), y);

        y = escreverSecao(conteudo, "Valores principais", y - 12);
        y = escreverCampo(conteudo, "Custo total", moeda(simulacao.custoTotal()), y);
        y = escreverCampo(conteudo, "Preco minimo", moeda(simulacao.precoMinimo()), y);
        y = escreverCampo(conteudo, "Preco recomendado", moeda(simulacao.precoRecomendado()), y);
        y = escreverCampo(conteudo, "Preco praticado", moeda(simulacao.precoVenda()), y);
        y = escreverCampo(conteudo, "Lucro estimado", moeda(simulacao.lucroEstimado()), y);
        y = escreverCampo(conteudo, "Margem real", percentual(simulacao.margemRealPercentual()), y);
        y = escreverCampo(conteudo, "Margem desejada", percentual(simulacao.margemDesejadaPercentual()), y);

        y = escreverSecao(conteudo, "Composicao de custos", y - 12);
        y = escreverCampo(conteudo, "Insumos", moeda(simulacao.custoInsumos()), y);
        y = escreverCampo(conteudo, "Sala por hora", moeda(simulacao.custoSalaPorHora()), y);
        y = escreverCampo(conteudo, "Hora profissional", moeda(simulacao.valorHoraProfissional()), y);
        y = escreverCampo(conteudo, "Deslocamento", moeda(simulacao.custoDeslocamento()), y);
        y = escreverCampo(conteudo, "Alimentacao", moeda(simulacao.custoAlimentacao()), y);
        y = escreverCampo(conteudo, "Taxas", moeda(simulacao.taxas()), y);

        y = linha(conteudo, y - 8);
        escreverTexto(conteudo, "Documento gerado pelo AtendePro para apoio a decisao comercial.", 48, y - 18, 9, PDType1Font.HELVETICA);
    }

    private float escreverSecao(PDPageContentStream conteudo, String texto, float y) throws IOException {
        return escreverTexto(conteudo, texto, 48, y, 12, PDType1Font.HELVETICA_BOLD) - 6;
    }

    private float escreverCampo(PDPageContentStream conteudo, String rotulo, String valor, float y) throws IOException {
        escreverTexto(conteudo, rotulo + ":", 60, y, 10, PDType1Font.HELVETICA_BOLD);
        return escreverTexto(conteudo, valor, 210, y, 10, PDType1Font.HELVETICA) - 14;
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

    private String nomeArquivo(SimulacaoPrecificacao simulacao) {
        return "precificacao-" + slug(simulacao.nomeProcedimento()) + "-" + DATA_ARQUIVO.format(simulacao.atualizadoEm()) + ".pdf";
    }

    private String slug(String valor) {
        String texto = textoPdf(valor).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        texto = texto.replaceAll("(^-|-$)", "");
        return texto.isBlank() ? "simulacao" : texto;
    }

    private String moeda(BigDecimal valor) {
        return NumberFormat.getCurrencyInstance(LOCALE_BR).format(valor);
    }

    private String percentual(BigDecimal valor) {
        return NumberFormat.getNumberInstance(LOCALE_BR).format(valor) + "%";
    }

    private String rotuloStatus(StatusMargemPrecificacao status) {
        return switch (status) {
            case PREJUIZO -> "Prejuizo";
            case EQUILIBRIO -> "Equilibrio";
            case MARGEM_BAIXA -> "Margem baixa";
            case SAUDAVEL -> "Saudavel";
        };
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
