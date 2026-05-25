package br.com.atendepro.modules.vertical.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import br.com.atendepro.shared.domain.exception.ValidationException;

class VerticalProfissionalTest {

    @Test
    void deveCriarVerticalProfissionalComListasImutaveis() {
        VerticalProfissional vertical = new VerticalProfissional(
                CodigoVerticalProfissional.NUTRI_PRO,
                "Nutri Pro",
                "R7",
                StatusVerticalProfissional.OPERACIONAL_BASE,
                "CRN",
                "Acompanhamento nutricional completo.",
                List.of("Nutricionistas"),
                List.of("plano alimentar", "diario alimentar"),
                List.of("Plano alimentar"),
                List.of("Plano alimentar PDF"),
                List.of("clientes/pacientes"),
                List.of("TASK-NUTRI-002")
        );

        assertThat(vertical.codigo()).isEqualTo(CodigoVerticalProfissional.NUTRI_PRO);
        assertThat(vertical.capacidades()).contains("plano alimentar", "diario alimentar");
        assertThatThrownBy(() -> vertical.capacidades().add("nova capacidade"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void naoDeveCriarVerticalSemNome() {
        assertThatThrownBy(() -> new VerticalProfissional(
                CodigoVerticalProfissional.NUTRI_PRO,
                " ",
                "R7",
                StatusVerticalProfissional.OPERACIONAL_BASE,
                "CRN",
                "Resumo",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        ))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Nome da vertical profissional e obrigatorio");
    }
}
