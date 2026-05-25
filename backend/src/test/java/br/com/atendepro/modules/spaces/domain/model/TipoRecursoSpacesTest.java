package br.com.atendepro.modules.spaces.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TipoRecursoSpacesTest {

    @Test
    void deveNormalizarCodigoDoTipoDeRecurso() {
        assertThat(TipoRecursoSpaces.deCodigo("sala")).isEqualTo(TipoRecursoSpaces.SALA);
        assertThat(TipoRecursoSpaces.deCodigo("cadeira")).isEqualTo(TipoRecursoSpaces.CADEIRA);
        assertThat(TipoRecursoSpaces.deCodigo("cabine")).isEqualTo(TipoRecursoSpaces.CABINE);
        assertThat(TipoRecursoSpaces.deCodigo("equipamento")).isEqualTo(TipoRecursoSpaces.EQUIPAMENTO);
    }

    @Test
    void naoDeveAceitarTipoInvalido() {
        assertThatThrownBy(() -> TipoRecursoSpaces.deCodigo("mesa"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tipo de recurso do spaces invalido");
    }
}
