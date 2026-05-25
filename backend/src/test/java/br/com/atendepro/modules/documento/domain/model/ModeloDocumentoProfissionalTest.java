package br.com.atendepro.modules.documento.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class ModeloDocumentoProfissionalTest {

    @Test
    void deveIdentificarModeloGlobal() {
        ModeloDocumentoProfissional modelo = modelo(null);

        assertThat(modelo.global()).isTrue();
        assertThat(modelo.pertenceAEmpresa(UUID.randomUUID())).isTrue();
    }

    @Test
    void deveIdentificarModeloDaEmpresa() {
        UUID empresaId = UUID.randomUUID();
        ModeloDocumentoProfissional modelo = modelo(empresaId);

        assertThat(modelo.global()).isFalse();
        assertThat(modelo.pertenceAEmpresa(empresaId)).isTrue();
        assertThat(modelo.pertenceAEmpresa(UUID.randomUUID())).isFalse();
    }

    @Test
    void deveExigirConteudoPadrao() {
        assertThatThrownBy(() -> new ModeloDocumentoProfissional(
                UUID.randomUUID(),
                null,
                "Declaracao",
                null,
                TipoDocumentoProfissional.DECLARACAO,
                "Declaracao",
                " ",
                true,
                Instant.parse("2026-05-25T00:00:00Z"),
                Instant.parse("2026-05-25T00:00:00Z")
        )).hasMessage("conteudo padrao do modelo de documento profissional e obrigatorio");
    }

    private ModeloDocumentoProfissional modelo(UUID empresaId) {
        return new ModeloDocumentoProfissional(
                UUID.randomUUID(),
                empresaId,
                "Declaracao",
                "Modelo de declaracao",
                TipoDocumentoProfissional.DECLARACAO,
                "Declaracao profissional",
                "Declaramos para os devidos fins.",
                true,
                Instant.parse("2026-05-25T00:00:00Z"),
                Instant.parse("2026-05-25T00:00:00Z")
        );
    }
}
