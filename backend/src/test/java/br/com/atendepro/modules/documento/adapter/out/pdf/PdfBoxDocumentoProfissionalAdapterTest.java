package br.com.atendepro.modules.documento.adapter.out.pdf;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import br.com.atendepro.modules.documento.domain.model.CarimboProfissional;
import br.com.atendepro.modules.documento.domain.model.ConselhoProfissional;
import br.com.atendepro.modules.documento.domain.model.DocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.StatusDocumentoProfissional;
import br.com.atendepro.modules.documento.domain.model.TipoDocumentoProfissional;

class PdfBoxDocumentoProfissionalAdapterTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");

    @Test
    void deveGerarPdfDoDocumentoProfissionalComCarimbo() {
        PdfBoxDocumentoProfissionalAdapter adapter = new PdfBoxDocumentoProfissionalAdapter();

        var result = adapter.gerarPdf(documento(), carimbo());

        assertThat(result.nomeArquivo()).isEqualTo("documento-profissional-declaracao-task-0603-202605250900.pdf");
        assertThat(result.contentType()).isEqualTo("application/pdf");
        assertThat(new String(result.conteudo(), 0, 4)).isEqualTo("%PDF");
        assertThat(result.conteudo().length).isGreaterThan(900);
    }

    private DocumentoProfissional documento() {
        return DocumentoProfissional.criar(
                EMPRESA_ID,
                null,
                UUID.randomUUID(),
                "Dra. Marina",
                "Declaracao TASK-0603",
                TipoDocumentoProfissional.DECLARACAO,
                "Paciente em acompanhamento profissional com orientacoes gerais registradas no AtendePro.",
                StatusDocumentoProfissional.EMITIDO,
                Instant.parse("2026-05-25T12:00:00Z")
        );
    }

    private CarimboProfissional carimbo() {
        return CarimboProfissional.criar(
                EMPRESA_ID,
                UUID.randomUUID(),
                "Dra. Marina",
                ConselhoProfissional.CRN,
                "SP",
                "CRN-0603",
                "Dra. Marina - CRN-0603",
                "AtendePro Clinica Demo",
                Instant.parse("2026-05-25T12:00:00Z")
        );
    }
}
