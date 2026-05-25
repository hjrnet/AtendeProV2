package br.com.atendepro.modules.documento.adapter.out.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.com.atendepro.modules.documento.application.port.out.GerarPdfDocumentoProfissionalPort;
import br.com.atendepro.modules.documento.application.result.DocumentoProfissionalPdfResult;
import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;

@Component
@Profile("!test")
public class PdfBoxDocumentoProfissionalAdapter implements GerarPdfDocumentoProfissionalPort {

    private static final String CONTENT_TYPE_PDF = "application/pdf";
    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZONE_ID);
    private static final DateTimeFormatter DATA_ARQUIVO = DateTimeFormatter.ofPattern("yyyyMMddHHmm").withZone(ZONE_ID);

    @Override
    public DocumentoProfissionalPdfResult gerarPdf(
            DocumentoProfissional documento,
            CarimboProfissional carimbo,
            String marcaDaguaAcademica
    ) {
        try (PDDocument pdf = new PDDocument(); ByteArrayOutputStream saida = new ByteArrayOutputStream()) {
            try (EscritorPdf escritor = new EscritorPdf(pdf, marcaDaguaAcademica)) {
                escreverDocumento(escritor, documento, carimbo, marcaDaguaAcademica);
            }
            pdf.save(saida);
            return new DocumentoProfissionalPdfResult(nomeArquivo(documento), CONTENT_TYPE_PDF, saida.toByteArray());
        } catch (IOException ex) {
            throw new IllegalStateException("Nao foi possivel gerar o PDF do documento profissional.", ex);
        }
    }

    private void escreverDocumento(
            EscritorPdf escritor,
            DocumentoProfissional documento,
            CarimboProfissional carimbo,
            String marcaDaguaAcademica
    ) throws IOException {
        escritor.texto("AtendePro", 18, PDType1Font.HELVETICA_BOLD);
        escritor.texto("Documento profissional", 14, PDType1Font.HELVETICA_BOLD);
        escritor.texto("Gerado em " + DATA_HORA.format(documento.atualizadoEm()), 9, PDType1Font.HELVETICA);
        if (marcaDaguaAcademica != null && !marcaDaguaAcademica.isBlank()) {
            escritor.texto(marcaDaguaAcademica, 11, PDType1Font.HELVETICA_BOLD);
        }
        escritor.linha();

        escritor.secao(documento.titulo());
        escritor.campo("Tipo", documento.tipo().name());
        escritor.campo("Status", documento.status().name());
        escritor.campo("Versao", Integer.toString(documento.versao()));
        escritor.campo("Profissional", documento.profissionalNome());
        if (documento.clientePacienteId() != null) {
            escritor.campo("Cliente/Paciente", documento.clientePacienteId().toString());
        }

        escritor.espaco();
        escritor.secao("Conteudo");
        escritor.paragrafos(documento.conteudo());

        escritor.espaco();
        if (carimbo != null) {
            escritor.linha();
            escritor.secao("Carimbo profissional");
            escritor.campo("Conselho", carimbo.conselho().name() + "/" + carimbo.uf());
            escritor.campo("Registro", carimbo.numeroRegistro());
            escritor.campo("Clinica", carimbo.clinicaNome());
            escritor.texto(carimbo.assinaturaTexto(), 10, PDType1Font.HELVETICA_BOLD);
        } else {
            escritor.linha();
            escritor.texto("Documento gerado sem carimbo profissional vinculado.", 9, PDType1Font.HELVETICA);
        }
        escritor.espaco();
        escritor.texto("Validacao publica: /api/documentos-profissionais/validacao/" + documento.codigoValidacao(), 9, PDType1Font.HELVETICA);
    }

    private String nomeArquivo(DocumentoProfissional documento) {
        return "documento-profissional-" + slug(documento.titulo()) + "-" + DATA_ARQUIVO.format(documento.atualizadoEm()) + ".pdf";
    }

    private String slug(String valor) {
        String texto = textoPdf(valor).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
        texto = texto.replaceAll("(^-|-$)", "");
        return texto.isBlank() ? "documento" : texto;
    }

    private static String textoPdf(String texto) {
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

    private static List<String> linhasQuebradas(String texto, int limite) {
        String normalizado = textoPdf(texto);
        if (normalizado.isBlank()) {
            return List.of("");
        }
        List<String> linhas = new ArrayList<>();
        StringBuilder atual = new StringBuilder();
        for (String palavra : normalizado.split(" ")) {
            if (atual.length() + palavra.length() + 1 > limite) {
                linhas.add(atual.toString());
                atual = new StringBuilder(palavra);
            } else {
                if (!atual.isEmpty()) {
                    atual.append(' ');
                }
                atual.append(palavra);
            }
        }
        if (!atual.isEmpty()) {
            linhas.add(atual.toString());
        }
        return linhas;
    }

    private static final class EscritorPdf implements AutoCloseable {

        private final PDDocument documento;
        private final String marcaDaguaAcademica;
        private PDPageContentStream conteudo;
        private float y;

        private EscritorPdf(PDDocument documento, String marcaDaguaAcademica) throws IOException {
            this.documento = documento;
            this.marcaDaguaAcademica = marcaDaguaAcademica;
            novaPagina();
        }

        private void texto(String texto, int tamanho, PDType1Font fonte) throws IOException {
            garantirEspaco(tamanho + 8);
            conteudo.beginText();
            conteudo.setFont(fonte, tamanho);
            conteudo.newLineAtOffset(48, y);
            conteudo.showText(textoPdf(texto));
            conteudo.endText();
            y -= tamanho + 6;
        }

        private void campo(String rotulo, String valor) throws IOException {
            garantirEspaco(18);
            conteudo.beginText();
            conteudo.setFont(PDType1Font.HELVETICA_BOLD, 10);
            conteudo.newLineAtOffset(60, y);
            conteudo.showText(textoPdf(rotulo + ":"));
            conteudo.endText();

            conteudo.beginText();
            conteudo.setFont(PDType1Font.HELVETICA, 10);
            conteudo.newLineAtOffset(190, y);
            conteudo.showText(textoPdf(valor));
            conteudo.endText();
            y -= 16;
        }

        private void secao(String texto) throws IOException {
            texto(texto, 12, PDType1Font.HELVETICA_BOLD);
        }

        private void paragrafos(String texto) throws IOException {
            for (String linha : linhasQuebradas(texto, 95)) {
                texto(linha, 10, PDType1Font.HELVETICA);
            }
        }

        private void linha() throws IOException {
            garantirEspaco(14);
            conteudo.moveTo(48, y);
            conteudo.lineTo(548, y);
            conteudo.stroke();
            y -= 18;
        }

        private void espaco() throws IOException {
            garantirEspaco(12);
            y -= 8;
        }

        private void garantirEspaco(float altura) throws IOException {
            if (y - altura < 56) {
                novaPagina();
            }
        }

        private void novaPagina() throws IOException {
            if (conteudo != null) {
                conteudo.close();
            }
            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);
            conteudo = new PDPageContentStream(documento, pagina);
            y = 790;
            escreverMarcaDagua();
        }

        private void escreverMarcaDagua() throws IOException {
            if (marcaDaguaAcademica == null || marcaDaguaAcademica.isBlank()) {
                return;
            }
            conteudo.saveGraphicsState();
            conteudo.setNonStrokingColor(220, 220, 220);
            conteudo.beginText();
            conteudo.setFont(PDType1Font.HELVETICA_BOLD, 32);
            conteudo.setTextMatrix(Matrix.getRotateInstance(Math.toRadians(35), 96, 290));
            conteudo.showText(textoPdf(marcaDaguaAcademica));
            conteudo.endText();
            conteudo.restoreGraphicsState();
            conteudo.setNonStrokingColor(0, 0, 0);
        }

        @Override
        public void close() throws IOException {
            if (conteudo != null) {
                conteudo.close();
            }
        }
    }
}
