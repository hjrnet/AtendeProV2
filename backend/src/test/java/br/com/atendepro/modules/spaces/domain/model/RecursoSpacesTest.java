package br.com.atendepro.modules.spaces.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class RecursoSpacesTest {

    private static final UUID EMPRESA_ID = UUID.fromString("b6389f22-a874-420d-84d1-d8207ac786b9");
    private static final Instant AGORA = Instant.parse("2026-05-25T00:00:00Z");

    @Test
    void deveCadastrarRecursoAtivoComDadosNormalizados() {
        RecursoSpaces recurso = RecursoSpaces.cadastrar(
                EMPRESA_ID,
                " Sala premium ",
                TipoRecursoSpaces.SALA,
                " Sala com maca e lavatorio ",
                2,
                " Unidade Paulista ",
                AGORA
        );

        assertThat(recurso.id()).isNotNull();
        assertThat(recurso.empresaId()).isEqualTo(EMPRESA_ID);
        assertThat(recurso.nome()).isEqualTo("Sala premium");
        assertThat(recurso.descricao()).isEqualTo("Sala com maca e lavatorio");
        assertThat(recurso.localizacao()).isEqualTo("Unidade Paulista");
        assertThat(recurso.ativo()).isTrue();
        assertThat(recurso.criadoEm()).isEqualTo(AGORA);
    }

    @Test
    void naoDeveAceitarCapacidadeInvalida() {
        assertThatThrownBy(() -> RecursoSpaces.cadastrar(
                EMPRESA_ID,
                "Cabine",
                TipoRecursoSpaces.CABINE,
                null,
                0,
                null,
                AGORA
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("capacidade do recurso spaces deve ser positiva");
    }
}
