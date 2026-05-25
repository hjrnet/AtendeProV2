package br.com.atendepro.modules.documento.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class DocumentoProfissionalTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final Instant AGORA = Instant.parse("2026-05-25T00:00:00Z");

    @Test
    void deveCriarDocumentoProfissionalComoRascunho() {
        DocumentoProfissional documento = DocumentoProfissional.criar(
                EMPRESA_ID,
                null,
                UUID.randomUUID(),
                " Dra. Marina ",
                " Declaracao de acompanhamento ",
                TipoDocumentoProfissional.DECLARACAO,
                " Conteudo clinico orientativo. ",
                null,
                AGORA
        );

        assertThat(documento.id()).isNotNull();
        assertThat(documento.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(documento.profissionalNome()).isEqualTo("Dra. Marina");
        assertThat(documento.titulo()).isEqualTo("Declaracao de acompanhamento");
        assertThat(documento.conteudo()).isEqualTo("Conteudo clinico orientativo.");
        assertThat(documento.status()).isEqualTo(StatusDocumentoProfissional.RASCUNHO);
        assertThat(documento.versao()).isEqualTo(1);
        assertThat(documento.ativo()).isTrue();
    }

    @Test
    void naoDeveCriarDocumentoSemConteudo() {
        assertThatThrownBy(() -> DocumentoProfissional.criar(
                EMPRESA_ID,
                null,
                null,
                "Dra. Marina",
                "Relatorio",
                TipoDocumentoProfissional.RELATORIO,
                " ",
                StatusDocumentoProfissional.RASCUNHO,
                AGORA
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("conteudo do documento profissional e obrigatorio");
    }
}
