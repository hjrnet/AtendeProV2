package br.com.atendepro.modules.empresa.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class DocumentoEmpresaTest {

    @Test
    void deveNormalizarDocumento() {
        DocumentoEmpresa documento = DocumentoEmpresa.de("12.345.678/0001-90");

        assertThat(documento.valor()).isEqualTo("12345678000190");
    }

    @Test
    void naoDeveAceitarDocumentoCurto() {
        assertThatThrownBy(() -> DocumentoEmpresa.de("123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("documento da empresa deve ter entre 8 e 20 caracteres");
    }
}
