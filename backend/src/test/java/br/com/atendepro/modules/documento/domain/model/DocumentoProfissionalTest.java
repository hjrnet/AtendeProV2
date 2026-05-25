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
        assertThat(documento.codigoValidacao()).isNotBlank();
        assertThat(documento.validacaoPublicaAtiva()).isTrue();
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

    @Test
    void deveSubstituirDocumentoIncrementandoVersao() {
        DocumentoProfissional documento = documento();

        DocumentoProfissional substituido = documento.substituir(
                "Relatorio atualizado",
                "Conteudo atualizado.",
                StatusDocumentoProfissional.EMITIDO,
                AGORA.plusSeconds(60)
        );

        assertThat(substituido.id()).isEqualTo(documento.id());
        assertThat(substituido.versao()).isEqualTo(2);
        assertThat(substituido.titulo()).isEqualTo("Relatorio atualizado");
        assertThat(substituido.conteudo()).isEqualTo("Conteudo atualizado.");
        assertThat(substituido.codigoValidacao()).isEqualTo(documento.codigoValidacao());
        assertThat(substituido.validacaoPublicaAtiva()).isTrue();
        assertThat(substituido.ativo()).isTrue();
    }

    @Test
    void deveCancelarDocumentoDesativandoValidacaoPublica() {
        DocumentoProfissional documento = documento();

        DocumentoProfissional cancelado = documento.cancelar(AGORA.plusSeconds(60));

        assertThat(cancelado.versao()).isEqualTo(2);
        assertThat(cancelado.status()).isEqualTo(StatusDocumentoProfissional.CANCELADO);
        assertThat(cancelado.validacaoPublicaAtiva()).isFalse();
        assertThat(cancelado.ativo()).isFalse();
    }

    private DocumentoProfissional documento() {
        return DocumentoProfissional.criar(
                EMPRESA_ID,
                null,
                UUID.randomUUID(),
                "Dra. Marina",
                "Declaracao de acompanhamento",
                TipoDocumentoProfissional.DECLARACAO,
                "Conteudo clinico orientativo.",
                StatusDocumentoProfissional.RASCUNHO,
                AGORA
        );
    }
}
